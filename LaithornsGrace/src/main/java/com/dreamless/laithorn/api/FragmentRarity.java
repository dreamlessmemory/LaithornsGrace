package com.dreamless.laithorn.api;

import com.dreamless.laithorn.player.PlayerDataHandler;

import java.util.HashMap;

public enum FragmentRarity {
	DULL(0), FAINT(1), PALE(2), GLOWING(3), SPARKLING(4), BRIGHT(5), SHINING(6), RADIANT(7), INCANDESCENT(8);

	private static int[][] WEIGHTS_MAP = null; // WEIGHTS_MAP[level][fragment_type] = weight

	// At a given player level, lower and higher shards are more rare than the "primary" shard type.
	// Ideally this should add up to 100 just for sanity purposes, so that it translates directly to % chance.
	private static final int[] shardScaling = {10, 10, 20, 30, 20, 10};

	public static void initializeWeightsMap()
	{
		int[][] weights = new int[PlayerDataHandler.LEVEL_CAP+1][INCANDESCENT.level+1];
		for (int level = 0; level <= PlayerDataHandler.LEVEL_CAP; level++) {
			for (int scaleIndex = 0; scaleIndex < shardScaling.length; scaleIndex++) {
				// At level zero, all of the weights should wind up in the DULL bucket.
				// At each higher level, the scaling curve shifts to the right by one.
				int shardType = scaleIndex - shardScaling.length + level + 1;
				// Clamp the "output" cell index so that we can simply accumulate the weights.
				shardType = Math.max(shardType, DULL.level);
				shardType = Math.min(shardType, INCANDESCENT.level);
				// Accumulate weights so that the lowest and highest levels collect a subset of the scaled weights.
				weights[level][shardType] += shardScaling[scaleIndex];
			}
		}

		WEIGHTS_MAP = weights;
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
		if (level < 0 || level > PlayerDataHandler.LEVEL_CAP) {
			return 0.0;
		}
		return WEIGHTS_MAP[level][this.level];
	}

}
