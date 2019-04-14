package com.dreamless.laithorn;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;

public class RecipeHandler {

	private static HashMap<String, Integer> levelRequirements = new HashMap<String, Integer>();
	
	public static void registerRecipe(String name, int level) {
		levelRequirements.put(name, level);
	}
	
	public static boolean canDoAction(Player player, String action) {
		
		if(!levelRequirements.containsKey(action)){
			return false;
		}
		
		PlayerData data = CacheHandler.getPlayer(player);
		return data.getSmithingLevel() >= levelRequirements.get(action); 
	}
}
