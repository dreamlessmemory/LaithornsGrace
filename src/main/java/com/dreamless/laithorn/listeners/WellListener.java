package com.dreamless.laithorn.listeners;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.LaithornsGrace;
import com.dreamless.laithorn.LanguageReader;
import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.api.Fragment;
import com.dreamless.laithorn.events.WellDropEvent;

import de.tr7zw.changeme.nbtapi.NBTItem;

public class WellListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFragmentDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		ItemStack itemStack = event.getItemDrop().getItemStack();
		
		// Check if fragment
		if (Fragment.getFragmentMaterial() != itemStack.getType()) {
			 PlayerMessager.debugLog("Not fragment");
			return;
		}

		NBTItem nbti = new NBTItem(itemStack);
		if (!nbti.hasTag("Laithorn")) {
			// PlayerMessager.debugLog("Not warp leaf");
			return;
		} // else PlayerMessager.debugLog("Nope?");

		// Variables
		Player player = event.getPlayer();

		if (!player.hasPermission("Laithorn.user")) {
			PlayerMessager.msg(player, LanguageReader.getText("Error_NoPermissions_Action"));
			return;
		}

		/*** Actual processing here ***/
		// Inform player
		PlayerMessager.debugLog("Process item");
		
		item.setPickupDelay(21); //Prevent pickup
		
		//Delayed handlers
		new WellDropEvent(player, item).runTaskLater(LaithornsGrace.grace, 20);
	}
}
