package net.samagames.core.api.network;

import net.md_5.bungee.api.chat.TextComponent;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.network.JoinResponse;
import net.samagames.api.pubsub.IPacketsReceiver;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.parties.Party;

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
public class PartiesPubSub implements IPacketsReceiver
{

    private ApiImplementation api;
    private final JoinManagerImplement implement;

    public PartiesPubSub(ApiImplementation api, JoinManagerImplement implement)
    {
        this.api = api;
        this.implement = implement;
    }

	/*
    Protocol data :
	partyjoin <uuid of the party>
	 */

    @Override
    public void receive(String channel, String packet)
    {
        UUID partyID = UUID.fromString(packet);
        Party party = api.getPartiesManager().getParty(partyID);
        JoinResponse response = implement.requestPartyJoin(party);

        if (!response.isAllowed())
        {
            TextComponent component = new TextComponent("Impossible de vous connecter : " + response.getReason());
            component.setColor(net.md_5.bungee.api.ChatColor.RED);
            SamaGamesAPI.get().getPlayerManager()
                    .sendMessage(SamaGamesAPI.get().getPartiesManager().getLeader(partyID), component);
        }
    }
}
