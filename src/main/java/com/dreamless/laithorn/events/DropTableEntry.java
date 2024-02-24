package com.dreamless.laithorn.events;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DropTableEntry {
	
	private static double LUCK_LEVEL_MODIFIER = 0.1;
	private static double BOOST_MODIFIER = 0.1;
	
	public static final void setLuckLevelModifier(double luckLevelModifier) {
		LUCK_LEVEL_MODIFIER = luckLevelModifier;
	}

	public static final void setBoostModifier(double boostModifier) {
		BOOST_MODIFIER = boostModifier;
	}

	private final double DROP_CHANCE;
	private final HashMap<String, Double> TAGS;
	
	public DropTableEntry(double dropChance, HashMap<String, Double> tags) {
		this.DROP_CHANCE = dropChance;
		this.TAGS = tags;
	}
	
	protected final String rollForTag(Player player, Random random, boolean isBoosted) {
		return (rollForDrop(player, random, isBoosted) ? new WeightedRandom<String>(random, TAGS).rollValue() : null);
	}
	
	private final int getPlayerLuckLevel(Player player) {
		PotionEffect luck = player.getPotionEffect(PotionEffectType.LUCK);
		if(luck == null) {
			return 0;
		}
		return Math.max(luck.getAmplifier() + 1, 0);
	}
	
	private final boolean rollForDrop(Player player, Random random, boolean isBoosted) {
		// The base drop chance
		double finalDropChance = DROP_CHANCE;

		// Add any bonuses for luck
		finalDropChance += getPlayerLuckLevel(player) * LUCK_LEVEL_MODIFIER;
		
		// If boosted, add another drop chance
		finalDropChance += isBoosted ? BOOST_MODIFIER : 0;

		// If the roll is less than the drop chance, it succeeds.
		// e.g. a 15% drop chance means 0-0.15 will succeed, but 0.3 will fail
		return random.nextDouble() <= finalDropChance;
	}
}
