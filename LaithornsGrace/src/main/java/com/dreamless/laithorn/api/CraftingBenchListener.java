package com.dreamless.laithorn.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CraftingBenchListener implements Listener {
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {

		Recipe recipe = event.getRecipe();
		if (recipe == null) {
			return; // Ignore if no recipe
		}

		CraftingInventory inventory = event.getInventory();
		
		if (!ItemCrafting.craftingBenchPrepareCheck(inventory, (Player) event.getView().getPlayer(), recipe)) { //Fragment check
			inventory.setResult(new ItemStack(Material.AIR)); // Effectively cancel event if not the right level
		}
	}
}
