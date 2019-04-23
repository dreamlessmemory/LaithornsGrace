package com.dreamless.laithorn.listeners;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dreamless.laithorn.CustomRecipes;
import com.dreamless.laithorn.LanguageReader;
import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.WellLocationHandler;
import com.dreamless.laithorn.LaithornsGrace;
import com.dreamless.laithorn.LaithornUtils;

public class CommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		String cmd = "help";
		if (args.length > 0) {
			cmd = args[0];
		}

		if (!sender.hasPermission("treewarp.admin")) {
			PlayerMessager.msg(sender, LanguageReader.getText("Error_NoPermissions"));
			return false;
		}

		if (cmd.equalsIgnoreCase("help")) {

		} else if (cmd.equalsIgnoreCase("reload")) {
			LaithornsGrace.grace.reload();
			PlayerMessager.msg(sender, LanguageReader.getText("CMD_Reload"));

		} else if (cmd.equalsIgnoreCase("fragment")) {
			cmdFragment(sender, args);
		} else if (cmd.equalsIgnoreCase("well")) {
			cmdWell(sender, args);
		} else if (cmd.equalsIgnoreCase("clear")) {
			cmdClearPlayer(sender, args);
		}
		return true;
	}

	private void cmdFragment(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			String level = "DULL";
			String type = "RAW";

			if (args.length >= 3) {
				level = args[1].toUpperCase();
				type = args[2].toUpperCase();
			} else if (args.length == 2) {
				level = args[1].toUpperCase();
				type = "RAW";
			}

			ArrayList<String> additionalFlags = new ArrayList<String>();
			for (int i = 3; i < args.length; i++) {
				additionalFlags.add(args[i].toUpperCase());
			}

			((Player) sender).getInventory().addItem(CustomRecipes.fragmentItem(level, type, additionalFlags));
		}
	}

	private void cmdClearPlayer(CommandSender sender, String[] args) {
		/**
		 * try { Player player = Bukkit.getPlayer(TreeWarpUtils.getUUID(args[1]));
		 * 
		 * if(player != null) {
		 * 
		 * Location centerLocation = CacheHandler.removePlayerFromCache(player);
		 * DatabaseHandler.removeTree(TreeWarpUtils.serializeLocation(centerLocation));
		 * PlayerMessager.msg(sender, LanguageReader.getText("CMD_Player_Cleared",
		 * args[1])); } else { PlayerMessager.msg(sender,
		 * LanguageReader.getText("CMD_Player_Not_Cleared", args[1])); } } catch
		 * (ParseException | org.json.simple.parser.ParseException e) {
		 * e.printStackTrace(); }
		 */
	}

	private void cmdWell(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) { // No console commands please
			return;
		}

		Player player = (Player) sender;

		if (args[1].equalsIgnoreCase("set")) {

			Location currentLocation = player.getLocation();
			Location targetLocationetLocation = new Location(currentLocation.getWorld(), currentLocation.getBlockX(),
					currentLocation.getBlockY(), currentLocation.getBlockZ());

			if (WellLocationHandler.addCorner(targetLocationetLocation)) {
				PlayerMessager.msg(sender, LanguageReader.getText("CMD_Well_Success"));
			} else {
				PlayerMessager.msg(sender, LanguageReader.getText("CMD_Well_Failure"));
			}
		} else if (args[1].equalsIgnoreCase("clear")) {
			WellLocationHandler.clearSpawn();
			PlayerMessager.msg(sender, LanguageReader.getText("CMD_Well_Cleared"));
		}
	}
}
