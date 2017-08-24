package net.samagames.core;

import net.md_5.bungee.api.ChatColor;
import net.samagames.api.network.IJoinHandler;
import net.samagames.api.pubsub.IPatternReceiver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

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
public class DebugListener implements IPatternReceiver, IJoinHandler
{

    private final CopyOnWriteArraySet<UUID> debugs = new CopyOnWriteArraySet<>();
    private boolean console = false;

    public void toggle(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            UUID id = ((Player) sender).getUniqueId();
            if (debugs.contains(id))
                debugs.add(id);
            else
                debugs.remove(id);
        } else
        {
            console = !console;
        }
    }

    @Override
    public void onLogout(Player player)
    {
        debugs.remove(player.getUniqueId());
    }

    @Override
    public void receive(String pattern, String channel, String packet)
    {
        if (channel.equals("__sentinel__:hello"))
            return;

        String send = ChatColor.AQUA + "[BukkitDebug : " + channel + "] " + packet;
        for (UUID debug : debugs)
        {
            Player player = Bukkit.getPlayer(debug);
            if (player != null)
                player.sendMessage(send);
        }

        if (console)
            Bukkit.getConsoleSender().sendMessage(send);
    }
}
