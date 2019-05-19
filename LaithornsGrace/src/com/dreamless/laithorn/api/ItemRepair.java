package com.dreamless.laithorn.api;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.PlayerMessager;
import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTItem;

public class ItemRepair {
	
	public static int getRepairValue(ItemStack essence, int baseRepairValue, List<String> bonusTags) {
		
		// NBT Setup 
		NBTItem nbti = new NBTItem(essence);
		NBTCompound laithorn = nbti.getCompound("Laithorn");
		if (laithorn == null) {
			PlayerMessager.debugLog("Cannot Repair: Item is not a fragment");
			return 0;
		}
		
		int bonus = 0;
		
		String level = laithorn.getString("level");
		
		for (String key : laithorn.getKeys()) {
			if (key.contains("Loot_") && bonusTags.contains(laithorn.getString(key))) {
				bonus += Math.max(baseRepairValue/10, 1);
			}
		}
		return (int) ((baseRepairValue + bonus) * FragmentRarity.valueOf(level).rarityModifier());
	}
}
