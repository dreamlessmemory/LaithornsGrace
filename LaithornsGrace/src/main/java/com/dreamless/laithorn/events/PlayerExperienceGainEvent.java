package com.dreamless.laithorn.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;

public class PlayerExperienceGainEvent extends Event implements Cancellable {

	private final Player player;
	private final GainType gainType;
	private final int expGain;
	private final boolean informPlayer;

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private boolean isCancelled;

	public final Player getPlayer() {
		return player;
	}

	public final GainType getGainType() {
		return gainType;
	}

	public final int getExpGain() {
		return expGain;
	}

	public static final HandlerList getHandlersList() {
		return HANDLERS_LIST;
	}

	public PlayerExperienceGainEvent(Player player, int expGain, GainType gainType, boolean informPlayer) {
		this.player = player;
		this.gainType = gainType;
		this.expGain = expGain;
		this.informPlayer = informPlayer;
	}

	public final boolean shouldInformPlayer() {
		return informPlayer;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;

	}

	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

}
