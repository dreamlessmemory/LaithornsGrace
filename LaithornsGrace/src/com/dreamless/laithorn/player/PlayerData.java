package com.dreamless.laithorn.player;

import java.util.HashMap;

public class PlayerData {

	private int attunementEXPNeeded;
	private int attunementLevel;
	private int smithingEXPNeeded;
	private int smithingLevel;
	private int essenceStorage;
	private HashMap<String, Boolean> flags;

	public PlayerData(int attunementEXPNeeded, int attunementLevel, int smithingEXPNeeded, int smithingLevel,
			int essenceStorage, HashMap<String, Boolean> flags) {
		this.attunementEXPNeeded = attunementEXPNeeded;
		this.attunementLevel = attunementLevel;
		this.smithingEXPNeeded = smithingEXPNeeded;
		this.smithingLevel = smithingLevel;
		this.essenceStorage = essenceStorage;
		if (flags == null) {
			this.flags = new HashMap<String, Boolean>();
		} else {
			this.flags = flags;
		}
	}

	public int getAttunementEXPNeeded() {
		return attunementEXPNeeded;
	}

	public void setAttunementEXPNeeded(int attunementEXPNeeded) {
		this.attunementEXPNeeded = attunementEXPNeeded;
	}

	public int getAttunementLevel() {
		return attunementLevel;
	}

	public void setAttunementLevel(int attunementLevel) {
		this.attunementLevel = attunementLevel;
	}

	public int getSmithingEXPNeeded() {
		return smithingEXPNeeded;
	}

	public void setSmithingEXPNeeded(int smithingEXPNeeded) {
		this.smithingEXPNeeded = smithingEXPNeeded;
	}

	public int getSmithingLevel() {
		return smithingLevel;
	}

	public void setSmithingLevel(int smithingLevel) {
		this.smithingLevel = smithingLevel;
	}

	public int getEssenceStorage() {
		return essenceStorage;
	}

	public void setEssenceStorage(int essenceStorage) {
		this.essenceStorage = essenceStorage;
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
}
