package com.dreamless.laithorn;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageReader {
	private static Map<String, String> entries = new TreeMap<String, String>();

	public static void loadEntries(File file) {
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		Set<String> keySet = configFile.getKeys(false);
		for (String key : keySet) {
			entries.put(key, configFile.getString(key));
		}
	}

	public static String getText(String key, String... args) {
		String entry = entries.get(key);

		if (entry != null) {
			int i = 0;
			for (String arg : args) {
				if (arg != null) {
					i++;
					entry = entry.replace("&v" + i, arg);
				}
			}
		} else {
			entry = "%No text for: '" + key + "'%";
		}

		return entry;
	}
	
	public static boolean containsEntry(String key) {
		return entries.containsKey(key);
	}
}
