package net.samagames.core.api.parties;

import net.samagames.api.parties.IParty;

import java.util.List;
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
public class Party implements IParty {

    private UUID party;
    private UUID leader;

    private List<UUID> players;

    public Party(UUID party, UUID leader, List<UUID> players)
    {
        this.party = party;
        this.leader = leader;
        this.players = players;
    }

    @Override
    public UUID getParty()
    {
        return party;
    }

    @Override
    public UUID getLeader() {
        return leader;
    }

    @Override
    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    @Override
    public boolean containsPlayer(UUID player)
    {
        return players.contains(player);
    }

    @Override
    public List<UUID> getPlayers() {
        return players;
    }

}
