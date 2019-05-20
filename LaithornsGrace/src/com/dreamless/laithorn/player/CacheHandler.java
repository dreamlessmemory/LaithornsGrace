package com.dreamless.laithorn.player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.dreamless.laithorn.PlayerMessager;

public class CacheHandler {

	private static Map<UUID, PlayerData> playerCache = new HashMap<UUID, PlayerData>();

	public static void loadPlayer(Player player) {
		if (player == null) {
			return;
		}
		if (!playerCache.containsKey(player.getUniqueId())) {
			PlayerMessager.debugLog("Loading " + player.getDisplayName() + " into cache");
			playerCache.put(player.getUniqueId(), DatabaseHandler.retreivePlayerData(player));
		}
	}

	public static void unloadPlayer(OfflinePlayer player) {
		PlayerMessager.debugLog("Removed " + player.getName() + " from cache");
		playerCache.remove(player.getUniqueId());
	}

	public static void updatePlayer(Player player, PlayerData data) {
		playerCache.put(player.getUniqueId(), data);
	}

	public static void saveCacheToDatabase() {
		PlayerMessager.debugLog("Starting save of data cache");

		for (Iterator<Map.Entry<UUID, PlayerData>> it = playerCache.entrySet().iterator(); it.hasNext();) {
			Map.Entry<UUID, PlayerData> entry = it.next();
			OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());

			if (player == null) {
				it.remove();
				PlayerMessager.debugLog("Removed [null] from cache");
				//unloadPlayer(player);
				continue;
			}

			PlayerMessager.debugLog("Saving data: " + player.getName() + ".");
			DatabaseHandler.updatePlayerData(entry.getKey(), entry.getValue());

			// Remove player from cache if they are offline
			if (!player.isOnline()) {
				it.remove();
				//unloadPlayer(player);
				PlayerMessager.debugLog("Removed " + player.getName() + " from cache");
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
