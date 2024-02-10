package com.dreamless.laithorn.api;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;

public class AnvilListener implements Listener{

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAnvilPrepare(PrepareAnvilEvent event) {
		if(ItemRepair.anvilPrepareCheck(event.getInventory(), (Player) event.getView().getPlayer())) {
			event.setResult(ItemRepair.generateRepairedItem(event.getInventory()));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onItemPickup(InventoryClickEvent event) {
		if(!(event.getInventory() instanceof AnvilInventory)) {
			return;
		}
		if(event.getSlot() != 2) {
			return;
		}
		
		if(event.getCurrentItem() == null)
		{
			return;
		}
		
		ItemRepair.anvilPickupCheck(event.getInventory(), event.getCurrentItem(), (Player) event.getWhoClicked(), event.isShiftClick());
	}
}
