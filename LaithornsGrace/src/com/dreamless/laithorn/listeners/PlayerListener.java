package com.dreamless.laithorn.listeners;

import java.util.HashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.api.ItemCrafting;
import com.dreamless.laithorn.events.PlayerExperienceGainEvent;
import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;
import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;
import com.dreamless.laithorn.player.PlayerDataHandler;
import com.dreamless.laithorn.player.PlayerDataHandler.PlayerExperienceSet;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			CacheHandler.loadPlayer(player);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		if (player != null && event.getReason().equalsIgnoreCase("You are not whitelisted on this server!")) {
			CacheHandler.unloadPlayer(player);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerExperienceGain(PlayerExperienceGainEvent event) {
		GainType type = event.getGainType();
		Player player = event.getPlayer();
		PlayerData data = CacheHandler.getPlayer(player);
		int expGained = event.getExpGain();

		PlayerExperienceSet expSet = new PlayerExperienceSet(data, type);

		if (expSet.isLevelCap()) {
			return; // Ignore if levelcapped
		}

		int currentLevel = expSet.getCurrentLevel();
		int currentExpRating = expSet.getCurrentExpRating();
		int requiredExpRating = expSet.getRequiredExpRating();

		if (event.shouldInformPlayer()) {
			switch (event.getGainType()) {
			case ATTUNEMENT:
				PlayerMessager.msg(player, "Your attunement has increased by " + expGained + " points");
				break;
			case SMITHING:
				PlayerMessager.msg(player, "Your essence smithing skill has increased by " + expGained + " points");
				break;
			default:
				break;
			}
		}
		// Calculate levelup
		int newExpRating = currentExpRating + expGained;
		int levelsGained = 0;

		PlayerMessager.debugLog("NEW: " + newExpRating + " REQ: " + requiredExpRating);

		while (newExpRating > requiredExpRating) {
			// Level up
			newExpRating -= requiredExpRating;
			requiredExpRating = PlayerDataHandler.getNewEXPRequirement(currentLevel + ++levelsGained + 1);
			PlayerMessager.debugLog("LEVELUP - NEW: " + newExpRating + " REQ: " + requiredExpRating);
		}

		// Set EXP

		if (levelsGained > 0) {
			// Inform player
			// PlayerMessager.msg(player, "You have advanced " + levelsGained +
			// PlayerDataHandler.getTypeDescription(type) + " level" + (levelsGained > 1 ?
			// "s " : ""));
			PlayerMessager.msg(player, "You have reached " + PlayerDataHandler.getTypeDescription(type) + " level "
					+ (currentLevel + levelsGained));
		}

		CacheHandler.updatePlayer(player,
				PlayerDataHandler.applyDataChanges(data, type, currentLevel + levelsGained, newExpRating));
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onItemPickup(EntityPickupItemEvent event) {
		LivingEntity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			PlayerMessager.debugLog("Not player pickup");
			return;
		}
		Item item = event.getItem();
		ItemStack itemStack = item.getItemStack();
		if (!ItemCrafting.isEssence(itemStack)) {
			PlayerMessager.debugLog("Not essence pickup");
			return;
		}

		Player player = (Player) entity;
		PlayerData data = CacheHandler.getPlayer(player);
		if (data.getFlag("autopickup")) {
			// Kill event
			event.setCancelled(true);
			item.remove();

			Inventory inventory = data.getInventory();
			if (inventory.firstEmpty() == -1) {
				PlayerMessager.msg(player, "Your reservoir is full.");
			}
			HashMap<Integer, ItemStack> remains = data.getInventory().addItem(itemStack);
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
			if (!remains.isEmpty()) {
				remains = player.getInventory().addItem(remains.get(0));
				if (!remains.isEmpty()) {
					PlayerMessager.msg(player, "Your inventory is totally is full.");
					player.getWorld().dropItemNaturally(player.getLocation().add(0, 0.5, 0), remains.get(0));
				}
			}
		} else {
			PlayerMessager.debugLog("Not autopickup pickup");
		}

	}
}
