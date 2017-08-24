package net.samagames.core.commands;

import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
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
public class CommandStart extends AbstractCommand
{
    public CommandStart(APIPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] arguments)
    {
        if (!hasPermission(sender, "api.game.start"))
            return true;

        if(SamaGamesAPI.get().getGameManager().getGame() == null)
        {
            sender.sendMessage(ChatColor.RED + "Ceci n'est pas un serveur de jeu.");
            return true;
        }

        SamaGamesAPI.get().getGameManager().getGame().startGame();
        Bukkit.broadcastMessage(ChatColor.GREEN + "Le jeu a été démarré par " + sender.getName());

        return true;
    }
}
