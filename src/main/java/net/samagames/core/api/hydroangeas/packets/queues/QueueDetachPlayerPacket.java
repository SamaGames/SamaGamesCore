package net.samagames.core.api.hydroangeas.packets.queues;

import net.samagames.core.api.hydroangeas.QPlayer;

import java.util.List;

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
public class QueueDetachPlayerPacket extends QueuePacket
{
    private QPlayer leader;
    private List<QPlayer> players;

    public QueueDetachPlayerPacket() {}

    public QueueDetachPlayerPacket(QPlayer leader, List<QPlayer> players)
    {
        this.leader = leader;
        this.players = players;
    }

    public QPlayer getLeader()
    {
        return this.leader;
    }

    public List<QPlayer> getPlayers()
    {
        return this.players;
    }
}
