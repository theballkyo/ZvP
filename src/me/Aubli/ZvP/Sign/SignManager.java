package me.Aubli.ZvP.Sign;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import me.Aubli.ZvP.Arena;
import me.Aubli.ZvP.GameManager;
import me.Aubli.ZvP.Lobby;
import me.Aubli.ZvP.ZvP;

public class SignManager {
	
	public enum SignType{
		INFO_SIGN,
		INTERACT_SIGN,
		;
	}	
	
	private static SignManager instance;
	
	private File interact;
	private File info;
	
	private ArrayList<InteractSign> interactSigns;
	private ArrayList<InfoSign> infoSigns;
	
	
	public SignManager(){
		instance = this;
		
		interactSigns = new ArrayList<InteractSign>();
		infoSigns = new ArrayList<InfoSign>();
		
		interact = new File(ZvP.getInstance().getDataFolder().getPath() + "/Signs/Interact");
		info = new File(ZvP.getInstance().getDataFolder().getPath() + "/Signs/Info");		
		reloadConfig();
	}		
	
	private void loadSigns(){		
		
		interactSigns = new ArrayList<InteractSign>();
		infoSigns = new ArrayList<InfoSign>();
		
		for(File f : info.listFiles()){
			InfoSign sign = new InfoSign(f);
			if(sign.getWorld()!=null){
				infoSigns.add(sign);
			}
		}		
		
		for(File f : interact.listFiles()){
			InteractSign sign = new InteractSign(f);
			if(sign.getWorld()!=null){
				interactSigns.add(sign);
			}
		}		
	}
	
	public void reloadConfig(){
		if(!interact.exists() || !info.exists()){
			info.mkdirs();
			interact.mkdirs();
		}
		loadSigns();
	}
	
	
	public static SignManager getManager(){
		return instance;
	}
	
	public InfoSign[] getInfoSigns(){
		InfoSign[] infSigns = new InfoSign[infoSigns.size()];
		
		for(int i=0;i<infoSigns.size();i++){
			infSigns[i] = infoSigns.get(i);
		}
		return infSigns;	
	}
	
	public InteractSign[] getInteractSigns(){
		InteractSign[] intSigns = new InteractSign[interactSigns.size()];
		
		for(int i=0;i<interactSigns.size();i++){
			intSigns[i] = interactSigns.get(i);
		}
		return intSigns;	
	}	
	
	public SignType getType(Location signLoc){
		for(InfoSign s : getInfoSigns()){
			if(s.getLocation().equals(signLoc)){
				return s.getType();
			}
		}
		
		for(InteractSign s : getInteractSigns()){
			if(s.getLocation().equals(signLoc)){
				return s.getType();
			}
		}
		return null;
	}
	
	public InfoSign getInfoSign(int ID){
		for(InfoSign infS : getInfoSigns()){
			if(infS.getID() == ID){
				return infS;
			}
		}
		return null;
	}
	
	public InfoSign getInfoSign(Location loc){
		for(InfoSign infS : getInfoSigns()){
			if(infS.getLocation().equals(loc)){
				return infS;
			}
		}
		return null;
	}
	
	public InteractSign getInteractSign(int ID){
		for(InteractSign s : getInteractSigns()){
			if(s.getID() == ID){
				return s;
			}
		}
		return null;
	}
	
	public InteractSign getInteractSign(Location loc){
		for(InteractSign s : getInteractSigns()){
			if(s.getLocation().equals(loc)){
				return s;
			}
		}
		return null;
	}
	
	
	public boolean isZVPSign(Location loc){
		if(getInteractSign(loc)!=null || getInfoSign(loc)!=null){
			return true;
		}
		return false;
	}
	
	public boolean createSign(SignType type, Location signLoc, Arena arena, Lobby lobby){		
		if(signLoc.getBlock().getState() instanceof Sign){
			if(type==SignType.INFO_SIGN){
				try{
					String path = info.getPath();
					InfoSign s = new InfoSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby);					
					infoSigns.add(s);	
					return true;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
			if(type==SignType.INTERACT_SIGN){
				try{
					String path = interact.getPath();
					InteractSign intSign = new InteractSign(signLoc.clone(), GameManager.getManager().getNewID(path), path, arena, lobby);
					interactSigns.add(intSign);
					return true;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}
	
	public boolean removeSign(SignType type, Location signLoc){
		if(type==SignType.INFO_SIGN){
			InfoSign s = getInfoSign(signLoc);
			infoSigns.remove(s);			
			s.delete();
			return true;
		}
		if(type==SignType.INTERACT_SIGN){
			InteractSign s = getInteractSign(signLoc);
			interactSigns.remove(s);
			s.delete();
			return true;
		}
		return false;
	}
	
	public boolean removeSign(SignType type, int signID){
		if(type==SignType.INFO_SIGN){
			InfoSign s = getInfoSign(signID);
			infoSigns.remove(s);
			s.delete();
			return true;
		}
		if(type==SignType.INTERACT_SIGN){
			InteractSign s = getInteractSign(signID);
			interactSigns.remove(s);
			s.delete();
			return true;
		}
		return false;
	}

	
	public void updateSigns(){
		for(InfoSign s : getInfoSigns()){
			s.update();
		}
		for(InteractSign s : getInteractSigns()){
			s.update();
		}
	}
	
	public void updateSigns(Lobby lobby){
		for(InfoSign s : getInfoSigns()){
			if(s.getLobby().equals(lobby)){
				s.update();
			}
		}
		for(InteractSign s : getInteractSigns()){
			if(s.getLobby().equals(lobby)){
				s.update();
			}
		}
	}
	
	public void updateSigns(Arena arena){
		for(InfoSign s : getInfoSigns()){
			if(s.getArena().equals(arena)){
				s.update();
			}
		}
		for(InteractSign s : getInteractSigns()){
			if(s.getArena().equals(arena)){
				s.update();
			}
		}
	}

}
