package com.dreamless.laithorn;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTItem;

public class CustomRecipes {
	
	public static ItemStack fragmentItem(String...types) {
		ItemStack item = new ItemStack(Material.FLINT);
		
		/*** Item Meta ***/
		ItemMeta itemMeta = item.getItemMeta();
		
		String type = "";
		
		if(types.length == 0)
			type = type.concat("_DULL");
		else {
			for(String parameter : types) {
				type = type.concat("_"+parameter);
			}
		}
		
		// Set Name
		itemMeta.setDisplayName(LanguageReader.getText("Essence_Item_Name" + type));
		
		// Set flavor text
		itemMeta.setLore(LaithornUtils.wrapText(LanguageReader.getText("Essence_Item_Text" + type)));
		
		// Set cosmetic enchantment
		itemMeta.addEnchant(Enchantment.MENDING, 1, true);
		
		// Apply meta
		item.setItemMeta(itemMeta);
		
		/*** NBT ***/
		NBTItem nbti = new NBTItem(item);
		
		//nbti.addCompound("Laithorn");
		NBTCompound laithorn = nbti.addCompound("Laithorn");
		for(int i = 0; i < types.length; i++) {
			laithorn.setString("Fragment_" + i, types[i]);
		}
		
		item = nbti.getItem();
		
		return item;
	}
}
