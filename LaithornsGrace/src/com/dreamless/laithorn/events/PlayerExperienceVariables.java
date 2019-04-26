package com.dreamless.laithorn.events;

import java.util.HashMap;

public class PlayerExperienceVariables {
	
	public static HashMap<String, Integer> experienceValues = new HashMap<String, Integer>();
	
	public enum GainType {
		ATTUNEMENT, SMITHING;
	}
}

