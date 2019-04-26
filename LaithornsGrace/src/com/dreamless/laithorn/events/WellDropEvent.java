package com.dreamless.laithorn.events;

import java.util.ArrayList;
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

import de.tr7zw.itemnbtapi.NBTCompound;
import de.tr7zw.itemnbtapi.NBTItem;

public class WellDropEvent extends BukkitRunnable {

	private Player player;
	private Item item;

	public WellDropEvent(Player player, Item item) {
		this.player = player;
		this.item = item;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		Location dropLocation = item.getLocation();
		if (!WellLocationHandler.checkIfInWell(dropLocation)) {
			PlayerMessager.debugLog("Not into the well");
			return; // It didn't go into the well
		}

		ItemStack itemStack = item.getItemStack();
		ArrayList<String> keywords = new ArrayList<String>();

		NBTItem nbti = new NBTItem(itemStack);
		NBTCompound laithorn = nbti.getCompound("Laithorn");
		if (laithorn == null) {
			PlayerMessager.debugLog("No dice, what's wrong?");
			return;
		}

		String level = laithorn.getString("level");
		keywords.add(laithorn.getString("type"));

		for (String key : laithorn.getKeys()) {
			if (key.contains("Loot_"))
				keywords.add(laithorn.getString(key));
		}

		item.remove();

		// Drop items
		List<ItemStack> drops = DropTableLookup.dropItems(keywords, level);
		dropLocation.getWorld().playSound(dropLocation, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);
		for (ItemStack drop : drops) {
			dropLocation.getWorld().dropItemNaturally(dropLocation.clone().add(0, 1, 0), drop);
			dropLocation.getWorld().spawnParticle(Particle.END_ROD, dropLocation.clone().add(0, 1, 0), 15, 0.5, 0.5,
					0.5);
		}
		
		// Calculate EXP
		int expGain = DropTableLookup.calculateEXPValue(keywords, level);

		// Give EXP
		Bukkit.getPluginManager().callEvent(new PlayerExperienceGainEvent(player, expGain, GainType.ATTUNEMENT));

	}

}
