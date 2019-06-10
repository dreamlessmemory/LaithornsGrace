package com.dreamless.laithorn.events;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DropTableEntry {
	
	private static double LUCK_LEVEL_MODIFIER = 0.1;
	
	public static final void setLuckLevelModifier(double luckLevelModifier) {
		LUCK_LEVEL_MODIFIER = luckLevelModifier;
	}

	private final double DROP_CHANCE;
	private final HashMap<String, Double> TAGS;
	
	public DropTableEntry(double dropChance, HashMap<String, Double> tags) {
		this.DROP_CHANCE = dropChance;
		this.TAGS = tags;
	}
	
	protected final String rollForTag(Player player, Random random) {
		return (rollForDrop(player, random) ? new WeightedRandom<String>(random, TAGS).rollValue() : null);
	}
	
	private final int getPlayerLuckLevel(Player player) {
		PotionEffect luck = player.getPotionEffect(PotionEffectType.LUCK);
		if(luck == null) {
			return 0;
		}
		return Math.max(luck.getAmplifier() + 1, 0);
	}
	
	private final boolean rollForDrop(Player player, Random random) {
		return random.nextDouble() - getPlayerLuckLevel(player) * LUCK_LEVEL_MODIFIER <= DROP_CHANCE;
	}
}
