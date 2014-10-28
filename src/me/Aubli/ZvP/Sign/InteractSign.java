package me.Aubli.ZvP.Sign;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Game.GameManager.ArenaStatus;
import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Sign.SignManager.SignType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class InteractSign implements ISign, Comparable<ISign> {	
	
	private int ID;
	
	private File signFile;
	private FileConfiguration signConfig;
	
	private Location signLoc;	
	private Sign sign;
	
	private SignType type;
	
	private Lobby lobby;
	private Arena arena;
	
	public InteractSign(Location signLoc, int ID, String path, Arena arena, Lobby lobby) throws Exception{
		this.ID = ID;
		this.signLoc = signLoc.clone();
		
		this.signFile = new File(path + "/" + ID + ".yml");
		this.signConfig = YamlConfiguration.loadConfiguration(signFile);
		
		this.type = SignType.INTERACT_SIGN;
		
		this.lobby = lobby;
		this.arena = arena;
		
		try{
			signFile.createNewFile();
			signConfig.set("sign.ID", ID);
			signConfig.set("sign.Type", getType().toString());
			signConfig.set("sign.Arena", arena.getID());
			signConfig.set("sign.Lobby", lobby.getID());
			
			signConfig.set("sign.Location.world", signLoc.getWorld().getUID().toString());
			signConfig.set("sign.Location.X", signLoc.getBlockX());
			signConfig.set("sign.Location.Y", signLoc.getBlockY());
			signConfig.set("sign.Location.Z", signLoc.getBlockZ());
			
			signConfig.save(signFile);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		if(signLoc.getBlock().getState() instanceof Sign){
			sign = (Sign)signLoc.getBlock().getState();
		}else{
			delete();
			throw new Exception("Location is not a Sign!");
		}
		update();
	}
	
	public InteractSign(File signFile){
		this.signFile = signFile;
		this.signConfig = YamlConfiguration.loadConfiguration(signFile);
		
		this.type = SignType.INTERACT_SIGN;
		
		this.ID = signConfig.getInt("sign.ID");
		this.signLoc = new Location(
				Bukkit.getWorld(UUID.fromString(signConfig.getString("sign.Location.world"))),
				signConfig.getDouble("sign.Location.X"),
				signConfig.getDouble("sign.Location.Y"), 
				signConfig.getDouble("sign.Location.Z"));
		
		if(signLoc.getWorld()!=null){
		
			this.sign = (Sign) signLoc.getBlock().getState();
			
			this.arena = GameManager.getManager().getArena(signConfig.getInt("sign.Arena"));
			this.lobby = GameManager.getManager().getLobby(signConfig.getInt("sign.Lobby"));
			update();
		}
	}
	
	public void delete(){
		this.signFile.delete();
	}
	
	
	public int getID(){
		return ID;
	}
	
	public World getWorld(){
		return signLoc.getWorld();
	}
	
	public Location getLocation(){
		return signLoc.clone();
	}
	
	public Sign getSign(){
		return sign;
	}
	
	public SignType getType(){
		return type;
	}
	
	public Arena getArena(){
		return arena;
	}
	
	public Lobby getLobby(){
		return lobby;
	}
	
	
	public void update(){
		if(arena!=null){
			sign.setLine(0, ZvP.getPrefix());
			sign.setLine(1, "Arena: " + arena.getID());
			
			if(getArena().getStatus()==ArenaStatus.RUNNING || getArena().getStatus()==ArenaStatus.VOTING){
				sign.setLine(2, ChatColor.DARK_GREEN + getArena().getStatus().getName());
				sign.setLine(3, ChatColor.DARK_RED + "Can't Join!");				
			}
			if(getArena().getStatus()==ArenaStatus.WAITING || getArena().getStatus()==ArenaStatus.STANDBY){
				sign.setLine(2, ChatColor.YELLOW + getArena().getStatus().getName());
				sign.setLine(3, ChatColor.GREEN + "[JOIN]");	
			}
			if(getArena().getStatus()==ArenaStatus.STOPED || getArena().getStatus()==ArenaStatus.SUSPEND){
				sign.setLine(2, ChatColor.DARK_RED  + getArena().getStatus().getName());
				sign.setLine(3, ChatColor.DARK_RED + "Can't Join!");	
			}
			
			sign.update(true);
		}else{
			sign.setLine(0, ZvP.getPrefix());
			sign.setLine(1, "");
			sign.setLine(2, ChatColor.DARK_RED + "Arena is not");
			sign.setLine(3, ChatColor.DARK_RED + "available!");
			sign.update(true);
		}
	}
	
	@Override
	public int compareTo(ISign o) {
		return getArena().compareTo(o.getArena());
	}
	
}