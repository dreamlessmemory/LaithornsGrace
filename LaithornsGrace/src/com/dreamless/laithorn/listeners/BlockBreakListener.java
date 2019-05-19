package com.dreamless.laithorn.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.dreamless.laithorn.events.DropTableLookup;
import com.dreamless.laithorn.events.PlayerExperienceGainEvent;
import com.dreamless.laithorn.events.PlayerExperienceVariables.GainType;

public class BlockBreakListener implements Listener{
	
	public static int PLAYER_EXP_GAIN = 10;
	public static int PLAYER_ATTUNEMENT_GAIN = 10;
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBreakEven(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(player == null) //Ignore if not player
			return;
		
		Block block = event.getBlock();
		Location location = block.getLocation();
		Material material = block.getType();
		
		if(DropTableLookup.containsDropTable(material)) {
			ItemStack drop = DropTableLookup.dropBlockItems(material, player);
			if(drop != null) {
				block.getWorld().dropItemNaturally(location, drop);
				Bukkit.getPluginManager().callEvent(new PlayerExperienceGainEvent(player, DropTableLookup.calculateEXPValue(drop)/10, GainType.ATTUNEMENT, false));
			}
		}
	}

}
