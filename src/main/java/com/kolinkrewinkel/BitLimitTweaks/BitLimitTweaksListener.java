package com.kolinkrewinkel.BitLimitTweaks;

import org.bukkit.plugin.Plugin;
import java.util.*;

import org.bukkit.event.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.*;
import org.bukkit.event.entity.*;

public class BitLimitTweaksListener implements Listener {
    private final BitLimitTweaks plugin; // Reference main plugin
    private final Random random;

    /******************************************
    Initialization: BitLimitTweaksListener(plugin)
    --------- Designated Initializer ----------
    ******************************************/

    public BitLimitTweaksListener(BitLimitTweaks plugin) {
        // Notify plugin manager that this plugin handles implemented events (block place, etc.)
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        // CreatureSpawnEvent (Entity spawnee, CreatureType type, Location loc, SpawnReason reason

        EntityType entityType = event.getEntityType();
        SpawnReason reason = event.getSpawnReason();
        if (entityType == EntityType.SLIME && (reason == SpawnReason.NATURAL || reason == SpawnReason.SLIME_SPLIT))  {
            boolean shouldCancel = getRandomBoolean();
            event.setCancelled(shouldCancel);
            if (event.isCancelled()) {
                this.plugin.getServer().broadcastMessage(ChatColor.GREEN + "Cancelled slime spawning.");
            } else {
                this.plugin.getServer().broadcastMessage(ChatColor.RED + "Slime spawned.");
            }
        }
    }

    public boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }
}

