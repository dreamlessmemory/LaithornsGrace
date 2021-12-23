package com.dreamless.laithorn.player;

import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;

public class PlayerDataHandler {

	public static int LEVEL_ONE_EXP = 7500;
	public static double GROWTH_RATE = 1.50;
	public static int LEVEL_CAP = 10;

	public static int getNewEXPRequirement(int level) {
		if(level > LEVEL_CAP)
		{
			return 0;
		}
		else 
		{
			return (int) Math.round(LEVEL_ONE_EXP * Math.pow(GROWTH_RATE, level));	
		}
	}

	public static boolean canCraftItem(String item) {
		// TODO: Logic
		return true;
	}
	
	public static String getTypeDescription(GainType type) {
		switch (type) {
		case ATTUNEMENT:
			return "attunement";
		case SMITHING:
			return "essence smithing";
		default:
			return "";
		}
	}
	
	public static PlayerData applyDataChanges(PlayerData data, GainType type, int newLevel, int newExp) {
		if (type == GainType.ATTUNEMENT) {
			data.setAttunementLevel(newLevel);
			data.setAttunementEXP(newExp);
		} else if (type == GainType.SMITHING) {
			data.setSmithingLevel(newLevel);
			data.setSmithingEXP(newExp);
		}
		return data;
	}
	
	
	public static class PlayerExperienceSet {
		private int currentLevel;
		private int currentExpRating;
		private int requiredExpRating;
		private boolean levelCap;

		public PlayerExperienceSet(PlayerData data, GainType type) {
			if (type == GainType.ATTUNEMENT) {
				currentLevel = data.getAttunementLevel();
				currentExpRating = data.getAttunementEXP();
			} else if (type == GainType.SMITHING) {
				currentLevel = data.getSmithingLevel();
				currentExpRating = data.getSmithingEXP();
			}
			levelCap = currentLevel >= LEVEL_CAP;
			requiredExpRating = PlayerDataHandler.getNewEXPRequirement(currentLevel + 1);
		}

		public final int getCurrentLevel() {
			return currentLevel;
		}

		public final int getCurrentExpRating() {
			return currentExpRating;
		}

		public final int getRequiredExpRating() {
			return requiredExpRating;
		}

		public final boolean isLevelCap() {
			return levelCap;
		}
	}

}
