package com.github.Holyvirus.giantshoprestock;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.giantit.minecraft.GiantShop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.GiantShop.API.GiantShopAPI;
import nl.giantit.minecraft.GiantShop.API.stock.core.itemStock;

public class GSRstocker {

	public static final Logger log = Logger.getLogger("Minecraft");
	itemStock ItemStock;
	itemStock IStock;
	org.bukkit.configuration.ConfigurationSection CS;
	GiantShopRestock GSR = GiantShopRestock.getGR();
	
	public void Restock(){
		Set<String> keys = GSR.config.getConfigurationSection("Restock").getKeys(false);
		if(keys != null) {
			   for(String key : keys) {
					try {
						int VID = GSR.GSItems.getItemIDByName(key).getId();
							if(GSR.GSItems.isValidItem(VID)){
							this.IStock = GiantShopAPI.Obtain().getStockAPI().getItemStock(key);
								if(!GSR.config.getBoolean("AllowOverStock") && (IStock.getStock() + GSR.config.getInt("Restock." + key)) > IStock.getMaxStock()){
									log.log(Level.INFO, "The restock of: " + key + " cancelled because you would overstock!");
								}else{
									IStock.setStock(IStock.getStock() + GSR.config.getInt("Restock." + key));
							}
						}else{
							log.log(Level.WARNING, "The config node:" + key + " is not valid!1");
						}
					} catch (ItemNotFoundException e) {
						e.printStackTrace();
						log.log(Level.WARNING, "The item:" + key + " has not been set in the shop! Type \"/shop add [item] [amount] [buyfor]\"");
					}
			   } 
		}else{
			log.log(Level.WARNING, "You have no items that need to be restocked, please define what you want to restock using\"/restock [item] [amount]\"");
		}
	}
}