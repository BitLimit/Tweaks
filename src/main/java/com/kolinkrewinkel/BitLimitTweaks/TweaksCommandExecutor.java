package com.kolinkrewinkel.BitLimitTweaks;

import java.util.*;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;

public class TweaksCommandExecutor implements CommandExecutor {
    private final BitLimitTweaks plugin;
    
    public TweaksCommandExecutor(BitLimitTweaks plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*  /tweaks tnt enable
            /tweaks tnt disable
            /tweaks weather enable
            /tweaks weather disable
            /tweaks slimes enable
            /tweaks slimes disable
        */

        if (sender.hasPermission("tweaks.*")) {
            FileConfiguration config = this.plugin.getConfig();

            if (args.length > 1) {
                boolean validParameter = isValidBooleanInput(args[1]);
                if (!validParameter && !args[1].toLowerCase().equals("location")) {
                    sender.sendMessage(ChatColor.RED + "Invalid second parameter: expected *able and its past participle, or standard YES/NO (capitalization agnostic).");
                    return false;
                }
                boolean newValue = parsedBooleanInput(args[1]);
                ConfigurationSection configurationSection = config.getConfigurationSection("preferences");

                if (args[0].toLowerCase().equals("tnt")) {
                    configurationSection.set("tnt", newValue);
                } else if (args[0].toLowerCase().equals("weather")) {
                    configurationSection.set("weather", newValue);
                } else if (args[0].toLowerCase().equals("slimes")) {
                    configurationSection.set("slimes", newValue);
                } else if (args[0].toLowerCase().equals("spawnitems")) {

                    if (args[1].toLowerCase().equals("location")) {

                        try {
                            ConfigurationSection section = config.getConfigurationSection("meta").createSection("spawnItems").createSection("location");

                            if (sender instanceof Player) {
                                Player player = (Player)sender;
                                this.putLocationInSection(player.getLocation(), section);
                                sender.sendMessage(ChatColor.GREEN + "Location set successfully.");
                            } else {
                                sender.sendMessage(ChatColor.RED + "Sender must be player.");
                            }

                            return true;
                        } catch (Exception e) {

                        }

                    } else {
                        configurationSection.set("spawnItems", newValue);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid parameter. Expected TNT, weather, or slimes.");
                    return false;
                }

                String argument = args[0];
                if (argument.equals("tnt")) {
                    argument = "TNT";
                } else {
                    argument = capitalizedString(argument);
                }
                String newValueString = newValue ? "enabled" : "disabled";
                sender.sendMessage(ChatColor.AQUA + argument + " tweaks are now " + ChatColor.GOLD + newValueString +ChatColor.AQUA + ".");
            } else if (args.length == 1) {
                String argument = args[0].toLowerCase();
                if (argument.equals("tnt") || argument.equals("weather") || argument.equals("slimes") || argument.equals("spawnItems")) {
                    boolean enabled = config.getBoolean("enabled-" + argument);
                    if (argument.equals("tnt")) {
                        argument = "TNT";
                    } else {
                        argument = capitalizedString(argument);
                    }
                    if (enabled) {
                        sender.sendMessage(ChatColor.AQUA + argument + ChatColor.GREEN + " tweaks are currently enabled.");
                    } else {
                        sender.sendMessage(ChatColor.AQUA + argument + ChatColor.RED + " tweaks are currently disabled.");
                    }
                } else {
                    sender.sendMessage(ChatColor.AQUA + "Valid parameters: TNT, weather, slimes, or spawnItems to query state, optionally, followed by \"enabled\" or \"disabled\" to set.");
                }
            } else {
                sender.sendMessage(ChatColor.AQUA + "Valid parameters: TNT, weather, slimes, or spawnItems to query state, optionally, followed by \"enabled\" or \"disabled\" to set.");
            }

            // Save
            this.plugin.saveConfig();
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
        }
        return false;
    }

    private boolean isValidBooleanInput(String string) {
        return string.equals("enable") || string.equals("enabled") || string.equals("true") || string.equals("YES") || string.equals("yes") || string.equals("disable") || string.equals("disabled") || string.equals("false") || string.equals("NO") || string.equals("no");
    }

    private boolean parsedBooleanInput(String string) {
        if (string.equals("enable") || string.equals("enabled") || string.equals("true") || string.equals("YES") || string.equals("yes")) {
            return true;
        } else if (string.equals("disable") || string.equals("disabled") || string.equals("false") || string.equals("NO") || string.equals("no")) {
            return false;
        }
        return false;
    }

    private String capitalizedString(String string)  
    {  
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);  
    }

    public void putLocationInSection(Location location, ConfigurationSection configurationSection) {
        configurationSection.set("world", location.getWorld().getName());
        configurationSection.set("x", location.getX());
        configurationSection.set("y", location.getY());
        configurationSection.set("z", location.getZ());
        configurationSection.set("yaw", location.getYaw());
        configurationSection.set("pitch", location.getPitch());
    }
}
