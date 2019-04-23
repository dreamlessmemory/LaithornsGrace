package com.dreamless.laithorn.events;

import java.util.List;

public class DropTableEntry {

	public List<LootPool> pools;
	
	public class LootPool {
		public final String getItem() {
			return item;
		}
		public final int getMin() {
			return min;
		}
		public final int getMax() {
			return max;
		}
		public final int getChance() {
			return chance;
		}
		public String item;
		public int min;
		public int max;
		public int chance;
	}
}
