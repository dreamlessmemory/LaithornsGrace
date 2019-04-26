package com.dreamless.laithorn.events;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.LaithornUtils;
import com.dreamless.laithorn.LaithornsGrace;
import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.events.DropTableEntry.LootPool;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class DropTableLookup {

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
			DropTableEntry entry = LaithornUtils.gson.fromJson(new FileReader(
					new File(LaithornsGrace.grace.getDataFolder(), "\\drops\\" + keyword.toLowerCase() + ".json")),
					DropTableEntry.class);
			if (entry == null) {
				PlayerMessager.debugLog("NULL?!");
			}

			Iterator<LootPool> pools = entry.pools.iterator();

			while (pools.hasNext()) {
				LootPool pool = pools.next();
				double chance = (double) pool.getChance() * rarityModifier(rarity) / 100.0;
				if (Math.random() <= chance) {// succesfull roll
					ItemStack drop = new ItemStack(Material.getMaterial(pool.getItem()));
					drop.setAmount(new Random().nextInt(pool.getMax() - pool.getMin() + 1) + pool.min);
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
		total += PlayerExperienceVariables.experienceValues.get(rarity);
		
		// Add tags
		
		for(String keyword : keywords) {
			total += PlayerExperienceVariables.experienceValues.get(keyword);
		}
		
		return total;
	}
	
}
