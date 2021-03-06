package me.Aubli.ZvP.Game;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPConfig;
import me.Aubli.ZvP.Game.GameEnums.ArenaStatus;
import me.Aubli.ZvP.Game.ArenaParts.ArenaArea;
import me.Aubli.ZvP.Sign.ISign;
import me.Aubli.ZvP.Sign.SignManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.util.File.Converter.FileConverter.FileType;


public class GameManager {
    
    private static GameManager instance;
    private String arenaPath;
    private String lobbyPath;
    
    private FilenameFilter fileFilter;
    
    private ArrayList<Lobby> lobbys;
    private ArrayList<Arena> arenas;
    
    private ScoreboardManager boardman;
    
    private GameManager() {
	this.arenaPath = ZvP.getInstance().getDataFolder().getPath() + "/Arenas";
	this.lobbyPath = ZvP.getInstance().getDataFolder().getPath() + "/Lobbys";
	
	this.boardman = Bukkit.getScoreboardManager();
	
	this.fileFilter = new FilenameFilter() {
	    
	    @Override
	    public boolean accept(File dir, String name) {
		if (name.contains(".old")) {
		    return false;
		}
		return true;
	    }
	};
	
	loadConfig();
    }
    
    public static synchronized GameManager init() {
	if (instance == null) {
	    instance = new GameManager();
	}
	return instance;
    }
    
    public static GameManager getManager() {
	return init();
    }
    
    public void shutdown() {
	stopGames();
    }
    
    public void reloadGameManager() {
	stopGames();
	loadConfig();
    }
    
    // Config
    private void loadConfig() {
	
	if (!new File(this.arenaPath).exists() || !new File(this.lobbyPath).exists()) {
	    new File(this.arenaPath).mkdirs();
	    new File(this.lobbyPath).mkdirs();
	}
	
	this.lobbys = new ArrayList<Lobby>();
	this.arenas = new ArrayList<Arena>();
	
	loadArenas();
	loadLobbys();
    }
    
    // Load and save
    private void loadArenas() {
	for (File arenaFile : new File(this.arenaPath).listFiles(this.fileFilter)) {
	    // Version 2.4.0 needs converted arena files
	    // Version 2.6.0 needs converted arena files too
	    // version 2.7.0 needs converted positions in arena file
	    // version 2.8.0 has new config values --> ordering
	    // version 2.8.3 has new config value --> new order
	    // version 2.9.0 has new config value(ArenaMode) --> new order
	    ZvP.getConverter().convert(FileType.ARENAFILE, arenaFile, 290.0);
	    
	    try {
		Arena arena = new Arena(arenaFile);
		if (arena.getWorld() != null) {
		    this.arenas.add(arena);
		    arena.getConfig().saveConfig();
		}
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(ArenaArea.class, Level.SEVERE, "Error while loading Arena " + arenaFile.getName().split(".y")[0] + ": " + e.getMessage(), true, false, e);
	    }
	}
    }
    
    private void loadLobbys() {
	for (File lobbyFile : new File(this.lobbyPath).listFiles()) {
	    Lobby lobby = new Lobby(lobbyFile);
	    
	    if (lobby.getWorld() != null) {
		this.lobbys.add(lobby);
	    }
	}
    }
    
    // Method to get new UIDs
    public int getNewID(String path) {
	
	File folder = new File(path);
	File[] files = folder.listFiles(this.fileFilter);
	if (files.length == 0) {
	    return 1;
	} else {
	    
	    int[] fileIds = new int[files.length];
	    
	    for (int i = 0; i < fileIds.length; i++) {
		fileIds[i] = Integer.parseInt(files[i].getName().split(".ym")[0]);
	    }
	    
	    Arrays.sort(fileIds);
	    
	    for (int k = 0; k < fileIds.length; k++) {
		if (fileIds[k] != (k + 1)) {
		    return (k + 1);
		}
	    }
	    return fileIds.length + 1;
	}
	
    }
    
    // arena, lobby getter methods
    public Arena[] getArenas() {
	Arena[] array = new Arena[this.arenas.size()];
	
	for (int i = 0; i < this.arenas.size(); i++) {
	    array[i] = this.arenas.get(i);
	}
	Arrays.sort(array);
	return array;
    }
    
    public Lobby[] getLobbys() {
	Lobby[] array = new Lobby[this.lobbys.size()];
	
	for (int i = 0; i < this.lobbys.size(); i++) {
	    array[i] = this.lobbys.get(i);
	}
	Arrays.sort(array);
	return array;
    }
    
    public Arena getArena(int ID) {
	for (Arena a : getArenas()) {
	    if (a.getID() == ID) {
		return a;
	    }
	}
	return null;
    }
    
    public Lobby getLobby(int ID) {
	for (Lobby l : getLobbys()) {
	    if (l.getID() == ID) {
		return l;
	    }
	}
	return null;
    }
    
    // get ZvPPlayer from Player
    public ZvPPlayer getPlayer(Player player) {
	for (Arena a : getArenas()) {
	    for (ZvPPlayer zp : a.getPlayers()) {
		if (zp.getUuid().equals(player.getUniqueId())) {
		    return zp;
		}
	    }
	    if (a.hasPreLobby()) {
		for (ZvPPlayer zp : a.getPreLobby().getPlayers()) {
		    if (zp.getUuid().equals(player.getUniqueId())) {
			return zp;
		    }
		}
	    }
	}
	return null;
    }
    
    public ZvPPlayer getPlayer(String playerName) {
	for (Arena a : getArenas()) {
	    for (ZvPPlayer zp : a.getPlayers()) {
		if (zp.getName().equals(playerName)) {
		    return zp;
		}
	    }
	    if (a.hasPreLobby()) {
		for (ZvPPlayer zp : a.getPreLobby().getPlayers()) {
		    if (zp.getName().equals(playerName)) {
			return zp;
		    }
		}
	    }
	}
	return null;
    }
    
    // get ZvPPlayer from UUID
    public ZvPPlayer getPlayer(UUID uuid) {
	return getPlayer(Bukkit.getPlayer(uuid));
    }
    
    // Scoreboard instance
    public ScoreboardManager getBoardManager() {
	return this.boardman;
    }
    
    public Scoreboard getNewBoard() {
	return getBoardManager().getNewScoreboard();
    }
    
    // Manage Arenas and Lobbys
    public Arena addArena(Location min, Location max) {
	
	double tempX;
	double tempY;
	double tempZ;
	
	if (min.getX() > max.getX()) {
	    tempX = min.getX();
	    min.setX(max.getX());
	    max.setX(tempX);
	}
	
	if (min.getY() > max.getY()) {
	    tempY = min.getY();
	    min.setY(max.getY());
	    max.setY(tempY);
	}
	
	if (min.getZ() > max.getZ()) {
	    tempZ = min.getZ();
	    min.setZ(max.getZ());
	    max.setZ(tempZ);
	}
	
	List<Location> locList = new ArrayList<Location>();
	locList.add(min.clone());
	locList.add(max.clone());
	return addArena(locList);
    }
    
    public Arena addArena(List<Location> arenaCorners) {
	
	if (arenaCorners.size() > 1) {
	    World world = arenaCorners.get(0).getWorld();
	    
	    for (Location loc : arenaCorners) {
		if (!world.equals(loc.getWorld())) {
		    ZvP.getPluginLogger().log(this.getClass(), Level.SEVERE, "Could not add new arena: Locations are not in same world!", true, false);
		    return null;
		}
	    }
	    
	    int arenaID = getNewID(this.arenaPath);
	    
	    try {
		Arena arena = new Arena(arenaID, this.arenaPath, world, arenaCorners);
		this.arenas.add(arena);
		
		ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Arena " + arena.getID() + " in World " + arena.getWorld().getUID().toString() + " added! MaxPlayer=" + arena.getConfig().getMaxPlayers() + ", Size=" + arena.getArea().getDiagonal(), true);
		return arena;
	    } catch (Exception e) {
		ZvP.getPluginLogger().log(this.getClass(), Level.SEVERE, "Error while adding Arena " + arenaID + ": " + e.getMessage(), true, false, e);
	    }
	}
	return null;
    }
    
    public void addLobby(Location loc) {
	Lobby l = new Lobby(getNewID(this.lobbyPath), this.lobbyPath, loc.clone());
	this.lobbys.add(l);
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Lobby " + l.getID() + " in World " + l.getWorld().getUID().toString() + " added!", true);
    }
    
    public boolean removeArena(Arena arena) {
	if (arena != null && this.arenas.contains(arena)) {
	    boolean arrayRemove = this.arenas.remove(arena);
	    boolean fileDelete = arena.delete();
	    
	    for (ISign arenaSign : SignManager.getManager().getSigns(arena)) {
		SignManager.getManager().removeSign(arenaSign.getID());
	    }
	    
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Arena " + arena.getID() + " removed! Removed from list " + (arrayRemove ? "successfully" : "failed") + "; Deleted File " + (fileDelete ? "successfully" : "failed") + "!", true);
	    return (arrayRemove && fileDelete); // INFO: return class would make sense here
	} else {
	    return false;
	}
    }
    
    public boolean removeLobby(Lobby lobby) {
	if (lobby != null && this.lobbys.contains(lobby)) {
	    boolean arrayRemove = this.lobbys.remove(lobby);
	    boolean fileDelete = lobby.delete();
	    
	    for (ISign lobbySign : SignManager.getManager().getSigns(lobby)) {
		SignManager.getManager().removeSign(lobbySign.getID());
	    }
	    
	    ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Lobby " + lobby.getID() + " removed! Removed from list " + (arrayRemove ? "successfully" : "failed") + "; Deleted File " + (fileDelete ? "successfully" : "failed") + "!", true);
	    return (arrayRemove && fileDelete); // INFO: return class would make sense here
	} else {
	    return false;
	}
    }
    
    // Manage Players
    public boolean createPlayer(Player player, Arena arena, Lobby lobby) {
	
	if ((!arena.isFull() || arena.getArenaMode().allowFullArena()) && arena.isOnline()) {
	    if (ZvPConfig.getAllowDuringGameJoin() || !arena.isRunning()) {
		new ZvPPlayer(player, arena, lobby);
		SignManager.getManager().updateSigns(lobby);
		SignManager.getManager().updateSigns(arena);
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }
    
    public boolean removePlayer(ZvPPlayer player) {
	boolean success = player.getArena().removePlayer(player);
	player.reset();
	ZvP.getPluginLogger().log(this.getClass(), Level.INFO, "Player " + player.getName() + " removed from Game!", true);
	return success;
    }
    
    // Game control
    public void startGame(Arena a, Lobby l) {
	a.start();
	SignManager.getManager().updateSigns(l);
	SignManager.getManager().updateSigns(a);
    }
    
    public void stopGames() {
	for (Arena a : getArenas()) {
	    if (a.getStatus() != ArenaStatus.STANDBY || a.hasPreLobby()) {
		a.stop();
	    }
	}
	Bukkit.getScheduler().cancelTasks(ZvP.getInstance());;
    }
    
    public boolean isInGame(Player player) {
	for (Arena a : getArenas()) {
	    if (a.containsPlayer(player)) {
		return true;
	    }
	    if (a.hasPreLobby()) {
		if (a.getPreLobby().containsPlayer(player)) {
		    return true;
		}
	    }
	}
	return false;
    }
    
}
