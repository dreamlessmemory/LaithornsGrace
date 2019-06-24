package com.dreamless.laithorn.events;

public class LootPool {
	public String item;
	public int min;
	public int max;
	public double chance;
	
	public LootPool(String item, int min, int max, double chance) {
		this.item = item;
		this.min = min;
		this.max = max;
		this.chance = chance;
	}
	public final String getItem() {
		return item;
	}
	public final int getMin() {
		return min;
	}
	public final int getMax() {
		return max;
	}
	public final double getChance() {
		return chance;
	}
	@Override
	public String toString() {
		return "LootPoolClass [item=" + item + ", min=" + min + ", max=" + max + ", chance=" + chance + "]";
	}
}