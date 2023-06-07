package com.dreamless.laithorn.player;

import java.util.ArrayList;
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
	
	private static ArrayList<Player> retryList = new ArrayList<Player>();

	public static void loadPlayer(Player player) {
		if (player == null) {
			return;
		}
		if (!playerCache.containsKey(player.getUniqueId())) {
			PlayerMessager.debugLog("Loading " + player.getDisplayName() + " into cache");
			PlayerData playerData = DatabaseHandler.retreivePlayerData(player);
			if(playerData.isValid())
			{
				PlayerMessager.debugLog("Succesfully loaded " + player.getName() + " into cache");
				playerCache.put(player.getUniqueId(), playerData);
			}
			else 
			{
				PlayerMessager.msg(player, "Your connection to Laithorn has been disrupted.");
				PlayerMessager.debugLog("Unable to load " + player.getName() + " into cache");
			}
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
	
	public static void retryPlayerDataRetrival() {
		
		if(retryList.size() == 0)
		{
			PlayerMessager.debugLog("Skipping cache save retry");
			return;
		}
		
		PlayerMessager.debugLog("Attemping retry for  " + retryList.size() + " players");
		for(Iterator<Player> it = retryList.iterator(); it.hasNext();)
		{
			Player player = it.next();	
			PlayerMessager.debugLog("Attemping retry for " + player.getName());
			PlayerData playerData = DatabaseHandler.retreivePlayerData(player);
			if(playerData.isValid())
			{
				PlayerMessager.debugLog("Succesfully loaded " + player.getName() + " into cache");
				playerCache.put(player.getUniqueId(), playerData);
				PlayerMessager.msg(player, "Your connection to Laithorn has been restored.");
				it.remove();
			}
			else 
			{
				PlayerMessager.debugLog("Unable to load " + player.getName() + " into cache. Trying again.");
			}
		}
	}

	public static class PeriodicCacheSave extends BukkitRunnable {
		@Override
		public void run() {
			saveCacheToDatabase();
		}
	}
	
	public static class PeriodicCacheRetry extends BukkitRunnable
	{
		@Override
		public void run() {
			retryPlayerDataRetrival();
		}
	}
}
