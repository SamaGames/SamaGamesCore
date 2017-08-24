package net.samagames.core.commands;

import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
public class CommandInv extends AbstractCommand
{
    public CommandInv(APIPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] arguments)
    {
        if (!hasPermission(sender, "api.inventory.show"))
            return true;

        if(sender instanceof Player)
        {
            if(arguments.length != 0)
            {
                String playerName = arguments[0];
                Player player = Bukkit.getPlayer(playerName);

                if(player != null)
                {
                    Inventory inventory = player.getInventory();
                    ((Player) sender).closeInventory();
                    ((Player) sender).openInventory(inventory);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Le joueur spécifié n'est pas en ligne !");
                }
            }
            else
            {
                return false;
            }
        }

        return true;
    }
}
