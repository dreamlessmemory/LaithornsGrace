package com.dreamless.laithorn.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.dreamless.laithorn.LaithornUtils;
import com.dreamless.laithorn.LanguageReader;

import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTItem;

public class ItemCrafting {

	public static Material fragmentMaterial = Material.FLINT;

	public static final Material getFragmentMaterial() {
		return fragmentMaterial;
	}

	public static final void setFragmentMaterial(Material newFragmentMaterial) {
		fragmentMaterial = newFragmentMaterial;
	}

	public static ItemStack fragmentItem(FragmentRarity rarity, String type, List<String> flags) {
		ItemStack item = new ItemStack(fragmentMaterial);

		/*** Item Meta ***/
		ItemMeta itemMeta = item.getItemMeta();

		// Set Name
		String displayName = LanguageReader.getText(rarity + "_title") + " " + LanguageReader.getText(type + "_title")
				+ " " + LanguageReader.getText("Title");
		itemMeta.setDisplayName(displayName);

		// Set flavor text
		// Level and base type.
		String loreText = LanguageReader.getText(rarity + "_desc") + " " + LanguageReader.getText(type + "_desc");
		for (String flag : flags) {
			loreText += " " + LanguageReader.getText(flag + "_desc");
		}

		ArrayList<String> loreTextArrayList = LaithornUtils.wrapText(loreText);
		loreTextArrayList.add("");
		loreTextArrayList.addAll(LaithornUtils.wrapText(LanguageReader.getText("Instruction")));

		itemMeta.setLore(loreTextArrayList);

		// Set cosmetic enchantment
		itemMeta.addEnchant(Enchantment.MENDING, 1, true);

		// Apply meta
		item.setItemMeta(itemMeta);

		/*** NBT ***/
		NBTItem nbti = new NBTItem(item);

		// nbti.addCompound("Laithorn");
		NBTCompound laithorn = nbti.addCompound("Laithorn");
		laithorn.setString("level", rarity.toString());
		laithorn.setString("type", type);
		for (int i = 0; i < flags.size(); i++) {
			laithorn.setString("Loot_" + i, flags.get(i).toUpperCase());
		}

		item = nbti.getItem();

		return item;
	}

	public static boolean checkForCorrectFragment(CraftingInventory inventory, FragmentRarity minimumRarityLevel) {
		ItemStack[] matrix = inventory.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			ItemStack itemStack = matrix[i];
			if (itemStack == null)
				continue;
			if (itemStack.getType() == fragmentMaterial) {
				NBTItem nbti = new NBTItem(itemStack);
				NBTCompound laithorn = nbti.getCompound("Laithorn");
				if (laithorn == null)
					return false;
				for (String key : laithorn.getKeys()) {
					if (FragmentRarity.valueOf(laithorn.getString(key)).meetsMinimum(minimumRarityLevel))
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean surroundedByEssence(CraftingInventory inventory) {
		ItemStack[] matrix = inventory.getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			if (i == 4)
				continue; // ignore the center
			if (!isEssence(matrix[i]))
				return false;
		}
		return true;
	}

	public static boolean isEssence(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return false;
		NBTItem nbti = new NBTItem(item);
		return nbti.hasKey("Laithorn");
	}
}
