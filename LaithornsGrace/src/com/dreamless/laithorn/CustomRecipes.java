package com.dreamless.laithorn;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTItem;

public class CustomRecipes {
	
	public static ItemStack fragmentItem(String level, String type, List<String>flags) {
		ItemStack item = new ItemStack(LaithornsGrace.FRAGMENT_MATERIAL);
		
		/*** Item Meta ***/
		ItemMeta itemMeta = item.getItemMeta();
		
		// Set Name
		String displayName = LanguageReader.getText(level + "_title") 
				+ " "
				+ LanguageReader.getText(type + "_title") 
				+ " "
				+ LanguageReader.getText("Title");
		itemMeta.setDisplayName(displayName);
		
		// Set flavor text
		// Level and base type. 
		String loreText = LanguageReader.getText(level + "_desc") 
				+ " "
				+ LanguageReader.getText(type + "_desc");
		for(String flag: flags) {
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
		
		//nbti.addCompound("Laithorn");
		NBTCompound laithorn = nbti.addCompound("Laithorn");
		laithorn.setString("level", level);
		laithorn.setString("type", type);
		for(int i = 0; i < flags.size(); i++) {
			laithorn.setString("Loot_" + i, flags.get(i).toUpperCase());
		}
		
		item = nbti.getItem();
		
		return item;
	}
}
