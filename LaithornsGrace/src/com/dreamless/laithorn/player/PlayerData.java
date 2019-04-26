package com.dreamless.laithorn.player;

import java.util.HashMap;

public class PlayerData {

	private int attunementEXP;
	private int attunementLevel;
	private int smithingEXP;
	private int smithingLevel;
	private int essenceStorage;
	private HashMap<String, Boolean> flags;

	public PlayerData(int attunementEXP, int attunementLevel, int smithingEXPNeeded, int smithingLevel,
			int essenceStorage, HashMap<String, Boolean> flags) {
		this.attunementEXP = attunementEXP;
		this.attunementLevel = attunementLevel;
		this.smithingEXP = smithingEXPNeeded;
		this.smithingLevel = smithingLevel;
		this.essenceStorage = essenceStorage;
		if (flags == null) {
			this.flags = new HashMap<String, Boolean>();
		} else {
			this.flags = flags;
		}
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
