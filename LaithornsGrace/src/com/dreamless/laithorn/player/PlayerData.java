package com.dreamless.laithorn.player;

public class PlayerData {

	private int attunementEXPNeeded;
	private int attunementLevel;
	private int smithingEXPNeeded;
	private int smithingLevel;
	private int essenceStorage;
	
	public PlayerData(int attunementEXPNeeded, int attunementLevel, int smithingEXPNeeded, int smithingLevel,
			int essenceStorage) {
		this.attunementEXPNeeded = attunementEXPNeeded;
		this.attunementLevel = attunementLevel;
		this.smithingEXPNeeded = smithingEXPNeeded;
		this.smithingLevel = smithingLevel;
		this.essenceStorage = essenceStorage;
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
	
	
}
