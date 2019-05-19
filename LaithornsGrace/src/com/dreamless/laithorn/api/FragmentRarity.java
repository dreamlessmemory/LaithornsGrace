package com.dreamless.laithorn.api;

public enum FragmentRarity {
	DULL(0), FAINT(1), PALE(2), GLOWING(3), SPARKLING(4), BRIGHT(5), SHINING(6), RADIANT(7), INCANDESCENT(8);

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
			return 1.0;
		case FAINT:
			return 1.05;
		case PALE:
			return 1.1;
		case GLOWING:
			return 1.2;
		case SPARKLING:
			return 1.25;
		case BRIGHT:
			return 1.35;
		case SHINING:
			return 1.5;
		case RADIANT:
			return 1.75;
		case INCANDESCENT:
			return 2.0;
		default:
			return 1.0;
		}
	}
	
	public final double baseChance() {
		switch(this) {
		case DULL:
			return 90.0;
		case FAINT:
			return 5.0;
		case PALE:
			return 3.0;
		case GLOWING:
			return 1.0;
		case SPARKLING:
			return 0.75;
		case BRIGHT:
			return 0.5;
		case SHINING:
			return 0.1;
		case RADIANT:
			return 0.05;
		case INCANDESCENT:
			return 0.2;
		default:
			return 1.0;
		}
	}
	
	public final double growthRate() {
		switch(this) {
		case DULL:
			return -0.7143;
		case FAINT:
			return 0.1224;
		case PALE:
			return 0.1224;
		case GLOWING:
			return 0.1224;
		case SPARKLING:
			return 0.01046;
		case BRIGHT:
			return 0.0867;
		case SHINING:
			return 0.05;
		case RADIANT:
			return 0.0505;
		case INCANDESCENT:
			return 0.0508;
		default:
			return 0;
		}
	}
}
