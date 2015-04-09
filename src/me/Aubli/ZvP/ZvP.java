package me.Aubli.ZvP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Listeners.AsyncChatListener;
import me.Aubli.ZvP.Listeners.BlockListener;
import me.Aubli.ZvP.Listeners.DeathListener;
import me.Aubli.ZvP.Listeners.EntityDamageListener;
import me.Aubli.ZvP.Listeners.GUIListener;
import me.Aubli.ZvP.Listeners.PlayerConnectionListener;
import me.Aubli.ZvP.Listeners.PlayerInteractListener;
import me.Aubli.ZvP.Listeners.PlayerRespawnListener;
import me.Aubli.ZvP.Listeners.SignChangelistener;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.util.Converter.FileConverter;
import org.util.Logger.PluginOutput;
import org.util.Metrics.Metrics;
import org.util.Updater.Updater;
import org.util.Updater.Updater.UpdateResult;
import org.util.Updater.Updater.UpdateType;


public class ZvP extends JavaPlugin {
    
    private static PluginOutput logger;
    
    private static FileConverter converter;
    
    private static ZvP instance;
    
    public static String ADDARENA = "Use this tool to add an arena!";
    public static String ADDPOSITION = "Use this tool to add a spawn position";
    
    private static String pluginPrefix = ChatColor.DARK_GREEN + "[" + ChatColor.DARK_RED + "Z" + ChatColor.DARK_GRAY + "v" + ChatColor.DARK_RED + "P" + ChatColor.DARK_GREEN + "]" + ChatColor.RESET + " ";
    
    private int pluginID = 59021;
    
    public static boolean updateAvailable = false;
    public static String newVersion = "";
    
    @Override
    public void onDisable() {
	
	for (Player p : Bukkit.getOnlinePlayers()) {
	    removeTool(p);
	}
	GameManager.getManager().shutdown();
	
	logger.log("Plugin is disabled!", false);
    }
    
    @Override
    public void onEnable() {
	initialize();
	
	logger.log("Plugin is enabled!", false);
    }
    
    private void initialize() {
	instance = this;
	
	new ZvPConfig(getConfig());
	logger = new PluginOutput(this, ZvPConfig.getDebugMode(), ZvPConfig.getLogLevel());
	
	converter = new FileConverter(this);
	
	new MessageManager(ZvPConfig.getLocale());
	new GameManager();
	new SignManager();
	new ShopManager();
	new KitManager(ZvPConfig.getEnableKits());
	
	registerListeners();
	getCommand("zvp").setExecutor(new ZvPCommands());
	getCommand("zvptest").setExecutor(new ZvPCommands());
	
	if (ZvPConfig.getEnableUpdater()) {
	    UpdateType updType = UpdateType.DEFAULT;
	    
	    if (ZvPConfig.getAutoUpdate() == false) {
		updType = UpdateType.NO_DOWNLOAD;
	    }
	    
	    Updater upd = new Updater(this, this.pluginID, this.getFile(), updType, ZvPConfig.getlogUpdate());
	    
	    if (ZvPConfig.getAutoUpdate() == false) {
		updateAvailable = (upd.getResult() == UpdateResult.UPDATE_AVAILABLE);
		newVersion = upd.getLatestName();
	    }
	}
	
	if (ZvPConfig.getUseMetrics() == true) {
	    try {
		Metrics metrics = new Metrics(this);
		metrics.start();
	    } catch (IOException e) {
		logger.log(Level.WARNING, "Can't start Metrics! Skip!", true, false, e);
	    }
	}
    }
    
    private void registerListeners() {
	PluginManager pm = Bukkit.getPluginManager();
	
	pm.registerEvents(new BlockListener(), this);
	pm.registerEvents(new DeathListener(), this);
	pm.registerEvents(new PlayerInteractListener(), this);
	pm.registerEvents(new PlayerConnectionListener(), this);
	pm.registerEvents(new PlayerRespawnListener(), this);
	pm.registerEvents(new SignChangelistener(), this);
	pm.registerEvents(new GUIListener(), this);
	pm.registerEvents(new EntityDamageListener(), this);
	pm.registerEvents(new AsyncChatListener(), this);
    }
    
    public static ZvP getInstance() {
	return instance;
    }
    
    public static PluginOutput getPluginLogger() {
	return logger;
    }
    
    public static FileConverter getConverter() {
	return converter;
    }
    
    public static String getPrefix() {
	return pluginPrefix;
    }
    
    public void updatePlugin() {
	if (ZvPConfig.getEnableUpdater() && updateAvailable) {
	    new Updater(this, this.pluginID, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, ZvPConfig.getlogUpdate());
	}
    }
    
    public static ItemStack getTool(String loreString) {
	ItemStack tool = new ItemStack(Material.STICK);
	
	List<String> lore = new ArrayList<String>();
	
	ItemMeta toolMeta = tool.getItemMeta();
	toolMeta.setDisplayName(pluginPrefix + ChatColor.BOLD + "Tool");
	toolMeta.addEnchant(Enchantment.DURABILITY, 5, true);
	lore.add(ChatColor.GOLD + loreString);
	toolMeta.setLore(lore);
	
	tool.setItemMeta(toolMeta);
	return tool;
    }
    
    public static boolean removeTool(Player player) {
	if (player.getInventory().contains(getTool(ADDARENA))) {
	    player.getInventory().removeItem(getTool(ADDARENA));
	    return true;
	} else if (player.getInventory().contains(getTool(ADDPOSITION))) {
	    player.getInventory().removeItem(getTool(ADDPOSITION));
	    return true;
	}
	return false;
    }
}
