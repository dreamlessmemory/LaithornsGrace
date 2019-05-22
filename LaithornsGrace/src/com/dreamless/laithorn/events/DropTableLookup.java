package com.dreamless.laithorn.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.api.FragmentRarity;
import com.dreamless.laithorn.api.ItemCrafting;
import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTItem;

public class DropTableLookup {

	public enum DropType {
		MOB, BLOCK;
	}



	private static HashMap<EntityType, DropTableEntry> mobDropTables = new HashMap<EntityType, DropTableEntry>();
	private static HashMap<Material, DropTableEntry> blockDropTables = new HashMap<Material, DropTableEntry>();
	private static HashMap<String, ArrayList<LootPool>> tagDropTables = new HashMap<String, ArrayList<LootPool>>();

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

			double dropChance = currentEntry.getDouble("chance", 100) / 100;
			String baseType = currentEntry.getString("base", "RAW");
			HashMap<String, Double> tags = new HashMap<String, Double>();

			for (String tag : currentEntry.getKeys(false)) {
				if (tag.equalsIgnoreCase("chance") || tag.equalsIgnoreCase("base"))
					continue;
				tags.put(tag, currentEntry.getDouble(tag, 0) / 100);
			}

			switch (type) {
			case MOB:
				if (EntityType.valueOf(entry) != null)
					mobDropTables.put(EntityType.valueOf(entry), new DropTableEntry(dropChance, baseType, tags));
				break;
			case BLOCK:
				if (Material.valueOf(entry) != null)
					blockDropTables.put(Material.valueOf(entry), new DropTableEntry(dropChance, baseType, tags));
				break;
			}
		}
	}

	public static void loadTagTables(FileConfiguration fileConfiguration) {
		tagDropTables.clear();
		for (String entry : fileConfiguration.getKeys(false)) {
			ConfigurationSection itemConfig = fileConfiguration.getConfigurationSection(entry);
			ArrayList<LootPool> lootPool = new ArrayList<LootPool>();
			for (String item : itemConfig.getKeys(false)) {
				ConfigurationSection currentEntry = fileConfiguration.getConfigurationSection(entry + "." + item);
				double dropChance = currentEntry.getDouble("chance", 0) / 100;
				int min = currentEntry.getInt("min", 1);
				int max = currentEntry.getInt("max", 1);
				LootPool pool = new LootPool(item, min, max, dropChance);
				lootPool.add(pool);
				PlayerMessager.debugLog(entry + " " + pool);
			}
			tagDropTables.put(entry, lootPool);
		}
	}

	public static final List<ItemStack> dropItems(ItemStack item) {
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
			for (int roll = 0; roll < item.getAmount(); roll++) {
				drops.addAll(rollPool(keyword, level));
			}
		}
		return drops;
	}

	public final static List<ItemStack> rollPool(String keyword, String rarity) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		ArrayList<LootPool> poolClass = tagDropTables.get(keyword);
		if (poolClass != null) {
			PlayerMessager.debugLog("Rolling for keyword: " + keyword);
			Iterator<LootPool> pools = poolClass.iterator();

			while (pools.hasNext()) {
				LootPool pool = pools.next();
				PlayerMessager.debugLog("Rolling for item: " + pool.getItem());
				double chance = (double) pool.getChance() * FragmentRarity.valueOf(rarity).rarityModifier();
				if (Math.random() <= chance) {// succesfull roll
					PlayerMessager.debugLog("Succesfull roll for: " + pool.getItem());
					ItemStack drop = new ItemStack(Material.getMaterial(pool.getItem()));
					if (pool.getMax() > 1) {
						drop.setAmount(new Random().nextInt(pool.getMax() - pool.getMin() + 1) + pool.min);
					}
					drops.add(drop);
				}
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

		String level = laithorn.getString("level");
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add(laithorn.getString("type"));

		for (String key : laithorn.getKeys()) {
			if (key.contains("Loot_"))
				keywords.add(laithorn.getString(key));
		}

		int total = 0;

		// Add base exp
		if (PlayerExperienceVariables.experienceValues.containsKey(level))
			total += PlayerExperienceVariables.experienceValues.get(level);

		// Add tags

		for (String keyword : keywords) {
			if (PlayerExperienceVariables.experienceValues.containsKey(keyword))
				total += PlayerExperienceVariables.experienceValues.get(keyword);
		}

		return total * item.getAmount();
	}

	public static final boolean containsDropTable(EntityType type) {
		return mobDropTables.containsKey(type);
	}

	public static final boolean containsDropTable(Material type) {
		return blockDropTables.containsKey(type);
	}

	public static final ItemStack dropMobItems(LivingEntity entity) {
		Player killer = entity.getKiller();
		PlayerData data = CacheHandler.getPlayer(killer);

		if (data != null) {
			try {
				DropTableEntry entry = mobDropTables.get(entity.getType());
				if (entry == null) {
					PlayerMessager.debugLog("No data for mob drop");
					return null;
				}

				// Roll for chance to drop
				if (Math.random() <= entry.getDropChance()) { // Successful roll
					ArrayList<String> tags = new ArrayList<String>();
					// Roll for each drop
					for (java.util.Map.Entry<String, Double> drop : entry.getTags().entrySet()) {
						if (Math.random() <= drop.getValue()) {
							tags.add(drop.getKey());
						}
					}

					return ItemCrafting.fragmentItem(getLevel(data), entry.getBaseType(), tags);
				}

			} catch (JsonSyntaxException | JsonIOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static ItemStack dropBlockItems(Material material, Player player) {
		PlayerData data = CacheHandler.getPlayer(player);

		if (data != null) {
			try {
				DropTableEntry entry = blockDropTables.get(material);
				if (entry == null) {
					PlayerMessager.debugLog("No data for block drop");
					return null;
				}

				// Roll for chance to drop
				if (Math.random() <= entry.getDropChance()) { // Successful roll
					ArrayList<String> tags = new ArrayList<String>();
					// Roll for each drop
					for (java.util.Map.Entry<String, Double> drop : entry.getTags().entrySet()) {
						if (Math.random() <= drop.getValue()) {
							tags.add(drop.getKey());
						}
					}

					return ItemCrafting.fragmentItem(getLevel(data), entry.getBaseType(), tags);
				}

			} catch (JsonSyntaxException | JsonIOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static FragmentRarity getLevel(PlayerData data) {
		return new WeightedRandomBag(data.getAttunementLevel()).getRandom();
	}

	private static class WeightedRandomBag {
		private class Entry {
			double weightedChance;
			FragmentRarity rarity;
			
			public Entry(FragmentRarity rarity, double accumlatedWeight) {
				this.rarity = rarity;
				this.weightedChance = accumlatedWeight;
			}
		}
		private List<Entry> entries = new ArrayList<>();
		private double accumulatedWeight;
		private Random rand = new Random();
		
		WeightedRandomBag(int level) {
			for(FragmentRarity rarity : FragmentRarity.values()) {
				entries.add(new Entry(rarity, rarity.weightedDropChance(level)));
			}
		}
		
		public FragmentRarity getRandom() {
			double r = rand.nextDouble() * accumulatedWeight;

			for (Entry entry : entries) {
				if (entry.weightedChance >= r) {
					return entry.rarity;
				}
			}
			return null; // should only happen when there are no entries
		}
	}
}
