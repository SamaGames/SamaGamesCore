package net.samagames.core.commands;

import net.samagames.core.APIPlugin;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by IamBlueSlime
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class CommandSpeakup extends AbstractCommand
{
    public CommandSpeakup(APIPlugin plugin)
    {
        super(plugin);
    }

    @Override
    protected boolean onCommand(CommandSender sender, String label, String[] arguments)
    {
        if (!hasPermission(sender, "api.modo.speakup"))
            return true;

        if(arguments.length == 0)
            return true;

        Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "[Modération] " + sender.getName() + ChatColor.RESET + ChatColor.BLUE + ": " + StringUtils.join(arguments, " "));

        return true;
    }
}
