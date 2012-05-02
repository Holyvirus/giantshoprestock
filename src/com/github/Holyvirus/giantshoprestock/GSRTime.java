package com.github.Holyvirus.giantshoprestock;

import java.util.Calendar;

import org.bukkit.Bukkit;

public class GSRTime {
	
	GSRTime GSRT = this;
	GSRstocker GSRS = new GSRstocker();
	GiantShopRestock GSR = GiantShopRestock.getGR();
	long restockday = GSR.config.getLong("RestockDay") *  86400;
	
    public int getTime() {
        long Mtime = Calendar.getInstance(Calendar.getInstance().getTimeZone()).getTimeInMillis();
        String format = String.format("%%0%dd", 2);  
        long Ftime = Mtime / 1000;
        String seconds = String.format(format, Ftime % 60);
        String minutes = String.format(format, (Ftime % 3600) / 60);
        String hours = String.format(format, Ftime % 86400 / 3600 + GSR.config.getInt("TimeZoneIncrease"));
        int nowtime = Integer.parseInt(hours) * 3600 + Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds);
        return nowtime;
    }
	
    public long getDelay(){
    	String[] data = GSR.config.getString("RestockTime").split(":");
		int h = Integer.parseInt(data[0]);
		int m = Integer.parseInt(data[1]);
		this.runtime = h * 3600 + m * 60;
		long delay = (86400 - getTime()) + (86400 - (86400 - runtime));
		if( delay >= 86400){
			 delay = delay - 86400;
		}
		return delay;
    }
    
    int runtime;
	
    public void doTask(){
    	Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GSR, new Runnable() {
       		public void run() {
           			GSRS.Restock();
       		}
    	}, getDelay(), restockday);
    }
    
    public String getTimeLeft(){
    	this.getDelay();
        String format = String.format("%%0%dd", 2);  
    	long NOWTIME = ((86400 - getTime()) + (86400 - (86400 - runtime)));
        String seconds = String.format(format, NOWTIME % 60);
        String minutes = String.format(format, (NOWTIME % 3600) / 60);
        String hours = String.format(format, (NOWTIME % 86400) / 3600 + GSR.config.getInt("TimeZoneIncrease"));
        String days = String.format(format, (NOWTIME % 31536000) / 86400);
        String time =  days + " days, " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds";
        return time;
    }
 }
