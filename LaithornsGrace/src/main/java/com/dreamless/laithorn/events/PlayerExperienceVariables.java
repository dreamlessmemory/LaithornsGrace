package com.dreamless.laithorn.events;

public class PlayerExperienceVariables {
	
	private static int FRAGMENT_EXP = 10;
	private static int DROP_EXP = 1;
	
	public static int getDropExp() {
		return DROP_EXP;
	}

	public static void setDropExp(int dropExp) {
		DROP_EXP = dropExp;
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

