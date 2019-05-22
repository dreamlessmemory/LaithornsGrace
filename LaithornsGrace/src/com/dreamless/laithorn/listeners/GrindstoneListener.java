package com.dreamless.laithorn.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.api.ItemCrafting;

public class GrindstoneListener implements Listener{
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreakEven(PrepareItemCraftEvent event) {
		CraftingInventory inventory = event.getInventory();
		if(inventory.getType() != InventoryType.GRINDSTONE) {
			return; //Ignore
		}
		
		ItemStack itemTop = inventory.getMatrix()[0];
		ItemStack itemBottom = inventory.getMatrix()[1];
		
		if(ItemCrafting.isEssence(itemBottom) || ItemCrafting.isEssence(itemTop)) {
			inventory.setResult(new ItemStack(Material.AIR));
			PlayerMessager.debugLog("Cancelling grindstone");
		}
	}
}
