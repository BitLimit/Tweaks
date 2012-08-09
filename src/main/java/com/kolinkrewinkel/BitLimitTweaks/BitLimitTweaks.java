package com.kolinkrewinkel.BitLimitTweaks;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.Server;
import java.util.*;

public class BitLimitTweaks extends JavaPlugin {
    private int weatherId;

    @Override
    public void onEnable() {
        new BitLimitTweaksListener(this);

        this.getCommand("tweaks").setExecutor(new TweaksCommandExecutor(this));
    }

    @Override
    public void onDisable() {        
        // save the configuration file, if there are no values, write the defaults.
        this.getServer().getScheduler().cancelTask(this.weatherId);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    public void setRepeatingTaskEnabled(boolean enabled) {
        Server server = this.getServer();
        BukkitScheduler scheduler = server.getScheduler();
        if (enabled) {
            class BitLimitRecurringTask implements Runnable {
                Plugin plugin;

                BitLimitRecurringTask(Plugin p) {
                    plugin = p;
                }

                public void run() {
                    World world = server.getWorld(plugin.getConfig().getString("world"));
                    if (!world.hasStorm()) {
                        int weatherDuration = world.getWeatherDuration();
                        world.setWeatherDuration(weatherDuration + 600);
                    }
                }
            }
            this.weatherId = scheduler.scheduleSyncRepeatingTask(this, new BitLimitRecurringTask(this), 1200L, 1200L);
        } else {
            scheduler.cancelTask(this.weatherId);
            this.weatherId = null;
            server.broadcastMessage(ChatColor.CYAN + "Weather recurring task stopped.", "tweaks");
        }
    }
}

