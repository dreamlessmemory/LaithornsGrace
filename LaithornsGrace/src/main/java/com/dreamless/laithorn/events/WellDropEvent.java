package com.dreamless.laithorn.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.WellLocationHandler;
import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;

public class WellDropEvent extends BukkitRunnable {

	private Player player;
	private Item item;

	public WellDropEvent(Player player, Item item) {
		this.player = player;
		this.item = item;
	}

	@Override
	public void run() {
		Location dropLocation = item.getLocation();
		if (!WellLocationHandler.checkIfInWell(dropLocation)) {
			PlayerMessager.debugLog("Not into the well");
			return; // It didn't go into the well
		}

		item.remove();

		ItemStack itemStack = item.getItemStack();

		// Drop items
		List<ItemStack> drops = DropTableLookup.dropItems(itemStack);
		if (drops != null) {
			// Drop Item
			dropLocation.getWorld().playSound(dropLocation, Sound.ENTITY_PLAYER_LEVELUP, 0.15f, 0.5f);
			for (ItemStack drop : drops) {
				dropLocation.getWorld().dropItemNaturally(dropLocation.clone().add(0, 1, 0), drop);
				dropLocation.getWorld().spawnParticle(Particle.END_ROD, dropLocation.clone().add(0, 1, 0), 15, 0.5, 0.5,
						0.5);
			}
			
			// Give EXP
			Bukkit.getPluginManager().callEvent(new PlayerExperienceGainEvent(player,
					DropTableLookup.calculateEXPValue(itemStack), GainType.ATTUNEMENT, true));
		}
	}

}
