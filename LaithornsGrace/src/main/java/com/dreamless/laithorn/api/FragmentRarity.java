package com.dreamless.laithorn.api;

public enum FragmentRarity {
	DULL(0), FAINT(1), PALE(2), GLOWING(3), SPARKLING(4), BRIGHT(5), SHINING(6), RADIANT(7), INCANDESCENT(8);
	
	public static final int DULL_MIN_LEVEL = 0;
	public static final int FAINT_MIN_LEVEL = 10;
	public static final int PALE_MIN_LEVEL = 15;
	public static final int GLOWING_MIN_LEVEL = 20;
	public static final int SPARKLING_MIN_LEVEL = 25;
	public static final int BRIGHT_MIN_LEVEL = 30;
	public static final int SHINING_MIN_LEVEL = 35;
	public static final int RADIANT_MIN_LEVEL = 40;
	public static final int INCANDESCENT_MIN_LEVEL = 45;

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
	
	public final int rarityDropQuantityBonus() {
		switch (this) {
		case DULL:
			return 0;
		case FAINT:
			return 1;
		case PALE:
			return 1;
		case GLOWING:
			return 2;
		case SPARKLING:
			return 2;
		case BRIGHT:
			return 3;
		case SHINING:
			return 4;
		case RADIANT:
			return 5;
		case INCANDESCENT:
			return 6;
		default:
			return 0;
		}
	}
	
	public final double weightedDropChance(int level) {
		switch(this) {
		case DULL:
			return 100.00;
		case FAINT:
			return (level >= FAINT_MIN_LEVEL ? (double)(level-FAINT_MIN_LEVEL) * 0.8583 + 11.1111 : 0);
		case PALE:
			return (level >= PALE_MIN_LEVEL ? (double)(level-PALE_MIN_LEVEL) * 0.4277 + 6.0738 : 0);
		case GLOWING:
			return (level >= GLOWING_MIN_LEVEL ? (double)(level-GLOWING_MIN_LEVEL) * 0.4464 + 7.5421 : 0);
		case SPARKLING:
			return (level >= SPARKLING_MIN_LEVEL ? (double)(level-SPARKLING_MIN_LEVEL) * 0.1346 + 1.6180 : 0);
		case BRIGHT:
			return (level >= BRIGHT_MIN_LEVEL ? (double)(level-BRIGHT_MIN_LEVEL) * 0.2302 + 0.1725 : 0);
		case SHINING:
			return (level >= SHINING_MIN_LEVEL ? (double)(level-SHINING_MIN_LEVEL) * 0.1124 + 0.3667 : 0);
		case RADIANT:
			return (level >= RADIANT_MIN_LEVEL ? (double)(level-RADIANT_MIN_LEVEL) * 0.1260 + 0.1956 : 0);
		case INCANDESCENT:
			return (level >= INCANDESCENT_MIN_LEVEL ?(double)(level-INCANDESCENT_MIN_LEVEL) * 0.8583 + 11.1111 : 0);
		default:
			return 0;
		}
	}
	
}
