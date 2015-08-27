package net.samagames.core.commands;

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

    private final PermissionsAPI api;

    public CommandBukkitrefresh(APIPlugin plugin) {
        super(plugin);

        api = plugin.getAPI().getPermissionsManager().getApi();
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, String label, String[] strings) {
        if (!hasPermission(commandSender, "api.permissions.refresh"))
            return true;

        Bukkit.getScheduler().runTaskAsynchronously(APIPlugin.getInstance(), () -> {
            api.getManager().refresh();
            commandSender.sendMessage(ChatColor.GREEN + "Les permissions locales ont été raffraichies !");
        });
        return true;
    }
}
