package com.dreamless.laithorn;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataHandler {

	public static File fileReader = new File(LaithornsGrace.grace.getDataFolder(), "data.yml");
	private static FileConfiguration configuration = new YamlConfiguration();

	public static void saveWellArea(Location first, Location second) {

		configuration.set("well1", (first != null ? first.serialize() : null));
		configuration.set("well2", (second != null ? second.serialize() : null));
		try {
			configuration.save(fileReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadWellArea() {
		PlayerMessager.debugLog("Loading well...");
		
		try {
			configuration.load(fileReader);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		if(configuration.contains("well1")) {
		WellLocationHandler.loadFirstCorner(new Location(Bukkit.getWorld(configuration.getString("well1.world")),
				configuration.getDouble("well1.x"), configuration.getDouble("well1.y"),
				configuration.getDouble("well1.z")));
		}
		if(configuration.contains("well2")) {
		WellLocationHandler.loadSecondCorner(new Location(Bukkit.getWorld(configuration.getString("well2.world")),
				configuration.getDouble("well2.x"), configuration.getDouble("well2.y"),
				configuration.getDouble("well2.z")));
		}
		
		if(WellLocationHandler.bothCornersDefined())
			WellLocationHandler.calculateEdges();
	}
}
