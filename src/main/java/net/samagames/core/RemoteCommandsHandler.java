package net.samagames.core;

import net.samagames.api.pubsub.IPacketsReceiver;
import org.bukkit.Bukkit;

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
class RemoteCommandsHandler implements IPacketsReceiver
{
    private APIPlugin plugin;

    public RemoteCommandsHandler(APIPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void receive(String channel, String command)
    {
        Bukkit.getLogger().info("Executing command remotely : " + command);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }
}
