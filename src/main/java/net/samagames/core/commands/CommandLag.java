package net.samagames.core.commands;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.samagames.core.APIPlugin;
import net.samagames.tools.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class CommandLag extends AbstractCommand
{
    private final SimpleDateFormat dateFormat;

	public CommandLag(APIPlugin plugin)
    {
		super(plugin);

        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss", Locale.FRANCE);
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] arguments)
    {
		if (!(sender instanceof Player))
			return true;

		Player player = (Player) sender;
		int latency = ((CraftPlayer) player).getHandle().ping;

		StringBuilder tps = new StringBuilder();
		double[] tab;
		int length = (tab = MinecraftServer.getServer().recentTps).length;

		for(int var7 = 0; var7 < length; ++var7)
        {
			tps.append(this.format(tab[var7]));

			if (var7 + 1 < length)
				tps.append(", ");
		}

		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
		player.sendMessage(ChatUtils.getCenteredText(ChatColor.WHITE + "•" + ChatColor.BOLD + " Informations de lag " + ChatColor.RESET + ChatColor.WHITE + "•"));
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Date : " + ChatColor.GRAY + this.dateFormat.format(new Date()));
        player.sendMessage(ChatColor.YELLOW + "Serveur : " + ChatColor.GRAY + APIPlugin.getInstance().getServerName().replace("_", ""));
		player.sendMessage("");
		player.sendMessage(ChatColor.YELLOW + "Latence : " + this.formatLag(latency) + ChatColor.GRAY + "ms");
		player.sendMessage(ChatColor.YELLOW + "Charge serveur : " + ChatColor.GRAY + tps.toString());
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

		return true;
	}


	private String format(double tps)
    {
		return (tps > 18.0D? org.bukkit.ChatColor.GREEN:(tps > 16.0D? org.bukkit.ChatColor.YELLOW: org.bukkit.ChatColor.RED)).toString() + (tps > 20.0D?"*":"") + Math.min((double)Math.round(tps * 100.0D) / 100.0D, 20.0D);
	}

	private String formatLag(double lag)
    {
		return "" + (lag > 200.0 ? org.bukkit.ChatColor.RED : (lag > 120D? org.bukkit.ChatColor.GOLD: (lag > 70D? org.bukkit.ChatColor.YELLOW: org.bukkit.ChatColor.GREEN))).toString() + (double)Math.round(lag * 100.0D) / 100.0D;
	}
}
