package com.dreamless.laithorn.api;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.dreamless.laithorn.LaithornUtils;
import com.dreamless.laithorn.LanguageReader;

import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTItem;

public class Fragment {
	private static Material FRAGMENT_MATERIAL = Material.FLINT;
	private static String NBT_TOP_LEVEL_TAG = "Laithorn";

	public static final Material getFragmentMaterial() {
		return FRAGMENT_MATERIAL;
	}

	public static final void setFragmentMaterial(Material newFragmentMaterial) {
		FRAGMENT_MATERIAL = newFragmentMaterial;
	}

	public static ItemStack fragmentItem(FragmentRarity rarity, String type, List<String> flags) {
		ItemStack item = new ItemStack(FRAGMENT_MATERIAL);

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

		NBTCompound laithorn = nbti.addCompound(NBT_TOP_LEVEL_TAG);
		laithorn.setString("module", "core");
		laithorn.setString("level", rarity.toString());
		laithorn.setString("type", type);
		for (int i = 0; i < flags.size(); i++) {
			laithorn.setString("Loot_" + i, flags.get(i).toUpperCase());
		}

		item = nbti.getItem();

		return item;
	}

	public static ItemStack fragmentItem(FragmentRarity rarity, String type) {
		ItemStack item = new ItemStack(FRAGMENT_MATERIAL);

		/*** Item Meta ***/
		ItemMeta itemMeta = item.getItemMeta();

		// Set Name
		String displayName = LanguageReader.getText(rarity + "_title") + " " + LanguageReader.getText(type + "_title")
				+ " " + LanguageReader.getText("Title");
		itemMeta.setDisplayName(displayName);

		// Set flavor text
		// Level and base type.
		String loreText = LanguageReader.getText(rarity + "_desc") + " " + LanguageReader.getText(type + "_desc");

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

		NBTCompound laithorn = nbti.addCompound(NBT_TOP_LEVEL_TAG);
		laithorn.setString("module", "core");
		laithorn.setString("level", rarity.toString());
		laithorn.setString("type", type);

		item = nbti.getItem();

		return item;
	}

	public static boolean isEssence(ItemStack item) {
		if (item == null || item.getType() != FRAGMENT_MATERIAL)
			return false;
		return new NBTItem(item).hasKey(NBT_TOP_LEVEL_TAG);
	}

	public static boolean isLaithornEnchanted(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return false;
		return new NBTItem(item).hasKey(NBT_TOP_LEVEL_TAG);
	}

	public static final String getTopLevelTag() {
		return NBT_TOP_LEVEL_TAG;
	}

	public static final void setTopLevelTag(String topLevelTag) {
		NBT_TOP_LEVEL_TAG = topLevelTag;
	}

}
