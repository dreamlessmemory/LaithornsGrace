package com.dreamless.laithorn.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
//import org.bukkit.event.player.PlayerQuitEvent;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.events.PlayerExperienceGainEvent;
import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;
import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;
import com.dreamless.laithorn.player.PlayerDataHandler;
import com.dreamless.laithorn.player.PlayerDataHandler.PlayerExperienceSet;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		// TODO: Load Cache
		Player player = event.getPlayer();
		CacheHandler.loadPlayer(player);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerExperienceGain(PlayerExperienceGainEvent event) {
		GainType type = event.getGainType();
		Player player = event.getPlayer();
		PlayerData data = CacheHandler.getPlayer(player);
		int expGained = event.getExpGain();
		
		PlayerExperienceSet expSet = new PlayerExperienceSet(data, type);
		
		if(expSet.isLevelCap()) {
			return; //Ignore if levelcapped
		}
		
		int currentLevel = expSet.getCurrentLevel();
		int currentExpRating = expSet.getCurrentExpRating();
		int requiredExpRating = expSet.getRequiredExpRating();
		
		PlayerMessager.msg(player, "You gained: " + expGained + " EXP");
		
		// Calculate levelup
		int newExpRating = currentExpRating + expGained;
		int levelsGained = 0;
		
		PlayerMessager.debugLog("NEW: " + newExpRating + " REQ: " + requiredExpRating);
		
		while(newExpRating > requiredExpRating) {
			// Level up
			newExpRating -= requiredExpRating;
			requiredExpRating = PlayerDataHandler.getNewEXPRequirement(currentLevel + ++levelsGained + 1);
			PlayerMessager.debugLog("LEVELUP - NEW: " + newExpRating + " REQ: " + requiredExpRating);
		}
		
		// Set EXP
		
		
		if(levelsGained > 0) {
			// Inform player
			PlayerMessager.msg(player, "You gained " +  + levelsGained + "  "+ 
					PlayerDataHandler.getTypeDescription(type) + " level" + (levelsGained > 1 ? "s " : ""));
		}
		
		CacheHandler.updatePlayer(player, PlayerDataHandler.applyDataChanges(data, type, currentLevel + levelsGained, newExpRating));

		
	}
}
