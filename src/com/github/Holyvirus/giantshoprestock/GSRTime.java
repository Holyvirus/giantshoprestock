package com.github.Holyvirus.giantshoprestock;

import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GSRTime {
	
		GSRTime GSRT = this;
		GSRstocker GSRS = new GSRstocker();
		GiantShopRestock GSR = GiantShopRestock.getGR();
		long restockday = GSR.config.getLong("RestockDay") *  86400;
	
    public int getTime() {
    	int TIMEINCREASE = Integer.parseInt(GSR.config.getString("TimeZoneIncrease"));
        long Mtime = Calendar.getInstance(Calendar.getInstance().getTimeZone()).getTimeInMillis();
        String format = String.format("%%0%dd", 2);  
        long Ftime = Mtime / 1000;
        String seconds = String.format(format, Ftime % 60);
        String minutes = String.format(format, (Ftime % 3600) / 60);
        String hours = String.format(format, Ftime % 86400 / 3600 + TIMEINCREASE);
        int nowtime = Integer.parseInt(hours) * 3600 + Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds);
        if(nowtime > 86400){
        	nowtime = nowtime - 86400;
        }else if(nowtime < 0){
        	nowtime = nowtime + 86400;
        }
        String hseconds = String.format(format, nowtime % 60);
        String hminutes = String.format(format, (nowtime % 3600) / 60);
        String hhours = String.format(format, (nowtime % 86400) / 3600);
        this.humanNowTime =  hhours + " hours, " + hminutes + " minutes and " + hseconds + " seconds";
        return nowtime;
    }
	
    String humanNowTime;
    long delay;
    
    public long getDelay(){
    	try{
	    	String[] data = GSR.config.getString("RestockTime").split("\\:");
			int h = Integer.parseInt(data[0]);
			int m = Integer.parseInt(data[1]);
			this.runtime = h * 3600 + m * 60;
			this.delay = (86400 - getTime()) + (86400 - (86400 - runtime));
				if(this.delay >= 86400){
					 this.delay = this.delay - 86400;
				}
		}catch(Exception e){
			GSR.log.log(Level.SEVERE, "The restock time is not formatted correctly, it has to be: \"hours:minutes\"");
		}
    	return restockday;
    }

    long stopTime;
    
    int runtime;
	boolean initdelay;
	
    public void doTask(){
    	GSR.lastTime = getTime();
    	GSRT.initdelay = true;
    	doTaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GSR, new Runnable() {
			public void run() {
				GSRT.initdelay = false;
       			GSR.lastTime = getTime();
           			GSRS.Restock();
       		}
    	}, GSRT.delay, restockday);
    }

    int doTaskID;
    long whichdelay;
    
    public void restartTask(CommandSender sender){
    	if(!Bukkit.getServer().getScheduler().isQueued(doTaskID)){
	    	delay = getTime() - stopTime;
	    	doTask();
	    	if(sender != null){
				sender.sendMessage(ChatColor.RED + "The restock system has been restarted!");
	    	}
    	}else{
			if(sender != null){
				sender.sendMessage(ChatColor.RED + "The restock task is already running!");
			}else{
				GSR.log.log(Level.INFO, "The restock task is already running!");
			}
    	}
    }
    
    public void stopTask(CommandSender sender){
    	if(Bukkit.getServer().getScheduler().isQueued(doTaskID)){
			Bukkit.getServer().getScheduler().cancelTask(doTaskID);
		    	if(sender != null){
					sender.sendMessage(ChatColor.RED + "The restock system has been stopped! Delays were saved if you wish to restart!");
		    	}
		}else{
			if(sender != null){
				sender.sendMessage(ChatColor.RED + "The restock task is NOT running atm! Please type \"/rs start\" to start restocking!");
			}
		}
    	stopTime = getTime();
    }
    
    public String getTimeLeft(){
    	if(initdelay == true){
    		this.whichdelay = getDelay();
    	}else if(initdelay == false){
    		this.whichdelay = restockday;
    	}
    	int TIMEINCREASE = Integer.parseInt(GSR.config.getString("TimeZoneIncrease"));
    	this.getDelay();
    	String format = String.format("%%0%dd", 2);
    	long timeLeft = (GSR.lastTime + whichdelay) - getTime();
    	GSR.log.log(Level.SEVERE, "lasttime : " + GSR.lastTime + ", which delay: " + whichdelay + ", gettime(): " + getTime());
        String seconds = String.format(format, timeLeft % 60);
        String minutes = String.format(format, (timeLeft % 3600) / 60);
        String hours = String.format(format, (timeLeft % 86400) / 3600 + TIMEINCREASE);
        String days = String.format(format, (timeLeft % 31536000) / 86400);
        String time =  days + " days, " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds";
        return time;
    }
 }
