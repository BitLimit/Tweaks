package com.kolinkrewinkel.BitLimitTweaks;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.Server;
import java.util.*;

public class BitLimitTweaks extends JavaPlugin {

    @Override
    public void onEnable() {
        new BitLimitTweaksListener(this);

        this.getCommand("tweaks").setExecutor(new TweaksCommandExecutor(this));

        BitLimitTweaksRepeatingTask task = new BitLimitTweaksRepeatingTask();
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, task, 0L, 1200L);
    }

    @Override
    public void onDisable() {        
        // save the configuration file, if there are no values, write the defaults.
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }
}

