package me.Aubli.ZvP.Listeners;

import java.util.logging.Level;

import me.Aubli.ZvP.ZvP;
import me.Aubli.ZvP.Game.Arena;
import me.Aubli.ZvP.Game.GameManager;
import me.Aubli.ZvP.Game.Lobby;
import me.Aubli.ZvP.Game.ZvPPlayer;
import me.Aubli.ZvP.Kits.KitManager;
import me.Aubli.ZvP.Shop.ShopItem;
import me.Aubli.ZvP.Shop.ShopManager;
import me.Aubli.ZvP.Shop.ShopManager.ItemCategory;
import me.Aubli.ZvP.Sign.ShopSign;
import me.Aubli.ZvP.Sign.SignManager;
import me.Aubli.ZvP.Sign.SignManager.SignType;
import me.Aubli.ZvP.Translation.MessageManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener{
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		Player eventPlayer = (Player)event.getWhoClicked();
		
	//	Super awesome Clickevent future
	//	if(event.getRawSlot()==-999) {
	//		eventPlayer.closeInventory();
	//	}
	//	Cool but very dangerous for importent user input	
	
	//	System.out.println(event.getRawSlot() + " " + event.getSlot() + "; " + event.getInventory().getSize());
		if(eventPlayer.hasPermission("zvp.play")) {
			if(event.getCurrentItem()!=null && event.getCurrentItem().getType()!=Material.AIR) {
				
				boolean onlyTopinventory = false;
				
				if(event.getInventory().getTitle().equalsIgnoreCase(MessageManager.getMessage("inventory:kit_select")) ||
				   event.getInventory().getTitle().contains(MessageManager.getMessage("inventory:select_category")) ||
				   event.getInventory().getTitle().contains("Items: "))	{
					onlyTopinventory = true;
				}				
				
				if(((event.getRawSlot() != event.getSlot()) || event.getSlot()>=event.getInventory().getSize()) && onlyTopinventory) {
					eventPlayer.sendMessage(MessageManager.getMessage("game:wrong_inventory"));
					event.setCancelled(true);
					return;
				}				
				
	//			System.out.println(event.getRawSlot() + " " + event.getSlot() + ": " + event.getCurrentItem().getItemMeta().getDisplayName());
				
				if(event.getInventory().getTitle().equalsIgnoreCase(MessageManager.getMessage("inventory:kit_select"))) {				
					event.setCancelled(true);
					eventPlayer.closeInventory();
					
					String kitName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());				
					ZvPPlayer player = GameManager.getManager().getPlayer(eventPlayer);
					
					if(KitManager.getManager().getKit(kitName)!=null && player!=null) {
						player.setKit(KitManager.getManager().getKit(kitName));
						ZvP.getPluginLogger().log(Level.INFO, "[ZvP] " + player.getName() + " took the " + player.getKit().getName() + " Kit", true);
						return;
					}
				}
				if(event.getInventory().getTitle().contains(MessageManager.getMessage("inventory:select_category"))) {					
					event.setCancelled(true);
					eventPlayer.closeInventory();
					
					int signID = Integer.parseInt(event.getInventory().getTitle().split("Category ")[1].replace("(", "").replace(")", ""));
					ItemCategory cat = ItemCategory.getEnum(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
					
					if(cat!=null && SignManager.getManager().getSign(signID)!=null) {
						ShopSign sign = (ShopSign)SignManager.getManager().getSign(signID);
						
						Arena a = sign.getArena();
						Lobby l = sign.getLobby();
						Location lo = sign.getLocation();
						
						SignManager.getManager().removeSign(signID);						
						SignManager.getManager().createSign(SignType.SHOP_SIGN, lo, a, l, cat);
					}
				}
				if(event.getInventory().getTitle().contains("Items: ")) {
					event.setCancelled(true);
					ZvP.getPluginLogger().log(Level.FINEST, "ShopClick: Slot: " + event.getSlot() + " RawSlot: " + event.getRawSlot() + " Result: " + event.getResult().toString(), true);
					
					if(event.getRawSlot()>event.getInventory().getSize()) {	
						ZvP.getPluginLogger().log(Level.WARNING, "Player " + eventPlayer.getName() + " tryed to acces slot " + event.getRawSlot() + " (index:" + event.getInventory().getSize() + ")", true);
						return; 
					}
					
					ItemCategory cat = ItemCategory.getEnum(event.getInventory().getTitle().split("s: ")[1]);						
					ShopItem item = ShopManager.getManager().getItem(cat, event.getCurrentItem());
					ZvPPlayer player = GameManager.getManager().getPlayer(eventPlayer);
						
					if(item !=null && player!=null && GameManager.getManager().isInGame(player.getPlayer())){
					
						switch (event.getClick()) {
						
							case LEFT: //Buy
								if(player.getArena().getBalance()>=item.getPrice()) {
																			
									ItemStack boughtItem = new ItemStack(item.getItem().getType(), item.getItem().getAmount());
									boughtItem.addUnsafeEnchantments(item.getItem().getEnchantments());
									boughtItem.setDurability(item.getItem().getDurability());
											
									player.getArena().subtractBalance(item.getPrice());
									player.getPlayer().getInventory().addItem(boughtItem);
									player.getArena().sendMessage(String.format(MessageManager.getMessage("game:player_bought"), player.getName(), item.getType().toString().toLowerCase().replace("_", " "), item.getPrice()));
								}else {
									player.sendMessage(MessageManager.getMessage("game:no_money"));
								}	
								break;
									
							case SHIFT_LEFT: //Buy all
								if(player.getArena().getBalance()>=item.getPrice()) {
									
									int amount = (int) (player.getArena().getBalance()/item.getPrice())<64 ? (int) (player.getArena().getBalance()/item.getPrice()):64;
									
									ItemStack boughtItem = new ItemStack(item.getItem().getType(), amount);
									boughtItem.addUnsafeEnchantments(item.getItem().getEnchantments());
									boughtItem.setDurability(item.getItem().getDurability());
									
									player.getArena().subtractBalance(item.getPrice()*amount);
									player.getPlayer().getInventory().addItem(boughtItem);
									player.getArena().sendMessage(String.format(MessageManager.getMessage("game:player_bought_more"), player.getName(), amount, item.getType().toString().toLowerCase().replace("_", " "), Math.round(item.getPrice()*amount)));
								}else {
									player.sendMessage(MessageManager.getMessage("game:no_money"));
								}	
								break;
								
							case RIGHT: //Sell
								
								ItemStack stack = new ItemStack(item.getItem().getType());
								stack.setDurability(item.getItem().getDurability());
								stack.addUnsafeEnchantments(item.getItem().getEnchantments());
								
								if(player.getPlayer().getInventory().containsAtLeast(stack, 1)) {
									player.getPlayer().getInventory().removeItem(stack);
									player.getArena().addBalance(item.getPrice());
									player.getArena().sendMessage(String.format(MessageManager.getMessage("game:player_sold"), player.getName(), item.getType().toString().toLowerCase().replace("_", " "), item.getPrice()));
								}else {
									player.sendMessage(MessageManager.getMessage("game:no_item_to_sell"));
								}
								
								break;
								
							case SHIFT_RIGHT: //sell all
								
								ItemStack stack1 = new ItemStack(item.getItem().getType());
								stack1.setDurability(item.getItem().getDurability());
								stack1.addUnsafeEnchantments(item.getItem().getEnchantments());
								
								int amount = 0;
								
								if(player.getPlayer().getInventory().containsAtLeast(stack1, 1)) {
									for(int i=0;i<player.getPlayer().getInventory().getSize();i++) {
										ItemStack invItem = player.getPlayer().getInventory().getItem(i);
										
										if(invItem!=null && invItem.getType()!=Material.AIR) {
											if(invItem.getType() == stack1.getType() && invItem.getDurability() == stack1.getDurability() && invItem.getEnchantments().equals(stack1.getEnchantments())) {
												amount += invItem.getAmount();
												player.getPlayer().getInventory().clear(i);
											}
										}
									}
									
									player.getArena().addBalance(item.getPrice() * amount);
									player.getArena().sendMessage(String.format(MessageManager.getMessage("game:player_sold_more"), player.getName(), amount, item.getType().toString().toLowerCase().replace("_", " "), Math.round(item.getPrice()*amount)));
								}else {
									player.sendMessage(MessageManager.getMessage("game:no_item_to_sell"));
								}
								
								break;
							default:
								break;
						}
					}
					event.getView().close();
					return;
				}
			}
		}
	}
	
	
	private ItemStack[] content = null;
	private String name;
	
	@EventHandler
	public void onClose(InventoryCloseEvent event){
		final Player eventPlayer = (Player)event.getPlayer();
		
		if(ChatColor.stripColor(event.getInventory().getTitle()).startsWith("ZvP-Kit: ") && event.getInventory().getSize()==9){	
			
			Inventory eventInv = event.getInventory();
			name = ChatColor.stripColor(eventInv.getTitle().split("ZvP-Kit: ")[1]);
			content = eventInv.getContents();
			
			for(ItemStack item : content) {
				if(item!=null && item.getType()!=Material.AIR) {
					Bukkit.getScheduler().runTaskLater(ZvP.getInstance(), new Runnable() {
						@Override
						public void run() {
							KitManager.getManager().openAddKitIconGUI(eventPlayer);			
						}
					}, 1*10L);
					break;
				}
			}			
		}
		
		if(event.getInventory().getTitle().equalsIgnoreCase(MessageManager.getMessage("inventory:kit_select"))) {
			ZvPPlayer player = GameManager.getManager().getPlayer(eventPlayer);
			
			if(player!=null) {
				if(!player.hasKit()) {
					player.setCanceled(true);
					GameManager.getManager().removePlayer(player);
					//player.setKit(KitManager.getManager().getKit("No Kit"));
					return;
				}
			}
		}
		
		if(event.getInventory().getTitle().equals(MessageManager.getMessage("inventory:place_icon"))){
			
			for(ItemStack item : event.getInventory().getContents()) {
				if(item!=null && item.getType()!=Material.AIR) {
					KitManager.getManager().addKit(name, item, content);
					eventPlayer.sendMessage(String.format(MessageManager.getMessage("manage:kit_saved"), name));
					break;
				}
			}
			this.content = null;
			this.name = null;
			return;
		}else {
			return;
		}
	}
}