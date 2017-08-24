package net.samagames.core.api.hydroangeas.packets.queues;

import net.samagames.core.api.hydroangeas.QPlayer;

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
public class QueueRemovePlayerPacket extends QueuePacket
{
    private QPlayer player;

    public QueueRemovePlayerPacket() {}

    public QueueRemovePlayerPacket(QPlayer player)
    {
        this.player = player;
    }

    public QPlayer getPlayer()
    {
        return this.player;
    }
}
