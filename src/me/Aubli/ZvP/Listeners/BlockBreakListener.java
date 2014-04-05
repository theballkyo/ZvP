package me.Aubli.ZvP.Listeners;

import me.Aubli.ZvP.ZvP;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener{

	public BlockBreakListener(ZvP plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		
		Player eventPlayer = event.getPlayer();
		if(plugin.start==true){
			if(plugin.playerVote.contains(eventPlayer)){
				event.setCancelled(true);	
			}			
		}	
	}
	
	private ZvP plugin;
}
