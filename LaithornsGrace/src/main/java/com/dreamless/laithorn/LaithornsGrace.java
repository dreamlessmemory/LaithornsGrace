package com.dreamless.laithorn;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.dreamless.laithorn.player.PlayerDataHandler;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.dreamless.laithorn.api.AnvilListener;
import com.dreamless.laithorn.api.CraftingBenchListener;
import com.dreamless.laithorn.api.Fragment;
import com.dreamless.laithorn.api.FragmentRarity;
import com.dreamless.laithorn.api.ItemCrafting;
import com.dreamless.laithorn.api.ItemRepair;
import com.dreamless.laithorn.api.LaithornRegister;
import com.dreamless.laithorn.events.DropTableLookup;
import com.dreamless.laithorn.events.DropTableLookup.DropType;
import com.dreamless.laithorn.events.PlayerExperienceVariables;
import com.dreamless.laithorn.listeners.BlockBreakListener;
import com.dreamless.laithorn.listeners.CommandListener;
import com.dreamless.laithorn.listeners.FishingListener;
import com.dreamless.laithorn.listeners.GrindstoneListener;
import com.dreamless.laithorn.listeners.InventoryListener;
import com.dreamless.laithorn.listeners.MobDeathListener;
import com.dreamless.laithorn.listeners.PlayerListener;
import com.dreamless.laithorn.listeners.WellListener;
import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.DatabaseHandler;
import java.sql.Connection;

public class LaithornsGrace extends JavaPlugin{

	public static LaithornsGrace grace;

	// Connection vars
	public static Connection connection; // This is the variable we will use to connect to database

	// DataBase vars.
	private String username;
	private String password;
	private String url;
	private static String database;
	private static String testdatabase;
	
	// Listeners
	private CommandListener commandListener;

	// debug
	public static boolean debug;
	public static boolean development;
	
	//Language
	public LanguageReader languageReader;
	
	@Override
	public void onEnable() {

		grace = this;

		// Load Config
		try {
			if (!readConfig()) {
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		// Load data
		DataHandler.loadWellArea();


		// SQL Setup
		try {
			connection = (Connection) DriverManager.getConnection(url, username, password);
		} catch (SQLException e) { // catching errors)
			e.printStackTrace(); // prints out SQLException errors to the console (if any)
			System.err.println("jdbc driver unavailable?");
			return;
		}
		
		// Load Cache

		// Listeners
		commandListener = new CommandListener();
		
		getCommand("givefragment").setExecutor(commandListener);
		getCommand("fragments").setExecutor(commandListener);
		getCommand("clearwell").setExecutor(commandListener);
		getCommand("setwell").setExecutor(commandListener);
		getCommand("laithornlevels").setExecutor(commandListener);
		getCommand("laithornplayerlevels").setExecutor(commandListener);
		getCommand("attunementlevel").setExecutor(commandListener);
		getCommand("smithinglevel").setExecutor(commandListener);
		getCommand("autopickup").setExecutor(commandListener);
		getCommand("loginmessage").setExecutor(commandListener);
		getCommand("bonusmessage").setExecutor(commandListener);
		getCommand("laithornreload").setExecutor(commandListener);
		
		grace.getServer().getPluginManager().registerEvents(new PlayerListener(), grace);
		grace.getServer().getPluginManager().registerEvents(new WellListener(), grace);
		grace.getServer().getPluginManager().registerEvents(new MobDeathListener(), grace);
		grace.getServer().getPluginManager().registerEvents(new BlockBreakListener(), grace);
		grace.getServer().getPluginManager().registerEvents(new FishingListener(), grace);
		grace.getServer().getPluginManager().registerEvents(new GrindstoneListener(), grace);
		grace.getServer().getPluginManager().registerEvents(new InventoryListener(), grace);
		grace.getServer().getPluginManager().registerEvents(new AnvilListener(), grace);
		grace.getServer().getPluginManager().registerEvents(new CraftingBenchListener(), grace);
		
		// Hacks
		ItemCrafting.init();
		ItemRepair.init();
		LaithornRegister.init();
		LaithornUtils.init();
		/*
		 * This init hack is to have these classes loaded as part of the Laithorn API
		 * instead of delaying its loading and breaking the package across
		 * multiple classloaders.
		 */

		// Runables
		new CacheHandler.PeriodicCacheSave().runTaskTimer(grace, 3600, 3600);
		

		new CacheHandler. PeriodicCacheRetry().runTaskTimer(grace, 1200, 1200);

		PlayerMessager.log(this.getDescription().getName() + " enabled!");
	}

	@Override
	public void onDisable() {

		// Save data
		CacheHandler.saveCacheToDatabase();
		
		// Disable listeners
		HandlerList.unregisterAll(this);

		// Stop shedulers
		getServer().getScheduler().cancelTasks(this);

		if (grace == null) {
			return;
		}

		// Disable Server
		try { // using a try catch to catch connection errors (like wrong sql password...)
			if (connection != null && !connection.isClosed()) { // checking if connection isn't null to
				// avoid receiving a nullpointer
				connection.close(); // closing the connection field variable.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		PlayerMessager.log(this.getDescription().getName() + " disabled!");

	}
	
	private boolean readConfig() {
		
		/*** config.yml ***/
		File currentFile = new File(grace.getDataFolder(), "config.yml");
		if (!currentFile.exists()) {
			return false;
		}
		FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(currentFile);

		// Database settings
		username = currentConfig.getString("username");
		password = currentConfig.getString("password");
		url = currentConfig.getString("url");
		database = currentConfig.getString("prefix");
		testdatabase = currentConfig.getString("testprefix");

		// Dev/Debug control
		debug = currentConfig.getBoolean("debug", false);
		development = currentConfig.getBoolean("development", false);
		
		// Control
		Fragment.setFragmentMaterial(Material.getMaterial(currentConfig.getString("material", "FLINT")));
		
		// Balancing
		ConfigurationSection tagEXP = currentConfig.getConfigurationSection("tag_experience");
		PlayerExperienceVariables.setFragmentExp(tagEXP.getInt("WELLSPRING", 10));
		PlayerExperienceVariables.setBonusExp(tagEXP.getInt("BONUS_EXP", 10));
		PlayerExperienceVariables.setDropExp(tagEXP.getInt("DROP", 1));
		PlayerDataHandler.setLevelingConfiguration(
				tagEXP.getInt("LEVEL_ONE_STACKS", 2),
				tagEXP.getInt("LEVEL_MAX_STACKS", 150),
				tagEXP.getInt("LEVEL_CAP", 10),
				PlayerExperienceVariables.getFragmentExp()*64
		);
		
		FragmentRarity.initializeWeightsMap();
		
		DatabaseHandler.setBonusCap(tagEXP.getInt("BONUS_CAP", 640));
		DatabaseHandler.setDailyBonus(tagEXP.getInt("DAILY_BONUS", 64));
		
		// Parse loot tables
		currentFile = new File(grace.getDataFolder(), "tags.yml");
		if (currentFile.exists()) {
			DropTableLookup.loadTagTables(YamlConfiguration.loadConfiguration(currentFile));
		}
		
		currentFile = new File(grace.getDataFolder(), "mobs.yml");
		if (currentFile.exists()) {
			DropTableLookup.loadDropTables(YamlConfiguration.loadConfiguration(currentFile), DropType.MOB);
		}
		
		currentFile = new File(grace.getDataFolder(), "blocks.yml");
		if (currentFile.exists()) {
			DropTableLookup.loadDropTables(YamlConfiguration.loadConfiguration(currentFile), DropType.BLOCK);
		}
		
	
		/*** text.yml ***/
		currentFile = new File(grace.getDataFolder(), "text.yml");
		if (!currentFile.exists()) {
			return false;
		}
		
		LanguageReader.loadEntries(currentFile);
		
		// Continuous
		//new EffectHandler.EffectContinuousRunnable().runTaskTimer(grace, 20, EffectHandler.AMBIENT_EFFECT_INTERVAL * 20);

		return true;
	}
	
	public  void reload() {	
		try {
			if (!grace.readConfig()) {
				grace = null;
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			grace = null;
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
	}

	public static String getDatabase() {
			return development ? testdatabase : database;

	}
}
