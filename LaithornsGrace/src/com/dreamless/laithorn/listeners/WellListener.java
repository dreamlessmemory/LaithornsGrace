package com.dreamless.laithorn.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.dreamless.laithorn.LaithornsGrace;
import com.dreamless.laithorn.LanguageReader;
import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.WellLocationHandler;
import com.dreamless.laithorn.events.WellDropEvent;

import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTItem;

public class WellListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFragmentDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		ItemStack itemStack = event.getItemDrop().getItemStack();
		
		// Check if fragment
		if (LaithornsGrace.FRAGMENT_MATERIAL != itemStack.getType()) {
			 PlayerMessager.debugLog("Not fragment");
			return;
		}

		NBTItem nbti = new NBTItem(itemStack);
		if (!nbti.hasKey("Laithorn")) {
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
		
		//Delayed handlers
		new WellDropEvent(player, item).runTaskLater(LaithornsGrace.grace, 20);
	}
}
