package com.dreamless.laithorn.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
//import org.bukkit.event.player.PlayerQuitEvent;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.events.PlayerExperienceGainEvent;
import com.dreamless.laithorn.player.CacheHandler;

public class PlayerListener implements Listener{

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		// TODO: Load Cache
		Player player = event.getPlayer();
		CacheHandler.loadPlayer(player);
	}
	/****************
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerLogout(PlayerQuitEvent event) {
		// TODO: Load Cache
		Player player = event.getPlayer();
		CacheHandler.unloadPlayer(player);
	}
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerExperienceGain(PlayerExperienceGainEvent event) {
		Player player = event.getPlayer();
		PlayerMessager.msg(player, "You gained: " + event.getExpGain() + " EXP");
	}
}
