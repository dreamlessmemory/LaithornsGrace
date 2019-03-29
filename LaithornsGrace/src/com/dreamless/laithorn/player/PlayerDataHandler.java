package com.dreamless.laithorn.player;

public class PlayerDataHandler {

	public static int getNewEXPRequirement(int level) {
		//TODO: Implement EXP curve
		return level * 100;
	}
	
	public static boolean canCraftItem(String item) {
		//TODO: Logic
		return true;
	}
	
}
