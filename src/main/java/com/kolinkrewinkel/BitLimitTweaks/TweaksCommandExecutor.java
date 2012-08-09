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
        /*  /tweaks tnt enable
            /tweaks tnt disable
            /tweaks weather enable
            /tweaks weather disable
            /tweaks slimes enable
            /tweaks slimes disable
        */

        if (sender.hasPermission("BitLimitTweaks")) {
            if (args.length > 1) {
                boolean validParameter = isValidBooleanInput(args[1]);
                if (!validParameter) {
                    sender.sendMessage(ChatColor.RED + "Invalid second parameter: expected *able and its past participle, or standard YES/NO (capitalization agnostic).")
                }
                if (args[0].toLowerCase().equals("tnt")) {

                } else if (args[0].toLowerCase().equals("weather")) {

                } else if (args[0].toLowerCase().equals("slimes")) {

                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid parameter. Expected TNT, weather, or slimes.");
                }
            } else {
                sender.sendMessage(ChatColor.GOLD + "Valid parameters: TNT, weather, or slimes, followed by \"enabled\" or \"disabled\"");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
        }
        return false;
    }

    private boolean isValidBooleanInput(String string) {
        return string.equals("enable") || string.equals("enabled") || string.equals("true") || string.equals("YES") || string.equals("yes") || string.equals("disable") || string.equals("disabled") || string.equals("false") || string.equals("NO") || string.equals("no");
    }
}
