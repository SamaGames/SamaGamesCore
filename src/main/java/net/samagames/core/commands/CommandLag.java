package net.samagames.core.commands;

import net.samagames.core.APIPlugin;
import net.samagames.tools.Reflection;
import net.samagames.tools.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	private static Class<?> craftPlayerClass;
	private static Method getServerMethod;
	private static Field recentTpsField;
	private static Field pingField;

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

		try
		{
			int latency = (int) pingField.get(craftPlayerClass.cast((Player) sender));

			StringBuilder tps = new StringBuilder();
			double[] recentTps = (double[]) recentTpsField.get(getServerMethod.invoke(null));
			int length = recentTps.length;

			for(int var7 = 0; var7 < length; ++var7)
			{
				tps.append(this.format(recentTps[var7]));

				if (var7 + 1 < length)
					tps.append(", ");
			}

			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			player.sendMessage(ChatUtils.getCenteredText(ChatColor.WHITE + "•" + ChatColor.BOLD + " Informations de lag " + ChatColor.RESET + ChatColor.WHITE + "•"));
			player.sendMessage("");
			player.sendMessage(ChatColor.YELLOW + "Date : " + ChatColor.GRAY + this.dateFormat.format(new Date()));
			player.sendMessage(ChatColor.YELLOW + "Serveur : " + ChatColor.GRAY + APIPlugin.getInstance().getServerName().replace("_", " "));
			player.sendMessage("");
			player.sendMessage(ChatColor.YELLOW + "Latence : " + (latency < 0 ? ChatColor.RED + "Erreur" : this.formatLag(latency) + ChatColor.GRAY + "ms"));
			player.sendMessage(ChatColor.YELLOW + "Charge serveur : " + ChatColor.GRAY + tps.toString());
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}

		return true;
	}


	private String format(double tps)
    {
		return (tps > 18.0D? org.bukkit.ChatColor.GREEN:(tps > 16.0D? org.bukkit.ChatColor.YELLOW: org.bukkit.ChatColor.RED)).toString() + (tps > 20.0D?"*":"") + Math.min((double)Math.round(tps * 100.0D) / 100.0D, 20.0D);
	}

	private String formatLag(double lag)
    {
		return "" + (lag > 200.0 ? org.bukkit.ChatColor.RED : (lag > 120D? org.bukkit.ChatColor.GOLD: (lag > 70D? org.bukkit.ChatColor.YELLOW: org.bukkit.ChatColor.GREEN))).toString() + (int) Math.round(lag * 100.0D) / 100.0D;
	}

	static
	{
		try
		{
			craftPlayerClass = Reflection.getOBCClass("entity.CraftPlayer");
            Class<?> minecraftServerClass = Reflection.getNMSClass("MinecraftServer");

            getServerMethod = minecraftServerClass.getMethod("getServer");
            recentTpsField = minecraftServerClass.getField("recentTps");
			pingField = craftPlayerClass.getField("ping");
		}
		catch (NoSuchFieldException | NoSuchMethodException e)
		{
			e.printStackTrace();
		}
    }
}
