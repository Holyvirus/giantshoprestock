package com.github.Holyvirus.giantshoprestock;

import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.Bukkit;

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
    
    public long getDelay(){
    	String[] data = GSR.config.getString("RestockTime").split("\\:");
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
    	GSR.lastTime = getTime();
    	GSR.log.log(Level.SEVERE, "Im at doTask!");
    	doTaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GSR, new Runnable() {
			public void run() {
		    	GSR.log.log(Level.SEVERE, "Im at run()!");
       			GSR.lastTime = getTime();
           			GSRS.Restock();
       		}
    	}, 20, restockday);
    }

    int doTaskID;
    
    public String getTimeLeft(){
    	int TIMEINCREASE = Integer.parseInt(GSR.config.getString("TimeZoneIncrease"));
    	this.getDelay();
    	String format = String.format("%%0%dd", 2);
    	long timeLeft = (GSR.lastTime + restockday) - getTime();
        String seconds = String.format(format, timeLeft % 60);
        String minutes = String.format(format, (timeLeft % 3600) / 60);
        String hours = String.format(format, (timeLeft % 86400) / 3600 + TIMEINCREASE);
        String days = String.format(format, (timeLeft % 31536000) / 86400);
        String time =  days + " days, " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds";
        return time;
    }
 }
