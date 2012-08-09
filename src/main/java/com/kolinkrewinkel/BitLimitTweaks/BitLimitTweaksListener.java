package com.kolinkrewinkel.BitLimitTweaks;

import org.bukkit.plugin.Plugin;
import java.util.*;

import org.bukkit.event.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.*;
import org.bukkit.event.entity.*;
import org.bukkit.configuration.file.FileConfiguration;

public class BitLimitTweaksListener implements Listener {
    private final BitLimitTweaks plugin; // Reference main plugin

    /*********************************************
    Initialization: BitLimitTweaksListener(plugin)
    ----------- Designated Initializer ----------
    *********************************************/

    public BitLimitTweaksListener(BitLimitTweaks plugin) {
        // Notify plugin manager that this plugin handles implemented events (block place, etc.)
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    /*********************************************
      Event Handler: onCreatureSpawnEvent(Event)
    --------------- Event Handler --------------
    *********************************************/

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        // CreatureSpawnEvent (Entity spawnee, CreatureType type, Location loc, SpawnReason reason

        FileConfiguration config = this.plugin.getConfig();
        if (!config.getBoolean("enabled-slimes"))
            return;

        // Gather information to determine if these are the slimes we are looking for.
        EntityType entityType = event.getEntityType();
        SpawnReason reason = event.getSpawnReason();
        if (entityType == EntityType.SLIME && (reason == SpawnReason.NATURAL || reason == SpawnReason.SLIME_SPLIT))  {
            // Pseudo-randomly cancel slime spawns to reduce their numbers.
            boolean shouldCancel = getRandomBoolean();
            event.setCancelled(shouldCancel);
        }
    }

    /*********************************************
    ------------ Conveinience Methods -----------
    *********************************************/

    public boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }
}

