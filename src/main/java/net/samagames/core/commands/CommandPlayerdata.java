package net.samagames.core.commands;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.player.AbstractPlayerData;
import net.samagames.core.APIPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class CommandPlayerdata extends AbstractCommand {

	public CommandPlayerdata(APIPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] arguments) {
		if (!hasPermission(sender, "api.playerdata.show"))
			return true;

		if (arguments.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage : /playerdata <pseudo>");
			return true;
		}

		if (arguments.length >= 3 && arguments[0].equalsIgnoreCase("set")) {
			if (!hasPermission(sender, "api.playerdata.set"))
				return true;

			final String playerName = arguments[1];
			final String key = arguments[2];
			final String value = Strings.join(Arrays.copyOfRange(arguments, 3, arguments.length), " ");

			new Thread(() -> {
				UUID playerId = SamaGamesAPI.get().getUUIDTranslator().getUUID(playerName, true);
				AbstractPlayerData data = SamaGamesAPI.get().getPlayerManager().getPlayerData(playerId);

				if (data == null)
				{
					sender.sendMessage(ChatColor.RED + "Une erreur inconnue s'est produite.");
					return;
				}

				data.set(key, value);
				sender.sendMessage(ChatColor.YELLOW + "Données modifiées.");
			}, "CommandPlayerDataSet").start();
			return true;
		}

		if (arguments.length >= 3 && arguments[0].equalsIgnoreCase("del")) {
			if (!hasPermission(sender, "api.playerdata.del"))
				return true;

			final String playerName = arguments[1];
			final String key = arguments[2];

			new Thread(() -> {
				UUID playerId = SamaGamesAPI.get().getUUIDTranslator().getUUID(playerName, true);
                AbstractPlayerData data = SamaGamesAPI.get().getPlayerManager().getPlayerData(playerId);

				if (data == null)
				{
					sender.sendMessage(ChatColor.RED + "Une erreur inconnue s'est produite.");
					return;
				}

				data.remove(key);
				sender.sendMessage(ChatColor.YELLOW + "Données supprimées.");
			}, "CommandPlayerDataSet").start();
			return true;
		}

		final String playerName = arguments[0];
			new Thread(() -> {
				UUID playerId = SamaGamesAPI.get().getUUIDTranslator().getUUID(playerName, true);
                AbstractPlayerData data = SamaGamesAPI.get().getPlayerManager().getPlayerData(playerId);

				if (data == null)
				{
					sender.sendMessage(ChatColor.RED + "Une erreur inconnue s'est produite.");
					return;
				}

				sender.sendMessage(ChatColor.YELLOW + "Data pour " + ChatColor.GREEN + playerName + ChatColor.YELLOW + " / " + ChatColor.AQUA + playerId);
				for (Map.Entry<String, String> entry : data.getValues().entrySet()) {
					sender.sendMessage(ChatColor.YELLOW + " - " + entry.getKey() + " : " + entry.getValue());
				}
			}, "CommandPlayerData").start();

		return true;
	}
}
