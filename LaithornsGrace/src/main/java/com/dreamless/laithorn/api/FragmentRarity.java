package com.dreamless.laithorn.api;

import java.util.HashMap;

public enum FragmentRarity {
	DULL(0), FAINT(1), PALE(2), GLOWING(3), SPARKLING(4), BRIGHT(5), SHINING(6), RADIANT(7), INCANDESCENT(8);

	private static HashMap<FragmentRarity, HashMap<Integer, Integer>> WEIGHTS_MAP = new HashMap<FragmentRarity, HashMap<Integer, Integer>>();

	public static void initializeWeightsMap()
	{
		WEIGHTS_MAP.clear();

		// DULL
		HashMap<Integer, Integer> dullHashMap = new HashMap<Integer, Integer>();
		dullHashMap.put(0, 100);
		dullHashMap.put(1, 90);
		dullHashMap.put(2, 70);
		dullHashMap.put(3, 40);
		dullHashMap.put(4, 20);
		dullHashMap.put(5, 10);
		WEIGHTS_MAP.put(DULL, dullHashMap);

		// FAINT
		HashMap<Integer, Integer> faintHashMap = new HashMap<Integer, Integer>();
		faintHashMap.put(1, 10);
		faintHashMap.put(2, 20);
		faintHashMap.put(3, 30);
		faintHashMap.put(4, 20);
		faintHashMap.put(5, 10);
		dullHashMap.put(6, 10);
		WEIGHTS_MAP.put(FAINT, faintHashMap);

		// PALE
		HashMap<Integer, Integer> paleHashMap = new HashMap<Integer, Integer>();
		paleHashMap.put(2, 10);
		paleHashMap.put(3, 20);
		paleHashMap.put(4, 30);
		paleHashMap.put(5, 20);
		paleHashMap.put(6, 10);
		paleHashMap.put(7, 10);
		WEIGHTS_MAP.put(PALE, paleHashMap);

		// GLOWING
		HashMap<Integer, Integer> glowingHashMap = new HashMap<Integer, Integer>();
		glowingHashMap.put(3, 10);
		glowingHashMap.put(4, 20);
		glowingHashMap.put(5, 30);
		glowingHashMap.put(6, 20);
		glowingHashMap.put(7, 10);
		glowingHashMap.put(8, 10);
		WEIGHTS_MAP.put(GLOWING, glowingHashMap);

		// SPARKLING
		HashMap<Integer, Integer> sparklingHashMap = new HashMap<Integer, Integer>();
		sparklingHashMap.put(4, 10);
		sparklingHashMap.put(5, 20);
		sparklingHashMap.put(6, 30);
		sparklingHashMap.put(7, 20);
		sparklingHashMap.put(8, 10);
		sparklingHashMap.put(9, 10);
		WEIGHTS_MAP.put(SPARKLING, sparklingHashMap);

		// BRIGHT
		HashMap<Integer, Integer> brightHashMap = new HashMap<Integer, Integer>();
		brightHashMap.put(5, 10);
		brightHashMap.put(6, 20);
		brightHashMap.put(7, 30);
		brightHashMap.put(8, 20);
		brightHashMap.put(9, 10);
		brightHashMap.put(10, 10);
		WEIGHTS_MAP.put(BRIGHT, brightHashMap);

		// SHINING
		HashMap<Integer, Integer> shiningHashMap = new HashMap<Integer, Integer>();
		shiningHashMap.put(6, 10);
		shiningHashMap.put(7, 20);
		shiningHashMap.put(8, 30);
		shiningHashMap.put(9, 30);
		shiningHashMap.put(10, 20);
		WEIGHTS_MAP.put(SHINING, shiningHashMap);

		// RADIANT
		HashMap<Integer, Integer> radiantHashMap = new HashMap<Integer, Integer>();
		radiantHashMap.put(7, 10);
		radiantHashMap.put(8, 20);
		radiantHashMap.put(9, 30);
		radiantHashMap.put(10, 40);
		WEIGHTS_MAP.put(RADIANT, radiantHashMap);

		// INCANDESCENT
		HashMap<Integer, Integer> incandescentHashMap = new HashMap<Integer, Integer>();
		incandescentHashMap.put(8, 10);
		incandescentHashMap.put(9, 20);
		incandescentHashMap.put(10, 300);
		WEIGHTS_MAP.put(INCANDESCENT, incandescentHashMap);
	}

	private final Integer level;

	private FragmentRarity(int level) {
		this.level = level;
	}

	public boolean meetsMinimum(FragmentRarity other) {
		return this.level >= other.level;
	}

	public final double rarityModifier() {
		switch (this) {
		case DULL:
			return 1.00;
		case FAINT:
			return 1.20;
		case PALE:
			return 1.50;
		case GLOWING:
			return 2.00;
		case SPARKLING:
			return 3.00;
		case BRIGHT:
			return 4.50;
		case SHINING:
			return 6.00;
		case RADIANT:
			return 8.00;
		case INCANDESCENT:
			return 12.00;
		default:
			return 1.0;
		}
	}

	public final int rarityDropQuantityMinBonus() {
		switch (this) {
		case DULL:
			return 0;
		case FAINT:
			return 0;
		case PALE:
			return 0;
		case GLOWING:
			return 0;
		case SPARKLING:
			return 1;
		case BRIGHT:
			return 1;
		case SHINING:
			return 1;
		case RADIANT:
			return 1;
		case INCANDESCENT:
			return 2;
		default:
			return 0;
		}
	}
	
	public final int rarityDropQuantityMaxBonus() {
		switch (this) {
		case DULL:
			return 0;
		case FAINT:
			return 1;
		case PALE:
			return 2;
		case GLOWING:
			return 3;
		case SPARKLING:
			return 3;
		case BRIGHT:
			return 4;
		case SHINING:
			return 5;
		case RADIANT:
			return 6;
		case INCANDESCENT:
			return 7;
		default:
			return 0;
		}
	}

	public final double weightedDropChance(int level) {
		return WEIGHTS_MAP.get(this).getOrDefault(level, 0);
	}

}
