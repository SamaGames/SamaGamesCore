package net.samagames.core.commands;

import net.samagames.core.APIPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public abstract class AbstractCommand implements CommandExecutor {

	protected final APIPlugin plugin;

	public AbstractCommand(APIPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		return onCommand(commandSender, s, strings);
	}

	protected abstract boolean onCommand(CommandSender sender, String label, String[] arguments);

	protected boolean hasPermission(CommandSender sender, String permission) {
		if (sender instanceof ConsoleCommandSender || sender.isOp())
			return true;

		boolean result = false;
		if (sender instanceof Player)
			result = plugin.getAPI().getPermissionsManager().hasPermission(sender, permission);

		if (!result)
			sender.sendMessage(ChatColor.RED + "Vous n'avez pas le droit de faire ça.");

		return result;
	}
}
