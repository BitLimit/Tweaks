package com.kolinkrewinkel.BitLimitTweaks;

import java.util.*;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.block.*;

public class TweaksCommandExecutor implements CommandExecutor {
    private final BitLimitTweaks plugin;
    
    public TweaksCommandExecutor(BitLimitTweaks plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("BitLimitTweaks")) {
            if (args.length > 1) {
                if (args.equals("weatherDuration")) {
                    if (sender instanceof Player) {
                        Player player = (Player)sender;
                        player.getWorld().setWeatherDuration(Integer.parseInt(args[1]));
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
        }
        return false;
    }
}
