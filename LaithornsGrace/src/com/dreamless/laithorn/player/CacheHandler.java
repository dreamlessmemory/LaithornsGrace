package com.dreamless.laithorn.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.dreamless.laithorn.PlayerMessager;

public class CacheHandler {

	private static Map<UUID, PlayerData> playerCache = new HashMap<UUID, PlayerData>();

	public static void loadPlayer(Player player) {
		if (!playerCache.containsKey(player.getUniqueId())) {
			PlayerMessager.debugLog("Loading " + player.getDisplayName() + " into cache");
			playerCache.put(player.getUniqueId(), DatabaseHandler.retreivePlayerData(player));
		}
	}

	public static void unloadPlayer(OfflinePlayer player) {
		PlayerMessager.debugLog("Removed " + player.getName() + " from cache");
		playerCache.remove(player.getUniqueId());
	}

	public static void saveCacheToDatabase() {
		PlayerMessager.debugLog("Starting save of data cache");
		for (Entry<UUID, PlayerData> entry : playerCache.entrySet()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());

			PlayerMessager.debugLog("Saving data: " + player.getName() + ".");
			DatabaseHandler.updatePlayerData(entry.getKey(), entry.getValue());

			// Remove player from cache if they are offline
			if (!player.isOnline()) {
				unloadPlayer(player);
			}
		}
	}

	public static PlayerData getPlayer(Player player) {
		return playerCache.get(player.getUniqueId());
	}

	public static class PeriodicCacheSave extends BukkitRunnable {
		@Override
		public void run() {
			saveCacheToDatabase();
		}
	}
}
