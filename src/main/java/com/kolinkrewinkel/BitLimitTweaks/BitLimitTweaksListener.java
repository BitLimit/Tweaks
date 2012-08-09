package com.kolinkrewinkel.BitLimitTweaks;

import org.bukkit.plugin.Plugin;
import java.util.*;

import org.bukkit.event.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.*;
import org.bukkit.event.entity.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.metadata.*;
import org.bukkit.block.Block;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockPlaceEvent;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import static com.sk89q.worldguard.bukkit.BukkitUtil.*;

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

