package com.dreamless.laithorn.events;

import java.util.HashMap;
import java.util.Map.Entry;

import com.dreamless.laithorn.api.FragmentRarity;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class WeightedRandom <E>{
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public WeightedRandom(Random random, HashMap<E, Double> tags) {
        this.random = random;
        for(Entry<E, Double> entry : tags.entrySet()) {
        	add(entry.getValue(), entry.getKey());
        }
    }
    
    @SuppressWarnings("unchecked")
	public WeightedRandom(Random random, int level) {
        this.random = random;
        for(FragmentRarity rarity : FragmentRarity.values()) {
			add(rarity.weightedDropChance(level), (E) rarity);
		}
    }

	public WeightedRandom<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E rollValue() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}
