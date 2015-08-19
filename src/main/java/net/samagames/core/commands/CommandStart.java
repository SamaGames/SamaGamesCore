package net.samagames.core.commands;

import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class CommandStart extends AbstractCommand
{
    public CommandStart(APIPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] arguments)
    {
        if (!hasPermission(sender, "staff.startgame"))
            return true;

        if(SamaGamesAPI.get().getGameManager().getGame() == null)
        {
            sender.sendMessage(ChatColor.RED + "Ceci n'est pas un serveur de jeu.");
            return true;
        }

        SamaGamesAPI.get().getGameManager().getGame().startGame();
        Bukkit.broadcastMessage(ChatColor.GREEN + "Le jeu a été démarré par " + sender.getName());

        return true;
    }
}
