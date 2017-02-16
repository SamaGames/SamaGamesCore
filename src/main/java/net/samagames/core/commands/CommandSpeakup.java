package net.samagames.core.commands;

import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        ChatColor color = ChatColor.BLUE;
        String tag = "Modération";

        if (SamaGamesAPI.get().getPermissionsManager().getPlayer(((Player) sender).getUniqueId()).getGroupId() == 8)
        {
            color = ChatColor.DARK_PURPLE;
            tag = "Animation";
        }

        Bukkit.broadcastMessage(color + "" + ChatColor.BOLD + "[" + tag + "] " + sender.getName() + ": " + StringUtils.join(arguments, " "));

        return true;
    }
}
