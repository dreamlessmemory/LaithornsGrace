package com.dreamless.laithorn.listeners;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.events.DropTableLookup;

public class MobDeathListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onMobDeathEvent(EntityDeathEvent event) {
		LivingEntity victimEntity = event.getEntity();
		
		Player killer = victimEntity.getKiller();
		if(killer == null) {
			return;
		}
		
		if(DropTableLookup.containsDropTable(victimEntity.getType())) {
			List<ItemStack> drops = event.getDrops();
			drops.add(DropTableLookup.dropMobItems(victimEntity));
		}
	}
}
