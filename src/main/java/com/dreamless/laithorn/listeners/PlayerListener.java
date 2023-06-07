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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.api.Fragment;
import com.dreamless.laithorn.events.PlayerExperienceGainEvent;
import com.dreamless.laithorn.events.PlayerExperienceVariables;
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
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = CacheHandler.getPlayer(player);
		// send message
		if(data != null && data.isValid() && data.getFlag(PlayerData.LOGIN_MESSAGE_FLAG))
		{
			
			if(data.getBoostedFragments() > 0)
			{
				PlayerMessager.msg(player, "Your connection to Laithorn is empowered. The next " + 
						data.getBoostedFragments() + " fragments will give more experience");
			}
			else 
			{
				PlayerMessager.msg(player, "Your connection with Laithorn is normal.");
			}
			
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

		// Check Level cap
		if (expSet.isLevelCap()) {
			return; // Ignore if levelcapped
		}

		// Calculate bonus
		if(event.getBonusesUsed() > 0 && data.getBoostedFragments() > 0)
		{
			//get number of bonuses to apply
			int usedBonuses = event.getBonusesUsed();
			int availableBonuses = data.getBoostedFragments();
			int appliedBonuses = 0;
			if(availableBonuses <= usedBonuses)
			{
				appliedBonuses = availableBonuses;
				data.setBoostedFragments(0);
				if(data.getFlag(PlayerData.BONUS_MESSAGE_FLAG))
				{
					PlayerMessager.msg(player, "Your empowered connection with Laithorn has faded.");
				}
			}
			else 
			{
				appliedBonuses = usedBonuses;
				int remainingBonus = availableBonuses - usedBonuses;
				data.setBoostedFragments(remainingBonus);
				if(data.getFlag(PlayerData.BONUS_MESSAGE_FLAG))
				{
					String secondHalf = "";
					if(remainingBonus > 0)
					{
						secondHalf = " This will last for " + remainingBonus + " more fragments.";
					}
					else 
					{
						secondHalf = " Your empowered connection with Laithorn has faded.";
					}
					PlayerMessager.msg(player, appliedBonuses + " fragments were empowered." + secondHalf);
				}
			}
			expGained += appliedBonuses * PlayerExperienceVariables.getBonusExp();
		}

		// Send message
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
		int currentLevel = expSet.getCurrentLevel();
		int requiredExpRating = expSet.getRequiredExpRating();
		int newExpRating = expSet.getCurrentExpRating() + expGained;
		int levelsGained = 0;

		PlayerMessager.debugLog("NEW: " + newExpRating + " REQ: " + requiredExpRating);

		while (newExpRating >= requiredExpRating) {
			// Level up
			newExpRating -= requiredExpRating;
			requiredExpRating = PlayerDataHandler.getNewEXPRequirement(currentLevel + ++levelsGained);
			PlayerMessager.debugLog("LEVELUP - NEW: " + newExpRating + " REQ: " + requiredExpRating);
			
			// Zero out Fragments
			if(event.getGainType() == GainType.ATTUNEMENT)
			{
				data.setBoostedFragments(0);
			}
			
			// Level cap check
			if(currentLevel + levelsGained >= PlayerDataHandler.LEVEL_CAP) 
			{
				newExpRating = 0;
				requiredExpRating = 0;
				break;
			}
		}

		// Set EXP
		if (levelsGained > 0) {
			PlayerMessager.msg(player, "You have reached " + PlayerDataHandler.getTypeDescription(type) + " level "
					+ (currentLevel + levelsGained));
			if(data.getFlag(PlayerData.BONUS_MESSAGE_FLAG) && event.getGainType() == GainType.ATTUNEMENT)
			{
				PlayerMessager.msg(player, "Your empowered connection has faded and returned to normal.");
			}
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
		if (!Fragment.isEssence(itemStack)) {
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
