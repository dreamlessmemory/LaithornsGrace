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

	public static void saveSpawnArea(Location first, Location second) {

		configuration.set("spawn1", (first != null ? first.serialize() : null));
		configuration.set("spawn2", (second != null ? second.serialize() : null));
		try {
			configuration.save(fileReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadSpawnArea() {
		PlayerMessager.debugLog("Loading spawn...");
		
		try {
			configuration.load(fileReader);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		if(configuration.contains("spawn1")) {
		WellHandler.loadFirstCorner(new Location(Bukkit.getWorld(configuration.getString("spawn1.world")),
				configuration.getDouble("spawn1.x"), configuration.getDouble("spawn1.y"),
				configuration.getDouble("spawn1.z")));
		}
		if(configuration.contains("spawn2")) {
		WellHandler.loadSecondCorner(new Location(Bukkit.getWorld(configuration.getString("spawn2.world")),
				configuration.getDouble("spawn2.x"), configuration.getDouble("spawn2.y"),
				configuration.getDouble("spawn2.z")));
		}
	}
}
