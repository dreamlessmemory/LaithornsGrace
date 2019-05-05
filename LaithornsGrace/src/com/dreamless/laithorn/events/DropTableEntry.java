package com.dreamless.laithorn.events;

import java.util.HashMap;

public class DropTableEntry {
	private final double DROP_CHANCE;
	private final String BASE_TYPE;
	//private final ArrayList<String> TAGS;
	
	private final HashMap<String, Double> TAGS;  
	
	public DropTableEntry(double dropChance, String baseType, HashMap<String, Double> tags) {
		this.DROP_CHANCE = dropChance;
		this.BASE_TYPE = baseType;
		this.TAGS = tags;
	}
	public final double getDropChance() {
		return DROP_CHANCE;
	}
	public final String getBaseType() {
		return BASE_TYPE;
	}
	public final HashMap<String, Double> getTags() {
		return TAGS;
	}
}
