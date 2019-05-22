package com.dreamless.laithorn.player;

import java.io.IOException;
import java.util.HashMap;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.dreamless.laithorn.LaithornUtils;
import com.dreamless.laithorn.PlayerMessager;

public class PlayerData implements InventoryHolder {

	private int attunementEXP;
	private int attunementLevel;
	private int smithingEXP;
	private int smithingLevel;
	private HashMap<String, Boolean> flags;
	private Inventory inventory;

	public PlayerData(int attunementEXP, int attunementLevel, int smithingEXPNeeded, int smithingLevel,
			HashMap<String, Boolean> flags, String inventoryString) {
		this.attunementEXP = attunementEXP;
		this.attunementLevel = attunementLevel;
		this.smithingEXP = smithingEXPNeeded;
		this.smithingLevel = smithingLevel;

		PlayerMessager.debugLog(inventoryString);
		
		if (inventoryString == "" || inventoryString == null) {
			inventory = org.bukkit.Bukkit.createInventory(this, 54, "Fragment Reservoir");
		} else {
			// Initialize Inventory
			try {
				inventory = LaithornUtils.fromBase64(inventoryString, this);
			} catch (IOException e) {
				inventory = org.bukkit.Bukkit.createInventory(this, 54, "Fragment Reservoir");
				PlayerMessager.debugLog("Error creating inventory for a player");
				e.printStackTrace();
			}
		}

		if (flags == null) {
			this.flags = new HashMap<String, Boolean>();
		} else {
			this.flags = flags;
		}
	}

	@Override
	public String toString() {
		return "Attument Level: " + attunementLevel + " Smithing Level: " + smithingLevel;
	}

	public int getAttunementEXP() {
		return attunementEXP;
	}

	public void setAttunementEXP(int attunementEXP) {
		this.attunementEXP = attunementEXP;
	}

	public int getAttunementLevel() {
		return attunementLevel;
	}

	public void setAttunementLevel(int attunementLevel) {
		this.attunementLevel = attunementLevel;
	}

	public int getSmithingEXP() {
		return smithingEXP;
	}

	public void setSmithingEXP(int smithingEXP) {
		this.smithingEXP = smithingEXP;
	}

	public int getSmithingLevel() {
		return smithingLevel;
	}

	public void setSmithingLevel(int smithingLevel) {
		this.smithingLevel = smithingLevel;
	}

	public HashMap<String, Boolean> getFlags() {
		return flags;
	}

	public boolean getFlag(String flagName) {
		return (flags.containsKey(flagName) ? flags.get(flagName) : false);
	}

	public void setFlag(String flag, boolean value) {
		flags.put(flag, value);
	}

	public void removeFlag(String flag) {
		flags.remove(flag);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
}
