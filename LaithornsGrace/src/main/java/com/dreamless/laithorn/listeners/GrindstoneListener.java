package com.dreamless.laithorn.listeners;

import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.api.Fragment;

public class GrindstoneListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGrindstoneClick(InventoryClickEvent event) {
		InventoryView view = event.getView();
		Inventory topInventory = view.getTopInventory();
		if (topInventory.getType() != InventoryType.GRINDSTONE) {
			PlayerMessager.debugLog("Ignoring, not grindstone");
			return; // Ignore If not Grindstone
		}

		switch (event.getSlot()) {
		case 0:
		case 1:
			ItemStack item = event.getCursor();
			if(!Fragment.isLaithornEnchanted(item)) {
				PlayerMessager.debugLog("Not fragment");
				return;
			}
			event.setCancelled(true);
			break;
		default:
			if(event.isShiftClick()) {
				ItemStack item2 = event.getCurrentItem();
				if(!Fragment.isLaithornEnchanted(item2)) {
					PlayerMessager.debugLog("Not fragment");
					return;
				}
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGrindstoneDrag(InventoryDragEvent event) {
		InventoryView view = event.getView();
		Inventory topInventory = view.getTopInventory();
		if (topInventory.getType() != InventoryType.GRINDSTONE) {
			PlayerMessager.debugLog("Ignoring, not grindstone");
			return; // Ignore If not Grindstone
		}

		
		Set<Integer> slots = event.getInventorySlots();
		if(slots.contains(0) || slots.contains(1)) {
			ItemStack item = event.getCursor();
			if(!Fragment.isLaithornEnchanted(item)) {
				PlayerMessager.debugLog("Not fragment");
				return;
			}
			event.setCancelled(true);
		}
	}
}
