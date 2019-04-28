package com.dreamless.laithorn.events;

import java.util.ArrayList;

public class MobDropTableEntry {
	private final double DROP_CHANCE;
	private final String BASE_TYPE;
	private final ArrayList<String> TAGS;
	public MobDropTableEntry(double dropChance, String baseType, ArrayList<String> tags) {
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
	public final ArrayList<String> getTags() {
		return TAGS;
	}
}
