package net.samagames.core.commands;

import net.samagames.core.APIPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/*
 * This file is part of SamaGamesCore.
 *
 * SamaGamesCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesCore.  If not, see <http://www.gnu.org/licenses/>.
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
