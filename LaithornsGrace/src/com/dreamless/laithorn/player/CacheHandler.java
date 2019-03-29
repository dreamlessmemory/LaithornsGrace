package com.dreamless.laithorn.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.dreamless.laithorn.PlayerMessager;

public class CacheHandler {

	private static Map<UUID, PlayerData> playerCache = new HashMap<UUID, PlayerData>();
	
	public static void loadPlayer(Player player) {
		playerCache.put(player.getUniqueId(), DatabaseHandler.retreivePlayerData(player));
	}
	
	public static void unloadPlayer(Player player) {
		playerCache.remove(player.getUniqueId());
	}
	
	public class periodicCacheSave extends BukkitRunnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			PlayerMessager.debugLog("Starting save of data cache");
			
			for(Entry<UUID, PlayerData> entry : playerCache.entrySet()){
				DatabaseHandler.updatePlayerData(entry.getKey(), entry.getValue());
			}
			
		}
		
	}
}
