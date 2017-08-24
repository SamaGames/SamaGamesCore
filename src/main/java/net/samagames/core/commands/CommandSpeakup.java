package net.samagames.core.commands;

import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
public class CommandSpeakup extends AbstractCommand
{
    public CommandSpeakup(APIPlugin plugin)
    {
        super(plugin);
    }

    @Override
    protected boolean onCommand(CommandSender sender, String label, String[] arguments)
    {
        if (!hasPermission(sender, "api.modo.speakup"))
            return true;

        if(arguments.length == 0)
            return true;

        ChatColor color = ChatColor.BLUE;
        String tag = "Modération";

        if (SamaGamesAPI.get().getPermissionsManager().getPlayer(((Player) sender).getUniqueId()).getGroupId() == 8)
        {
            color = ChatColor.DARK_PURPLE;
            tag = "Animation";
        }

        Bukkit.broadcastMessage(color + "" + ChatColor.BOLD + "[" + tag + "] " + sender.getName() + ": " + StringUtils.join(arguments, " "));

        return true;
    }
}
