package com.dreamless.laithorn.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.events.DropTableLookup;
import com.dreamless.laithorn.events.PlayerExperienceGainEvent;
import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;

public class FishingListener implements Listener {
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerFishEvent(PlayerFishEvent event) {
		if(event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
			return;
		}
	
	Entity caught = event.getCaught();
	if(!(caught instanceof Item)) {
		return;
	}
	Player player = event.getPlayer();
	Material material = ((Item)event.getCaught()).getItemStack().getType();
	
	if(DropTableLookup.containsDropTable(material)) {
		ItemStack drop = DropTableLookup.dropBlockItems(material, player);
		if(drop != null) {
			player.getWorld().dropItemNaturally(player.getLocation(), drop);
			Bukkit.getPluginManager().callEvent(new PlayerExperienceGainEvent(player, DropTableLookup.calculateEXPValue(drop)/10, GainType.ATTUNEMENT, false));
		}
	}
	}
	
}
