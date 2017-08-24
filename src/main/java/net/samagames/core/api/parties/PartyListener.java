package net.samagames.core.api.parties;

import net.samagames.api.pubsub.IPacketsReceiver;
import net.samagames.core.APIPlugin;

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
public class PartyListener implements IPacketsReceiver
{

    private final PartiesManager partiesManager;
    private final APIPlugin plugin;

    public PartyListener(APIPlugin plugin, PartiesManager partiesManager)
    {
        this.plugin = plugin;
        this.partiesManager = partiesManager;
    }

    @Override
    public void receive(String channel, String message) {
        String[] parts = channel.split("\\.");
        if (parts.length < 2)
            return;

        String action = parts[1];
        String[] args = message.split(" ");

        switch (action)
        {
            case "disband":
            {
                if (args.length < 1)
                    return;

                UUID player = UUID.fromString(args[0]);
                Party partyForPlayer = partiesManager.getPartyForPlayer(player);
                if (partyForPlayer != null)
                {
                    partyForPlayer.getPlayers().clear();
                    partiesManager.unloadParties();
                }
                break;
            }
            case "join":
            case "kick":
            case "leave":
            case "lead":
            case "disconnect":
            {
                if (args.length < 2)
                    return;

                UUID party = UUID.fromString(args[0]);
                partiesManager.loadParty(party);
                //clear cache if no player
                partiesManager.unloadParties();
                break;
            }
        }
    }
}