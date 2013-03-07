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
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.SkullMeta;

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

    /******************************************
    Event Handler: Block Place(BlockPlaceEvent)
    ----------- Core Event Listener -----------
    ******************************************/
    
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        // Event reference
        // BlockPlaceEvent(Block placedBlock, BlockState replacedBlockState, Block placedAgainst, ItemStack itemInHand, Player thePlayer, boolean canBuild) 

        boolean confinementEnabled = this.plugin.getConfig().getBoolean("enabled-tnt");
        if (event.getItemInHand().getTypeId() != 46 || !confinementEnabled)
            return;

        WorldGuardPlugin worldGuard = getWorldGuard();
        Block block = event.getBlockPlaced();
        Vector pt = toVector(block.getLocation());
        LocalPlayer localPlayer = worldGuard.wrapPlayer(event.getPlayer());
         
        RegionManager regionManager = worldGuard.getRegionManager(event.getPlayer().getWorld());
        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
        
        if (set.size() == 0)
            event.setCancelled(true);
        else
            event.setCancelled(!set.isOwnerOfAll(localPlayer));
        
        if (event.isCancelled()) {
            displaySmokeInWorldAtLocation(block.getWorld(), block.getLocation());
            event.getPlayer().sendMessage(ChatColor.RED + "You are not authorized to place TNT in this location.");
        }
    }

    /******************************************
      Event Handler: Explosion Creation (TNT)
    ----------- Core Event Listener -----------
    ******************************************/
    
    @EventHandler
    public void onExplosionPrimeEvent(ExplosionPrimeEvent event) {
        // Event reference
        // ExplosionPrimeEvent (final Entity what, final float radius, final boolean fire)

        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed && this.plugin.getConfig().getBoolean("enabled-tnt")) {
            TNTPrimed tnt = (TNTPrimed)entity;
            List <Entity> nearbyEntities = tnt.getNearbyEntities(64, 128, 64); // check if player is horizontally within 4 chunks
            Iterator entityIterator = nearbyEntities.iterator();

            boolean playerNearby = false;
            while (entityIterator.hasNext()) {
                Entity nextEntity = (Entity)entityIterator.next();
                if (nextEntity instanceof Player) 
                    playerNearby = true;
            }

            // Required due to Bukkit's broken implementation of explosion prime - only checks if players are nearby so that *someone* is there to ensure it happened, though this includes the hypothetical griefer as well.
            // Btw, to the few at Bukkit who keep blocking this: you're really annoying. Don't play semantic bullshit games. Think you're Richard Stallman or something, idiot?

            if (!playerNearby) {
                event.setCancelled(true);
                displaySmokeInWorldAtLocation(tnt.getWorld(), tnt.getLocation());
                ItemStack tntItem = new ItemStack(Material.TNT, 1);
                tnt.getWorld().dropItemNaturally(tnt.getLocation(), tntItem);

                List <Entity> distantEntities = tnt.getNearbyEntities(256, 128, 256); // check if player is horizontally within far render-distance zone
                Iterator chunkEntities = distantEntities.iterator();
                while (chunkEntities.hasNext()) {
                    Entity chunkEntity = (Entity)chunkEntities.next();
                    if (chunkEntity instanceof Player) {
                        Player chunkPlayer = (Player)chunkEntity;
                        chunkPlayer.sendMessage(ChatColor.RED + "TNT explosion within area failed - original item dropped at location.");
                        displaySmokeInWorldAtLocation(chunkPlayer.getWorld(), chunkPlayer.getLocation());
                    }
                }
            }
        }
    }

    /******************************************
      Event Handler: Player Head Kill-Drops
    ----------- Core Event Listener -----------
    ******************************************/

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;

        if (event.getEntity().getKiller() instanceof Player) {
            ItemStack skullStack = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);

            SkullMeta meta = (SkullMeta) skullStack.getItemMeta();
            meta.setOwner(event.getEntity().getDisplayName());

            Calendar now = Calendar.getInstance();
            ArrayList lore = new ArrayList();
            lore.add(ChatColor.AQUA + "Slain by " + ChatColor.GOLD + event.getEntity().getKiller().getDisplayName() + ChatColor.AQUA + " on " + getFriendlyDate(now));
            meta.setLore(lore);
            skullStack.setItemMeta(meta);

            event.getDrops().add(skullStack);
        }
    }

    /******************************************
    External Getter: Returns World Guard Plugin
    ---------- Dependency Conveneince ---------
    ******************************************/

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldGuardPlugin) plugin;
    }

    /*********************************************
    ------------ Conveinience Methods -----------
    *********************************************/

    public boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    private void displaySmokeInWorldAtLocation(World world, Location location) {
          world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
    }

    // Overloaded function to cut down on calling arguments, no default parameters in Java =/
        private static String getFriendlyDate(Calendar theDate)
        {
            return getFriendlyDate(theDate, false);
        }
     
        // Function to get a human readable version of a Calendar object
        // If verbose is true we slightly expand the date wording
        private static String getFriendlyDate(Calendar theDate, boolean verbose)
        {
            int year       = theDate.get(Calendar.YEAR);
            int month      = theDate.get(Calendar.MONTH);
            int dayOfMonth = theDate.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek  = theDate.get(Calendar.DAY_OF_WEEK);
     
            // Get the day of the week as a String.
            // Note: The Calendar DAY_OF_WEEK property is NOT zero-based, and Sunday is the first day of week.
            String friendly = "";
            switch (dayOfWeek)
            {
                case 1:
                    friendly = "Sunday";
                    break;
                case 2:
                    friendly = "Monday";
                    break;
                case 3:
                    friendly = "Tuesday";
                    break;
                case 4:
                    friendly = "Wednesday";
                    break;
                case 5:
                    friendly = "Thursday";
                    break;
                case 6:
                    friendly = "Friday";
                    break;
                case 7:
                    friendly = "Saturday";
                    break;
                default:
                    friendly = "BadDayValue";
                    break;
            }
     
            // Add padding and the prefix to the day of month
            if (verbose == true)
            {
                friendly += " the " + dayOfMonth;
            }
            else
            {
                friendly += ", " + dayOfMonth;
            }
     
            String dayString = String.valueOf(dayOfMonth);   // Convert dayOfMonth to String using valueOf
     
            // Suffix is "th" for day of day of month values ending in 0, 4, 5, 6, 7, 8, and 9
            if (dayString.endsWith("0") || dayString.endsWith("4") || dayString.endsWith("5") || dayString.endsWith("6") ||
                        dayString.endsWith("7") || dayString.endsWith("8") || dayString.endsWith("9") || dayString.equals("13"))
            {
                friendly += "th ";
            } else if (dayString.endsWith("1"))
            {
                friendly += "st ";
            } else if (dayString.endsWith("2"))
            {
                friendly += "nd ";
            } else if (dayString.endsWith("3"))
            {
                friendly += "rd ";
            }
     
            // Add more padding if we've been asked to be verbose
            if (verbose == true)
            {
                friendly += "of ";
            }
     
     
            // Get a friendly version of the month.
            // Note: The Calendar MONTH property is zero-based to increase the chance of developers making mistakes.
            switch (month)
            {
                case 0:
                    friendly += "January";
                    break;
                case 1:
                    friendly += "February";
                    break;
                case 2:
                    friendly += "March";
                    break;
                case 3:
                    friendly += "April";
                    break;
                case 4:
                    friendly += "May";
                    break;
                case 5:
                    friendly += "June";
                    break;
                case 6:
                    friendly += "July";
                    break;
                case 7:
                    friendly += "August";
                    break;
                case 8:
                    friendly += "September";
                    break;
                case 9:
                    friendly += "October";
                    break;
                case 10:
                    friendly += "November";
                    break;
                case 11:
                    friendly += "December";
                    break;
                default:
                    friendly += "BadMonthValue";
                    break;
            }
     
            // Tack on the year and we're done. Phew!
            friendly += " " + year;     
     
            return friendly;
     
            } // End of getFriendlyDate function
}

