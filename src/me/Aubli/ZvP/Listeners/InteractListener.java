package me.Aubli.ZvP.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.ZvPCommands;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.InteractSign;
import me.Aubli.ZvP.Sign.ShopSign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Sign.SignManager.SignType;
import me.Aubli.ZvP.Translation.MessageKeys.arena;
import me.Aubli.ZvP.Translation.MessageKeys.error;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageKeys.manage;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class InteractListener implements Listener {
    
    private HashMap<Action, Location> clickLoc = new HashMap<Action, Location>();
    private static List<Location> locationList = new ArrayList<Location>();
    
    private SignManager sm = SignManager.getManager();
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
	
	Player eventPlayer = event.getPlayer();
	
	if (event.getItem() != null) {
	    
	    //@formatter:off
	    ItemStack s1 = event.getItem();
	    ItemStack s2 = ZvP.getTool(ZvP.ADDARENA_SINGLE);
	    ZvP.getPluginLogger().log(getClass(), Level.ALL, "\nType:" + s1.getType()+ ";" + s2.getType()+ "\n"
		    					    +"Dura:" + s1.getDurability() + ";" + s2.getDurability()+"\n"
		    					    +"hasMeta:" + s1.hasItemMeta() + ";" + s2.hasItemMeta() + "\n"
		    					    +"isSimilar:" + s1.isSimilar(s2), true, true);
	    
	    if(s1.hasItemMeta()) {
		ItemMeta m1 = s1.getItemMeta();
		ItemMeta m2 = s2.getItemMeta();
		ZvP.getPluginLogger().log(getClass(), Level.ALL, "\nClass:" + m1.getClass().getName() + ";" +m2.getClass().getName()+";" + m1.getClass().equals(m2.getClass())+"\n"
								+"dpn:" +m1.getDisplayName() + ";" + m2.getDisplayName() +";" + m1.getDisplayName().equals(m2.getDisplayName())+"\n"
								+"lore:" +m1.getLore() +";"+m2.getLore()+";"+m1.getLore().equals(m2.getLore())+"\n"
								+"flags:" + m1.getItemFlags() + ";" + m2.getItemFlags()+";"+m1.getItemFlags().equals(m2.getItemFlags())+"\n"
								+"ench:" + m1.getEnchants() + ";" +m2.getEnchants()+";" + m1.getEnchants().equals(m2.getEnchants())+"\n"
								+"equals:" + ZvP.getInstance().getServer().getItemFactory().equals(m1, m2), true, true);
		
	    }
	    //@formatter:on
	    if (ZvP.equalsTool(event.getItem())) {
		if (eventPlayer.hasPermission("zvp.manage.arena")) {
		    
		    String toolString = ChatColor.stripColor(event.getItem().getItemMeta().getLore().get(0));
		    event.setCancelled(true);
		    
		    if (toolString.equals(ZvP.ADDARENA_SINGLE)) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			    this.clickLoc.put(event.getAction(), event.getClickedBlock().getLocation().clone());
			    eventPlayer.sendMessage(MessageManager.getMessage(manage.right_saved));
			}
			
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			    this.clickLoc.put(event.getAction(), event.getClickedBlock().getLocation().clone());
			    eventPlayer.sendMessage(MessageManager.getMessage(manage.left_saved));
			}
			
			if (this.clickLoc.containsKey(Action.RIGHT_CLICK_BLOCK) && this.clickLoc.containsKey(Action.LEFT_CLICK_BLOCK)) {
			    Arena arena = GameManager.getManager().addArena(this.clickLoc.get(Action.LEFT_CLICK_BLOCK), this.clickLoc.get(Action.RIGHT_CLICK_BLOCK));
			    if (arena != null) {
				eventPlayer.sendMessage(MessageManager.getFormatedMessage(manage.arena_saved, arena.getID()));
			    } else {
				eventPlayer.sendMessage(MessageManager.getMessage(error.arena_place));
			    }
			    
			    this.clickLoc.clear();
			}
			
		    } else if (toolString.equals(ZvP.ADDARENA_POLYGON)) {
			locationList.add(event.getClickedBlock().getLocation().clone());
			eventPlayer.sendMessage(MessageManager.getFormatedMessage(manage.position_saved_poly, locationList.size()));
			
		    } else if (toolString.equals(ZvP.ADDPOSITION)) {
			for (Arena arena : GameManager.getManager().getArenas()) {
			    if (arena.containsLocation(eventPlayer.getLocation())) {
				boolean success = arena.getArea().addSpawnPosition(event.getClickedBlock().getLocation().clone().add(0, 1, 0));
				if (success) {
				    eventPlayer.sendMessage(MessageManager.getFormatedMessage(manage.position_saved, "Position in arena " + arena.getID()));
				} else {
				    eventPlayer.sendMessage(MessageManager.getMessage(manage.position_not_saved));
				}
				return;
			    }
			}
			eventPlayer.sendMessage(MessageManager.getMessage(manage.position_not_in_arena));
		    }
		    
		} else {
		    ZvP.removeTool(eventPlayer);
		    ZvPCommands.commandDenied(eventPlayer);
		}
		return;
	    }
	}
	
	if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    if (this.sm.isZVPSign(event.getClickedBlock().getLocation())) {
		if (this.sm.getType(event.getClickedBlock().getLocation()) == SignType.INTERACT_SIGN) { // Player join per Sign
		    if (!GameManager.getManager().isInGame(eventPlayer)) {
			if (eventPlayer.hasPermission("zvp.play")) {
			    InteractSign sign = (InteractSign) this.sm.getSign(event.getClickedBlock().getLocation());
			    if (sign.getArena() != null) {
				if (sign.getArena().isOnline()) {
				    boolean success = GameManager.getManager().createPlayer(eventPlayer, sign.getArena(), sign.getLobby());
				    
				    if (!success) {
					event.setCancelled(true);
					eventPlayer.sendMessage(MessageManager.getMessage(arena.not_ready));
					return;
				    }
				} else {
				    event.setCancelled(true);
				    eventPlayer.sendMessage(MessageManager.getMessage(arena.offline));
				    return;
				}
			    } else {
				event.setCancelled(true);
				eventPlayer.sendMessage(MessageManager.getMessage(error.arena_not_available));
				return;
			    }
			} else {
			    event.setCancelled(true);
			    ZvPCommands.commandDenied(eventPlayer);
			    return;
			}
		    } else {
			event.setCancelled(true);
			eventPlayer.sendMessage(MessageManager.getMessage(game.already_in_game));
			return;
		    }
		} else if (this.sm.getType(event.getClickedBlock().getLocation()) == SignType.SHOP_SIGN) { // player clicked shop sign
		    if (GameManager.getManager().isInGame(eventPlayer)) {
			if (eventPlayer.hasPermission("zvp.play")) {
			    event.setCancelled(true);
			    
			    ShopSign shopSign = (ShopSign) this.sm.getSign(event.getClickedBlock().getLocation());
			    ItemCategory cat = shopSign.getCategory();
			    ZvPPlayer player = GameManager.getManager().getPlayer(eventPlayer);
			    
			    Inventory shopInv = Bukkit.createInventory(eventPlayer, ((int) Math.ceil(ShopManager.getManager().getItems(cat).size() / 9.0)) * 9, "Items: " + cat.toString());
			    
			    for (ShopItem shopItem : ShopManager.getManager().getItems(cat)) {
				
				ItemStack item = shopItem.getItem();
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList<String>();
				
				lore.add("Category: " + shopItem.getCategory().toString());
				lore.add(player.getArena().getScore().getScore(player) >= shopItem.getPrice() ? (ChatColor.GREEN + "Price: " + shopItem.getPrice()) : (ChatColor.RED + "Price: " + shopItem.getPrice()));
				
				meta.setLore(lore);
				item.setItemMeta(meta);
				shopInv.addItem(item);
			    }
			    eventPlayer.closeInventory();
			    eventPlayer.openInventory(shopInv);
			    return;
			} else {
			    event.setCancelled(true);
			    ZvPCommands.commandDenied(eventPlayer);
			    return;
			}
		    } else {
			event.setCancelled(true);
			eventPlayer.sendMessage(MessageManager.getMessage(game.not_in_game));
			return;
		    }
		}
	    }
	} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
	    if (GameManager.getManager().isInGame(eventPlayer)) {
		event.setCancelled(true);
		return;
	    }
	}
    }
    
    public static void clearPositionList() {
	locationList.clear();
    }
    
    public static Arena createArenaFromList() {
	return GameManager.getManager().addArena(locationList);
    }
}
