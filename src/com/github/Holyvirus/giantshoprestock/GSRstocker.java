package com.github.Holyvirus.giantshoprestock;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.giantit.minecraft.GiantShop.API.stock.ItemNotFoundException;
import nl.giantit.minecraft.GiantShop.API.stock.stockAPI;
import nl.giantit.minecraft.GiantShop.API.stock.core.itemStock;
import nl.giantit.minecraft.GiantShop.core.Items.ItemID;
import nl.giantit.minecraft.GiantShop.core.Items.Items;

public class GSRstocker {

	public static final Logger log = Logger.getLogger("Minecraft");
	stockAPI stock;
	itemStock ItemStock;
	itemStock IStock;
	Items items;
	org.bukkit.configuration.ConfigurationSection CS;
	GiantShopRestock GSR = GiantShopRestock.getGR();
	
	public void Restock(){
		Set<String> keys = GSR.config.getConfigurationSection("Restock").getKeys(false);
		if(keys == null) {
		     log.log(Level.WARNING, "You have no items that need to be restocked, please define what you want to restock using\"/restock [item] [amount]\"");
		}else{
			   for(String key : keys) {
					try {
						this.IStock = stock.getItemStock(key);
						if(!GSR.config.getBoolean("AllowOverStock") && (IStock.getStock() + GSR.config.getInt("Restock." + key)) > IStock.getMaxStock()){
							log.log(Level.WARNING, "The restock of: " + key + " cancelled because you would overstock!");
						}else{
							IStock.setStock(IStock.getStock() + GSR.config.getInt("Restock." + key));
						}
					} catch (ItemNotFoundException e) {
						e.printStackTrace();
						log.log(Level.WARNING, "the config node:" + key + "is not valid!");
					}
			   }
		}
	}
}
