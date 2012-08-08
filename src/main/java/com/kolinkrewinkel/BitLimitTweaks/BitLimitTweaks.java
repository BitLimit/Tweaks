package com.kolinkrewinkel.BitLimitTweaks;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import java.util.*;

public class BitLimitTweaks extends JavaPlugin {

    @Override
    public void onEnable() {
        new BitLimitTweaksListener(this);

        this.getCommand("tweaks").setExecutor(new TweaksCommandExecutor(this));

        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            public void run() {
                World world = this.getServer().getWorld(this.getConfig().getString("world"));
                if (!world.hasStorm()) {
                    this.getServer().broadcastMessage(ChatColor.GREEN + "Rain decremented!");
                    int weatherDuration = world.getWeatherDuration();
                    world.setWeatherDuration(weatherDuration + 6000);
                } else {
                    this.getServer().broadcastMessage(ChatColor.RED + "Timer untouched, it's raining!");
                }
            }
        }, 0L, 1200L);
    }

    @Override
    public void onDisable() {        
        // save the configuration file, if there are no values, write the defaults.
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }
}

