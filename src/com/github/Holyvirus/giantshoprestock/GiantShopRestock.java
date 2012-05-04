package com.github.Holyvirus.giantshoprestock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.API.stock.core.itemStock;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class GiantShopRestock extends JavaPlugin{
	

	public static final Logger log = Logger.getLogger("Minecraft");
	File configFile;
	FileConfiguration config;
	private BukkitScheduler BS;
	private itemStock itemstock;
	private GiantShopAPI API;
	public Items GSItems;
	public ItemID enteredItem;
	public static GiantShopRestock GR;
	
	public GiantShopRestock() {
		setGR();
	}
	public void setGR() {
		GR = this;
	}
	public static GiantShopRestock getGR() {
		return GR;
	}
	@Override
	public void onEnable() {
		configFile = new File(getDataFolder(), "config.yml");
	    try {
	        firstRun();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    config = new YamlConfiguration();
	    loadYamls();
		GSItems = GiantShopAPI.Obtain().getItemHandlerAPI();
		this.GSRS = new GSRstocker();
		this.GSRT = new GSRTime();
		log.log(Level.INFO, "[GiantShopRestock] Was successfully enabled!");
	}
	
	public GSRstocker GSRS;
	public GSRTime GSRT;
	public long lastTime = 0;
	
	@Override
	public void onDisable() {
		log.log(Level.INFO, "[GiantShopRestock] Was successfully disabled!");
	}

	private void firstRun() throws Exception {
	    if(!configFile.exists()){
	        configFile.getParentFile().mkdirs();
	        copy(getResource("config.yml"), configFile);
	    }
	}
	    
	    private void copy(InputStream in, File file) {
	        try {
	            OutputStream out = new FileOutputStream(file);
	            byte[] buf = new byte[1024];
	            int len;
	            while((len=in.read(buf))>0){
	                out.write(buf,0,len);
	            }
	            out.close();
	            in.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public void loadYamls() {
	        try {
	            config.load(configFile);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public void saveYamls() {
	        try {
	            config.save(configFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
	    	if(cmd.getName().equalsIgnoreCase("restock") || cmd.getName().equalsIgnoreCase("rs")){
	    		if (sender.hasPermission("giantshop.restock")){
	    			if(args.length == 1){
	    				if(args[0].equalsIgnoreCase("start")){
	    	    			GSRT.doTask();
	    	    			sender.sendMessage(ChatColor.GOLD + "The restock system has been started! Your next restock is in: " + "¤9" + GSRT.getTimeLeft());
	    				}else if(args[0].equalsIgnoreCase("stop")){
	    					if(BS.isCurrentlyRunning(GSRT.doTaskID)){
	    						BS.cancelTask(GSRT.doTaskID);
	    						sender.sendMessage(ChatColor.RED + "The restock system has been stopped! All delays were reset!");
	    					}else{
	    		    			sender.sendMessage(ChatColor.RED + "The restock task is NOT running atm! Please type \"/rs start\" to start restocking!");
	    					}
	    				}
	    			}else if(args.length == 2){
	    				if(args[0].matches("[0-9]+") && args[1].matches("[0-9]+")) {
	    					String FItem = args[0];
	    					int FAmt = Integer.parseInt(args[1]);
	    					write(sender, FItem, FAmt);
	    			}else if(args[0].matches("[0-9]+:[0-9]+") && args[1].matches("[0-9]+")){
		    			try {
							String[] data = args[0].split(":");
							int FItem = Integer.parseInt(data[0]);
							int FType = Integer.parseInt(data[1]);
							int FAmt = Integer.parseInt(args[1]);
							write(sender, FItem, FType, FAmt);
		    			}catch(NumberFormatException e) {
							e.printStackTrace();	
		    			}
	    			}else if(args[0].matches("^[a-zA-Z_]+$") ) {
    	    			String FItem = args[0];
    	    			int FAmt = Integer.parseInt(args[1]);
						write(sender, FItem, FAmt);
	    			}
	    			}else{
	    				sender.sendMessage(ChatColor.RED + "Please enter more arguements!");
	    			}
	    		}else{
	    			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
	    		}
	    	}else if(cmd.getName().equalsIgnoreCase("restocktime") || cmd.getName().equalsIgnoreCase("rst")){
	    		if(BS.isCurrentlyRunning(GSRT.doTaskID)){
	    			if(args.length < 1){
	    				sender.sendMessage(ChatColor.GOLD + "The current time is: " + GSRT.humanNowTime + "the shop will next restock in: " + "¤9" + GSRT.getTimeLeft());
	    			}else if(args.length == 1){
	    					if (sender.hasPermission("giantshop.restock")){
	    							config.set("RestockTime", args[0]);
	    							sender.sendMessage("The shops will restock every: " + args[0] + "days!");
	    							this.saveYamls();
	    					}else{
	    						sender.sendMessage(ChatColor.RED + "You have entered too many arguments!");
	    					}
	    			}
	    		}else{
	    			sender.sendMessage(ChatColor.GOLD + "The restock task is NOT running atm! Please type \"/rs start\" to start restocking!");
	    		}
	    	}
	    	return true;
	    }

	    public void write(CommandSender sender, String Item, int FAmt){
	    	ItemID FItem = GSItems.getItemIDByName(Item);
	    	if(GSItems.isValidItem(FItem.getId())){
	    		getConfig().set("Restock." + Item, FAmt);
	    		try {
					getConfig().save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}else{
	    		sender.sendMessage("You have not specified a valid item!");
	    	}
	    }
	    
	    public void write(CommandSender sender, int FItem, int FType, int FAmt){
	    	if(GSItems.isValidItem(FItem, FType)){
	    		getConfig().set("Restock." + FItem, FAmt);
	    		try {
					getConfig().save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}else{
	    		sender.sendMessage("You have not specified a valid item!");
	    	}
	    }
}
