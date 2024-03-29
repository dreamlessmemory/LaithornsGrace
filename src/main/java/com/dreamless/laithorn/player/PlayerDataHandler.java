package com.dreamless.laithorn.player;

import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;

public class PlayerDataHandler {

	public static int LEVEL_CAP = 10;
	public static int LEVEL_ONE_EXP = 1000;
	public static double GROWTH_RATE = 1.5;

	public static void setLevelingConfiguration(int levelOneStacks, int maxLevelStacks, int levelCap, int stackExp) {
		LEVEL_CAP = levelCap;
		LEVEL_ONE_EXP = levelOneStacks*stackExp;
		GROWTH_RATE = Math.pow((double)(maxLevelStacks)/levelOneStacks, 1.0/(levelCap-1));
	}

	// getNewEXPRequirement returns the requirements to level up for a player at the given attunement level.
	public static int getNewEXPRequirement(int level) {
		if(level >= LEVEL_CAP)
		{
			// Ensure that no amount of experience can raise the player level past the cap.
			return Integer.MAX_VALUE;
		}

		return (int) Math.round(LEVEL_ONE_EXP * Math.pow(GROWTH_RATE, level));
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
			requiredExpRating = PlayerDataHandler.getNewEXPRequirement(currentLevel);
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
