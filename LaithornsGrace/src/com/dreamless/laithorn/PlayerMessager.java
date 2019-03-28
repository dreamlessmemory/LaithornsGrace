package com.dreamless.laithorn;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PlayerMessager {
	public static void msg(CommandSender sender, String msg) {
		sender.sendMessage(color("&2[Laithorn] &f" + msg));
	}

	public static void msg(CommandSender sender, ArrayList<String> msg) {
		sender.sendMessage(msg.toArray(new String[msg.size()]));
	}

	public static void msgMult(CommandSender sender, String msg) {
		sender.sendMessage(msg.split("\n"));
	}

	public static void log(String msg) {
		msg(Bukkit.getConsoleSender(), msg);
	}

	public static void debugLog(String msg) {
		if (LaithornsGrace.debug) {
			msg(Bukkit.getConsoleSender(), "&2[Debug] &f" + msg);
		}
	}

	public static String color(String msg) {
		if (msg != null) {
			msg = ChatColor.translateAlternateColorCodes('&', msg);
		}
		return msg;
	}
}
