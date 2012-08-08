package com.kolinkrewinkel.BitLimitTweaks;

import org.bukkit.plugin.Plugin;
import java.util.*;

public class BitLimitTweaksListener implements Listener {
    private final BitLimitTweaks plugin; // Reference main plugin
    
    /******************************************
    Initialization: BitLimitTweaksListener(plugin)
    --------- Designated Initializer ----------
    ******************************************/

    public BitLimitTweaksListener(BitLimitTweaks plugin) {
        // Notify plugin manager that this plugin handles implemented events (block place, etc.)
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
}

