package com.dreamless.laithorn.api;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;

public class RequirementsHandler {

	private static HashMap<String, Integer> levelRequirements = new HashMap<String, Integer>();

	public static void registerRecipe(String name, int level) {
		levelRequirements.put(name, level);
	}

	public static boolean canDoAction(Player player, String action, List<String> flags) {

		if (!levelRequirements.containsKey(action)) {
			return false;
		}

		PlayerData data = CacheHandler.getPlayer(player);

		if (flags != null) {
			for (String flag : flags) {
				if (!data.getFlag(flag))
					return false;
			}
		}
		return data.getSmithingLevel() >= levelRequirements.get(action);
	}
}
