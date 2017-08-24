package net.samagames.core.api.network;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.network.JoinResponse;
import net.samagames.api.pubsub.IPacketsReceiver;

import java.util.UUID;

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
public class RegularJoinHandler implements IPacketsReceiver
{

    private final JoinManagerImplement manager;

    public RegularJoinHandler(JoinManagerImplement manager)
    {
        this.manager = manager;
    }

    @Override
    public void receive(String channel, String packet)
    {
        UUID player = UUID.fromString(packet);
        JoinResponse response = manager.requestJoin(player, false);
        if (!response.isAllowed())
        {
            TextComponent component = new TextComponent(response.getReason());
            component.setColor(ChatColor.RED);
            SamaGamesAPI.get().getPlayerManager().sendMessage(player, component);
        } else
        {
            SamaGamesAPI.get().getPlayerManager().connectToServer(player, SamaGamesAPI.get().getServerName());
            SamaGamesAPI.get().getGameManager().refreshArena();
        }
    }
}
