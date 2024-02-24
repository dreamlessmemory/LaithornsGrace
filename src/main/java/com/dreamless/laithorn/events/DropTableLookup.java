package com.dreamless.laithorn.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.api.Fragment;
import com.dreamless.laithorn.api.FragmentRarity;
import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;

public class DropTableLookup {

	public enum DropType {
		MOB, BLOCK;
	}

	private static HashMap<EntityType, DropTableEntry> mobDropTables = new HashMap<EntityType, DropTableEntry>();
	private static HashMap<Material, DropTableEntry> blockDropTables = new HashMap<Material, DropTableEntry>();
	private static HashMap<String, HashMap<String, LootPool>> tagDropTables = new HashMap<String, HashMap<String, LootPool>>();
	private static final Random RANDOM = new Random();

	public static void loadDropTables(FileConfiguration fileConfiguration, DropType type) {
		// Wipe tables
		switch (type) {
			case MOB:
				mobDropTables.clear();
				break;
			case BLOCK:
				blockDropTables.clear();
				break;
		}

		for (String entry : fileConfiguration.getKeys(false)) {
			ConfigurationSection currentEntry = fileConfiguration.getConfigurationSection(entry);

			double dropChance = currentEntry.getDouble("chance", 0) / 100;

			ConfigurationSection dropChances = currentEntry.getConfigurationSection("pool");
			HashMap<String, Double> tags = new HashMap<String, Double>();

			for (String tag : dropChances.getKeys(false)) {
				tags.put(tag, dropChances.getDouble(tag, 0) / 100);
			}

			switch (type) {
				case MOB:
					if (EntityType.valueOf(entry) != null)
						mobDropTables.put(EntityType.valueOf(entry), new DropTableEntry(dropChance, tags));
					break;
				case BLOCK:
					if (Material.valueOf(entry) != null)
						blockDropTables.put(Material.valueOf(entry), new DropTableEntry(dropChance, tags));
					break;
			}
		}
	}

	public static void loadTagTables(FileConfiguration fileConfiguration) {
		tagDropTables.clear();
		for (String entry : fileConfiguration.getKeys(false)) {
			ConfigurationSection itemConfig = fileConfiguration.getConfigurationSection(entry);
			HashMap<String, LootPool> lootPool = new HashMap<String, LootPool>();
			for (String item : itemConfig.getKeys(false)) {
				ConfigurationSection currentEntry = fileConfiguration.getConfigurationSection(entry + "." + item);
				double dropChance = currentEntry.getDouble("chance", 0) / 100;
				int min = currentEntry.getInt("min", 1);
				int max = currentEntry.getInt("max", 1);
				LootPool pool = new LootPool(item, min, max, dropChance);
				lootPool.put(item, pool);
				PlayerMessager.debugLog(entry + " " + pool);
			}
			tagDropTables.put(entry, lootPool);
		}
	}

	protected static final List<ItemStack> dropItems(ItemStack item) {

		// Air/null check
		if (item == null || item.getType() == Material.AIR) {
			PlayerMessager.debugLog("Item is air or null");
			return null;
		}

		NBTItem nbti = new NBTItem(item);
		NBTCompound laithorn = nbti.getCompound("Laithorn");
		if (laithorn == null) {
			PlayerMessager.debugLog("Item is not a fragment");
			return null;
		}

		String level = laithorn.getString("level");
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add(laithorn.getString("type"));

		for (String key : laithorn.getKeys()) {
			if (key.contains("Loot_"))
				keywords.add(laithorn.getString(key));
		}
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		for (String keyword : keywords) {
			drops.addAll(rollPool(keyword, level, item.getAmount()));
		}
		return drops;
	}

	private final static List<ItemStack> rollPool(String keyword, String rarity, int times) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		HashMap<String, LootPool> poolClass = tagDropTables.get(keyword);
		if (poolClass != null) {
			PlayerMessager.debugLog("Rolling for keyword: " + keyword);
			Iterator<Entry<String, LootPool>> pools = poolClass.entrySet().iterator();

			HashMap<String, Double> weightedChances = new HashMap<String, Double>();
			while (pools.hasNext()) {
				Entry<String, LootPool> pool = pools.next();
				double rawChance = pool.getValue().getChance();
				double finalChance = rawChance < 1.0 ? rawChance * FragmentRarity.valueOf(rarity).rarityModifier()
						: rawChance;
				weightedChances.put(pool.getKey(), finalChance);
				PlayerMessager.debugLog("Added Weight for item: " + pool.getKey() + " @ " + finalChance);
			}

			String resultString;
			for (int i = 0; i < times; i++) {
				resultString = new WeightedRandom<String>(RANDOM, weightedChances).rollValue();
				ItemStack drop = new ItemStack(Material.getMaterial(resultString));
				PlayerMessager.debugLog("Rolled a " + resultString);
				LootPool itemPool = poolClass.get(resultString);
				if (itemPool.getMax() > 1) {
					int minAmount = itemPool.getMin() + FragmentRarity.valueOf(rarity).rarityDropQuantityMinBonus();
					int maxAmount = itemPool.getMax() + FragmentRarity.valueOf(rarity).rarityDropQuantityMaxBonus();
					int randomAmount = ThreadLocalRandom.current().nextInt(minAmount, maxAmount + 1);
					PlayerMessager.debugLog("Min: " + minAmount + " Max: " + maxAmount);
					drop.setAmount(randomAmount);
					PlayerMessager.debugLog("Changed drop amount to: " + drop.getAmount());
				}
				drops.add(drop);
			}
		}

		return drops;
	}

	public static final int calculateEXPValue(ItemStack item) {
		NBTItem nbti = new NBTItem(item);
		NBTCompound laithorn = nbti.getCompound("Laithorn");
		if (laithorn == null) {
			PlayerMessager.debugLog("Item is not a fragment");
			return 0;
		}
		return PlayerExperienceVariables.getFragmentExp() * item.getAmount();
	}

	public static final boolean containsDropTable(EntityType type) {
		return mobDropTables.containsKey(type);
	}

	public static final boolean containsDropTable(Material type) {
		return blockDropTables.containsKey(type);
	}

	public static final ItemStack dropMobItems(LivingEntity entity) {
		Player killer = entity.getKiller();
		DropTableEntry entry = mobDropTables.get(entity.getType());
		if (entry == null) {
			PlayerMessager.debugLog("No data for mob drop");
			return null;
		}
		return rollDropTable(entry, killer);
	}

	public static ItemStack dropBlockItems(Material material, Player player) {
		DropTableEntry entry = blockDropTables.get(material);
		if (entry == null) {
			PlayerMessager.debugLog("No data for block drop");
			return null;
		}
		return rollDropTable(entry, player);
	}

	private static ItemStack rollDropTable(DropTableEntry entry, Player player) {
		PlayerData data = CacheHandler.getPlayer(player);
		int boostsRemaining = data.getBoostedFragments();
		String result = entry.rollForTag(player, RANDOM, boostsRemaining > 0);

		// If the result fails, then just return nothing
		if (result == null) {
			return null;
		}

		// Adjust boost if needed
		if (boostsRemaining > 0) {
			data.setBoostedFragments(boostsRemaining--);
			if (data.getFlag(PlayerData.BONUS_MESSAGE_FLAG) && boostsRemaining == 0) {
				PlayerMessager.msg(player, "Your empowered connection with Laithorn has faded.");
			}
		}
		return Fragment.fragmentItem(getLevel(data), result);
	}

	private static FragmentRarity getLevel(PlayerData data) {
		return new WeightedRandom<FragmentRarity>(RANDOM, data.getAttunementLevel()).rollValue();
	}
}
