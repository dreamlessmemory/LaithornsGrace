package com.dreamless.laithorn.events;

public class PlayerExperienceVariables {
	
	private static int FRAGMENT_EXP = 10;
	private static int DROP_EXP = 1;
	private static int BONUS_EXP = 10;
	
	public static int getDropExp() {
		return DROP_EXP;
	}

	public static void setDropExp(int dropExp) {
		DROP_EXP = dropExp;
	}
	
	public static int getBonusExp() {
		return BONUS_EXP;
	}

	public static void setBonusExp(int bonusExp) {
		BONUS_EXP = bonusExp;
	}

	public enum GainType {
		ATTUNEMENT, SMITHING;
	}
	
	public static void setFragmentExp(int fragmentExp)
	{
		FRAGMENT_EXP = fragmentExp;
	}
	
	public static int getFragmentExp()
	{
		return FRAGMENT_EXP;
	}
}

