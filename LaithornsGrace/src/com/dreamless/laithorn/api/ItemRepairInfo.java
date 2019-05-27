package com.dreamless.laithorn.api;

import org.bukkit.inventory.ItemStack;

public class ItemRepairInfo {
	private final ItemStack repairedItem;
	private final int damageRepaired;
	private final int fragmentsUsed;
	
	public ItemRepairInfo(ItemStack repairedItem, int damageRepaired, int fragmentsUsed) {
		this.repairedItem = repairedItem;
		this.damageRepaired = damageRepaired;
		this.fragmentsUsed = fragmentsUsed;
	}

	public final ItemStack getRepairedItem() {
		return repairedItem;
	}

	public final int getDamageRepaired() {
		return damageRepaired;
	}

	public final int getFragmentsUsed() {
		return fragmentsUsed;
	}
}
