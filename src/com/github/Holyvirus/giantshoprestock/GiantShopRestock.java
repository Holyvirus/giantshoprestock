package com.github.Holyvirus.giantshoprestock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	GSRstocker GSRS = new GSRstocker();
	GSRTime GSRT = new GSRTime();
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
		log.log(Level.INFO, "[GiantShopRestock] Was successfully enabled!");
	}

	@Override
	public void onDisable() {
		BS.cancelTasks(GR);
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
    	    		if(args[0] == "start"){
    	    			BS.cancelTasks(GR);
    	    			GSRT.doTask();
    	    			sender.sendMessage(ChatColor.GOLD + "The restock system has been started! Your next restock is in: " + "¤9" + GSRT.getTimeLeft());
    	    		}else if(args[0] == "stop"){
    	    			BS.cancelTasks(GR);
    	    			sender.sendMessage(ChatColor.RED + "The restock system has been stopped! All delays were reset!");
    	    		}else if(args[0].matches("^[a-zA-Z_]+$") ) {
    	    			String FItem = args[0];
						int FAmt = Integer.parseInt(args[1]);
						config.getIntegerList("Restock." + FItem).add(FAmt);
	    	    		sender.sendMessage("The item: " + FItem + " will restock by " + FAmt + " each time!");
    	    		}else if(args[0].matches("[0-9]+") && args[1].matches("[0-9]+")) {
			    		int item = Integer.parseInt(args[0]);
						String itemName = GSItems.getItemNameByID(item);
						String FItem = itemName;
						int FAmt = Integer.parseInt(args[1]);
						config.getIntegerList("Restock." + FItem).add(FAmt);
	    	    		sender.sendMessage("The item: " + FItem + " will restock by " + FAmt + " each time!");
    	    		}else if(args[0].matches("[0-9]+:[0-9]+")){
		    			try {
							String[] data = args[0].split(":");
							int itemID = Integer.parseInt(data[0]);
							int itemType = Integer.parseInt(data[1]);
							String itemName = GSItems.getItemNameByID(itemID, itemType);
			    			String FItem = itemName;
							int FAmt = Integer.parseInt(args[1]);
							config.getIntegerList("Restock." + FItem).add(FAmt);
		    	    		sender.sendMessage("The item: " + FItem + " will restock by " + FAmt + " each time!");
						}catch(NumberFormatException e) {
							e.printStackTrace();
						}
    	    		}
    		}
    		}else if(cmd.getName().equalsIgnoreCase("restocktime") || cmd.getName().equalsIgnoreCase("rst")){
    			if(args.length < 1){
    				sender.sendMessage(ChatColor.GOLD + "The current time is: " + GSRT.getTime() + "the shop will next restock in: " + "¤9" + GSRT.getTimeLeft());
    			}else if(args.length == 1){
    			if (sender.hasPermission("giantshop.restock")){
    			config.set("RestockTime", args[0]);
    			sender.sendMessage("The shops will restock every: " + args[0] + "days!");
    			this.saveYamls();
    			}
    			}
    		}
    		return true;
	    }
}
