package com.dreamless.laithorn.listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.LanguageReader;
import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.api.Fragment;
import com.dreamless.laithorn.player.PlayerData;

public class InventoryListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onReservoirClose(InventoryCloseEvent event) {
		Inventory inventory = event.getInventory();
		if (!(inventory.getHolder() instanceof PlayerData)) {
			return;
		}
		Player player = (Player) event.getPlayer();

		boolean ejected = false;
		for (ItemStack item : inventory.getContents()) {
			if (item == null) {
				continue;
			}
			if (item.getType() == Material.AIR) {
				continue;
			}
			if (!Fragment.isStorable(item)) {
				player.getWorld().dropItemNaturally(player.getLocation().add(0, 1.5, 0), item);
				inventory.remove(item);
				ejected = true;
			}
		}

		if (ejected) {
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP,
					(float) (Math.random() / 2) + 0.75f, (float) (Math.random() / 2) + 0.75f);
			PlayerMessager.msg(player, LanguageReader.getText("Player_EjectReservior"));
		}
		
		player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 0.1f, 1.0f);
	}

}
