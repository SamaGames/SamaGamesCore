package net.samagames.core.commands;

import net.samagames.core.APIPlugin;
import net.samagames.core.api.permissions.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
public class CommandBukkitrefresh extends AbstractCommand {

    private final PermissionManager api;

    public CommandBukkitrefresh(APIPlugin plugin) {
        super(plugin);

        api = plugin.getAPI().getPermissionsManager();
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, String label, String[] strings) {
        if (!hasPermission(commandSender, "api.permissions.refresh"))
            return true;

        Bukkit.getScheduler().runTaskAsynchronously(APIPlugin.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(api::refreshPlayer);
            commandSender.sendMessage(ChatColor.GREEN + "Les permissions locales ont été raffraichies !");
        });
        return true;
    }
}
