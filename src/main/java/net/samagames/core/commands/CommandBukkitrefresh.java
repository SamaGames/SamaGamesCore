package net.samagames.core.commands;

import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import net.samagames.permissionsapi.PermissionsAPI;
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
public class CommandBukkitrefresh extends AbstractCommand {

    protected PermissionsAPI api;

    public CommandBukkitrefresh(APIPlugin plugin) {
        super(plugin);
        
        this.api = SamaGamesAPI.get().getPermissionsManager().getApi();
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, String label, String[] strings) {
        Bukkit.getScheduler().runTaskAsynchronously(APIPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                api.getManager().refresh();
                commandSender.sendMessage(ChatColor.GREEN + "Les permissions locales ont été raffraichies !");
            }
        });
        return true;
    }
}
