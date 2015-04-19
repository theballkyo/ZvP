package me.Aubli.ZvP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;


public class ZvPConfig {
    
    private static FileConfiguration config;
    
    private static boolean useMetrics = true;
    private static boolean debugMode = false;
    private static Locale locale = null;
    private static int logLevel = Level.INFO.intValue();
    
    private static boolean enableUpdater = true;
    private static boolean autoUpdate = true;
    private static boolean logUpdate = false;
    
    private static boolean enableKits = true;
    private static boolean enableFirework = true;
    private static boolean useVoteSystem = true;
    private static boolean separatePlayerScores = false;
    private static int maxPlayers;
    private static int defaultRounds;
    private static int defaultWaves;
    
    private static int startDelay;
    private static int breakTime;
    
    private static int defaultZombieSpawnRate;
    private static double defaultSaveRadius;
    
    private static double zombieFund;
    private static double deathFee;
    
    public ZvPConfig(FileConfiguration configuration) {
	config = configuration;
	init();
	load();
    }
    
    private static void init() {
	File configFile = new File(ZvP.getInstance().getDataFolder(), "config.yml");
	getConfig().options().header("\n" + "This is the main config file for ZombieVsPlayer.\n" + "For more information read the comments or visit the Bukkit page:\n" + "http://dev.bukkit.org/bukkit-plugins/zombievsplayer/\n");
	
	getConfig().addDefault("plugin.enableMetrics", true);
	getConfig().addDefault("plugin.Locale", "en");
	getConfig().addDefault("plugin.debugMode", false);
	getConfig().addDefault("plugin.loglevel", Level.FINE.intValue());
	
	getConfig().addDefault("plugin.update.enable", true);
	getConfig().addDefault("plugin.update.autoUpdate", true);
	getConfig().addDefault("plugin.update.showUpdateInConsole", true);
	
	getConfig().addDefault("game.enableKits", true);
	getConfig().addDefault("game.enableFirework", true);
	getConfig().addDefault("game.useVoteSystem", true);
	getConfig().addDefault("game.separatePlayerScores", false);
	getConfig().addDefault("game.maximal_Players", 25);
	getConfig().addDefault("game.default_rounds", 2);
	getConfig().addDefault("game.default_waves", 4);
	
	getConfig().addDefault("times.joinTime", 15);
	getConfig().addDefault("times.timeBetweenWaves", 90);
	
	getConfig().addDefault("zombies.default_spawnRate", 8);
	getConfig().addDefault("zombies.default_saveRadius", 3.5);
	
	getConfig().addDefault("money.ZombieFund", 0.37);
	getConfig().addDefault("money.DeathFee", 3);
	saveConfig();
	
	insertComment(configFile, "enableKits", "Enable kits for the game.#If disabled player will start the game with their current items.#The inventory will be restored after the game.");
	insertComment(configFile, "enableFirework", "Fireworks will shoot when the game ends.#Note that Fireworks take extra time!");
	insertComment(configFile, "useVoteSystem", "Use votes to get to the next round.#If false the game will wait timeBetweenWaves in seconds.");
	insertComment(configFile, "separatePlayerScores", "True: Each player will have his own score.#False: All players have the same score. They pay and earn together.");
	insertComment(configFile, "maximal_Players", "Maximal amount of players in an arena.");
	insertComment(configFile, "default_rounds", "Amount of rounds a newly created arena will have by default.");
	insertComment(configFile, "default_waves", "Amount of waves a newly created arena will have by default.");
	insertComment(configFile, "joinTime", "Time in seconds the game will wait before it starts.#Note that the arena specific minimum has to be reached.");
	insertComment(configFile, "timeBetweenWaves", "Time in seconds the game will wait until a new wave starts.#Only applies if useVoteSystem is false!");
	insertComment(configFile, "default_spawnRate", "Default zombie spawnrate for newly created arenas.#The spawnrate defines how many zombies will spawn.#The calculation uses arena size, amount of player, spawnrate and difficulty setting.");
	insertComment(configFile, "default_saveRadius", "Default zombie spawnrate for newly created arenas.#The save radius is the radius in blocks in which no zombies will spawn.");
	insertComment(configFile, "ZombieFund", "Amount of money you will get from killing a zombie.");
	insertComment(configFile, "DeathFee", "Amount of money you have to pay when you die.");
	
    }
    
    private static void load() {
	useMetrics = getConfig().getBoolean("plugin.enableMetrics", true);
	debugMode = getConfig().getBoolean("plugin.debugMode", false);
	locale = new Locale(getConfig().getString("plugin.Locale", "en"));
	logLevel = getConfig().getInt("plugin.loglevel", 500);
	
	enableUpdater = getConfig().getBoolean("plugin.update.enable", true);
	autoUpdate = getConfig().getBoolean("plugin.update.autoUpdate", true);
	logUpdate = getConfig().getBoolean("plugin.update.showUpdateInConsole", false);
	
	enableKits = getConfig().getBoolean("game.enableKits", true);
	enableFirework = getConfig().getBoolean("game.enableFirework", true);
	useVoteSystem = getConfig().getBoolean("game.useVoteSystem", true);
	separatePlayerScores = getConfig().getBoolean("game.separatePlayerScores", false);
	maxPlayers = getConfig().getInt("game.maximal_Players");
	defaultRounds = getConfig().getInt("game.default_rounds");
	defaultWaves = getConfig().getInt("game.default_waves");
	
	startDelay = getConfig().getInt("times.joinTime");
	breakTime = getConfig().getInt("times.timeBetweenWaves");
	
	defaultZombieSpawnRate = getConfig().getInt("zombies.default_spawnRate");
	defaultSaveRadius = getConfig().getDouble("zombies.default_saveRadius");
	
	zombieFund = getConfig().getDouble("money.ZombieFund");
	deathFee = getConfig().getDouble("money.DeathFee");
	
	// this.getConfig().addDefault("config.misc.portOnJoinGame", true);
	// this.getConfig().addDefault("config.misc.changeToSpectatorAfterDeath", false);
    }
    
    public static void reloadConfig() {
	ZvP.getInstance().reloadConfig();
	config = ZvP.getInstance().getConfig();
	init();
	load();
    }
    
    public static boolean getUseMetrics() {
	return useMetrics;
    }
    
    public static boolean getDebugMode() {
	return debugMode;
    }
    
    public static boolean getEnableUpdater() {
	return enableUpdater;
    }
    
    public static boolean getAutoUpdate() {
	return autoUpdate;
    }
    
    public static boolean getlogUpdate() {
	return logUpdate;
    }
    
    public static boolean getEnableKits() {
	return enableKits;
    }
    
    public static boolean getEnableFirework() {
	return enableFirework;
    }
    
    public static boolean getSeparatePlayerScores() {
	return separatePlayerScores;
    }
    
    public static boolean getUseVoteSystem() {
	return useVoteSystem;
    }
    
    public static Locale getLocale() {
	return locale;
    }
    
    public static int getLogLevel() {
	return logLevel;
    }
    
    public static int getMaxPlayers() {
	return maxPlayers;
    }
    
    public static int getDefaultRounds() {
	return defaultRounds;
    }
    
    public static int getDefaultWaves() {
	return defaultWaves;
    }
    
    public static int getStartDelay() {
	return startDelay;
    }
    
    public static int getBreakTime() {
	return breakTime;
    }
    
    public static int getDefaultZombieSpawnRate() {
	return defaultZombieSpawnRate;
    }
    
    public static double getDefaultSaveRadius() {
	return defaultSaveRadius;
    }
    
    public static double getZombieFund() {
	return zombieFund;
    }
    
    public static double getDeathFee() {
	return deathFee;
    }
    
    private static FileConfiguration getConfig() {
	return config;
    }
    
    private static void saveConfig() {
	getConfig().options().copyDefaults(true);
	ZvP.getInstance().saveConfig();
    }
    
    public static boolean insertComment(File file, String option, String comment) {
	comment = comment.replace("#", "\n# ");
	comment = "\n# " + comment;
	
	try {
	    ArrayList<String> fileContent = new ArrayList<String>();
	    Scanner scan = new Scanner(file);
	    
	    while (scan.hasNextLine()) {
		String line = scan.nextLine();
		
		if (line.trim().split(":")[0].equalsIgnoreCase(option)) {
		    fileContent.add(comment);
		}
		fileContent.add(line);
	    }
	    
	    PrintWriter writer = new PrintWriter(file);
	    for (String string : fileContent) {
		writer.write(string + "\n");
	    }
	    writer.flush();
	    writer.close();
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	
	return false;
    }
}
