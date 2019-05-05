package com.dreamless.laithorn.events;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

import com.dreamless.laithorn.CustomRecipes;
import com.dreamless.laithorn.LaithornUtils;
import com.dreamless.laithorn.LaithornsGrace;
import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.events.WellDropTableEntry.LootPool;
import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class DropTableLookup {

	public static enum DropType {
		MOB, BLOCK;
	}

	public static double DULL_BASE_CHANCE = 90;
	public static double FAINT_BASE_CHANCE = 5;
	public static double PALE_BASE_CHANCE = 3;
	public static double GLOWING_BASE_CHANCE = 1;
	public static double SPARKLING_BASE_CHANCE = 0.75;
	public static double BRIGHT_BASE_CHANCE = 0.5;
	public static double SHINING_BASE_CHANCE = 0.1;
	public static double RADIANT_BASE_CHANCE = 0.05;
	public static double INCANDESCENT_BASE_CHANCE = 0.02;

	public static double DULL_GROWTH_RATE = -0.7143;
	public static double FAINT_GROWTH_RATE = 0.1224;
	public static double PALE_GROWTH_RATE = 0.1224;
	public static double GLOWING_GROWTH_RATE = 0.1224;
	public static double SPARKLING_GROWTH_RATE = 0.1046;
	public static double BRIGHT_GROWTH_RATE = 0.0867;
	public static double SHINING_GROWTH_RATE = 0.05;
	public static double RADIANT_GROWTH_RATE = 0.0505;
	public static double INCANDESCENT_GROWTH_RATE = 0.0508;

	private static HashMap<EntityType, DropTableEntry> mobDropTables = new HashMap<EntityType, DropTableEntry>();
	private static HashMap<Material, DropTableEntry> blockDropTables = new HashMap<Material, DropTableEntry>();

	public static void loadDropTables(FileConfiguration fileConfiguration, DropType type) {

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

	public static List<ItemStack> dropItems(List<String> keywords, String rarity) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		for (String keyword : keywords) {
			drops.addAll(rollPool(keyword, rarity));
		}
		return drops;
	}

	public static List<ItemStack> rollPool(String keyword, String rarity) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		try {
			WellDropTableEntry entry = LaithornUtils.gson.fromJson(new FileReader(
					new File(LaithornsGrace.grace.getDataFolder(), "\\drops\\" + keyword.toLowerCase() + ".json")),
					WellDropTableEntry.class);
			if (entry == null) {
				PlayerMessager.debugLog("NULL?!");
			}

			Iterator<LootPool> pools = entry.pools.iterator();

			while (pools.hasNext()) {
				LootPool pool = pools.next();
				double chance = (double) pool.getChance() * rarityModifier(rarity) / 100.0;
				if (Math.random() <= chance) {// succesfull roll
					ItemStack drop = new ItemStack(Material.getMaterial(pool.getItem()));
					if (pool.getMax() > 1) {
						drop.setAmount(new Random().nextInt(pool.getMax() - pool.getMin() + 1) + pool.min);
					}
					drops.add(drop);
				}
			}
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}

		return drops;
	}

	public static double rarityModifier(String rarity) {
		switch (rarity) {
		case "DULL":
			return 1.0;
		default:
			return 1.0;
		}

	}

	public static int calculateEXPValue(List<String> keywords, String rarity) {
		int total = 0;

		// Add base exp
		if (PlayerExperienceVariables.experienceValues.containsKey(rarity))
			total += PlayerExperienceVariables.experienceValues.get(rarity);

		// Add tags

		for (String keyword : keywords) {
			if (PlayerExperienceVariables.experienceValues.containsKey(keyword))
				total += PlayerExperienceVariables.experienceValues.get(keyword);
		}

		return total;
	}

	public static boolean containsDropTable(EntityType type) {
		return mobDropTables.containsKey(type);
	}

	public static boolean containsDropTable(Material type) {
		return blockDropTables.containsKey(type);
	}

	public static ItemStack dropMobItems(LivingEntity entity) {
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

					return CustomRecipes.fragmentItem(getLevel(data), entry.getBaseType(), tags);
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

					return CustomRecipes.fragmentItem(getLevel(data), entry.getBaseType(), tags);
				}

			} catch (JsonSyntaxException | JsonIOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static String getLevel(PlayerData data) {
		WeightedRandomBag bag = new WeightedRandomBag();
		int level = data.getAttunementLevel();

		// DULL
		bag.addEntry("DULL", DULL_GROWTH_RATE * (level - 1) + DULL_BASE_CHANCE);

		// FAINT
		bag.addEntry("FAINT", FAINT_GROWTH_RATE * (level - 1) + FAINT_BASE_CHANCE);

		// PALE
		bag.addEntry("PALE", PALE_GROWTH_RATE * (level - 1) + PALE_BASE_CHANCE);

		// FAINT
		bag.addEntry("FAINT", FAINT_GROWTH_RATE * (level - 1) + FAINT_BASE_CHANCE);

		// GLOWING
		bag.addEntry("GLOWING", GLOWING_GROWTH_RATE * (level - 1) + GLOWING_BASE_CHANCE);

		// SPARKLING
		bag.addEntry("SPARKLING", SPARKLING_GROWTH_RATE * (level - 1) + SPARKLING_BASE_CHANCE);

		// BRIGHT
		bag.addEntry("BRIGHT", BRIGHT_GROWTH_RATE * (level - 1) + BRIGHT_BASE_CHANCE);

		// SHINING
		bag.addEntry("SHINING", SHINING_GROWTH_RATE * (level - 1) + SHINING_BASE_CHANCE);

		// RADIANT
		bag.addEntry("RADIANT", RADIANT_GROWTH_RATE * (level - 1) + RADIANT_BASE_CHANCE);

		// INCANDESCENT
		bag.addEntry("INCANDESCENT", INCANDESCENT_GROWTH_RATE * (level - 1) + INCANDESCENT_BASE_CHANCE);

		return bag.getRandom();
	}

	private static class WeightedRandomBag {

		private class Entry {
			double accumulatedWeight;
			String level;
		}

		private List<Entry> entries = new ArrayList<>();
		private double accumulatedWeight;
		private Random rand = new Random();

		public void addEntry(String level, double weight) {
			accumulatedWeight += weight;
			Entry e = new Entry();
			e.level = level;
			e.accumulatedWeight = accumulatedWeight;
			entries.add(e);
		}

		public String getRandom() {
			double r = rand.nextDouble() * accumulatedWeight;

			for (Entry entry : entries) {
				if (entry.accumulatedWeight >= r) {
					return entry.level;
				}
			}
			return null; // should only happen when there are no entries
		}
	}
}
