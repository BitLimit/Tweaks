package com.kolinkrewinkel.BitLimitTweaks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;
import java.util.*;

public class BitLimitTweaks extends JavaPlugin {
    private int weatherId;

    @Override
    public void onEnable() {
        new BitLimitTweaksListener(this);

        this.getCommand("tweaks").setExecutor(new TweaksCommandExecutor(this));
        this.saveConfig();
    }

    @Override
    public void onDisable() {        
        this.getServer().getScheduler().cancelTask(this.weatherId);
        this.saveConfig();
    }

    @Override
    public void saveConfig() {
        super.saveConfig();

        this.getConfig().options().copyDefaults(true);
        this.reloadConfig();
        setRepeatingTaskEnabled(this.getConfig().getConfigurationSection("preferences").getBoolean("weather"));
    }

    public void setRepeatingTaskEnabled(boolean enabled) {
        Server server = this.getServer();
        BukkitScheduler scheduler = server.getScheduler();
        if (enabled && this.weatherId == 0) {
            class BitLimitRecurringTask implements Runnable {
                Plugin plugin;

                BitLimitRecurringTask(Plugin p) {
                    plugin = p;
                }

                public void run() {
                    World world = this.plugin.getServer().getWorld(plugin.getConfig().getConfigurationSection("meta").getConfigurationSection("weather").getString("world"));

                    if (!world.hasStorm()) {
                        int weatherDuration = world.getWeatherDuration();
                        int ratio = (int)0.75; // 75% less often.
                        int calculatedAddback = 1200 * ratio;
                        world.setWeatherDuration(weatherDuration + calculatedAddback);
                    }
                }
            }

            this.weatherId = scheduler.scheduleSyncRepeatingTask(this, new BitLimitRecurringTask(this), 1200L, 1200L);
        } else if (this.weatherId != 0) {
            scheduler.cancelTask(this.weatherId);
            this.weatherId = 0;
        }
    }
}

