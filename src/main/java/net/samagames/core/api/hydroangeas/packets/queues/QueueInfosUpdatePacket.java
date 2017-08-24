package net.samagames.core.api.hydroangeas.packets.queues;


import net.samagames.core.api.hydroangeas.QPlayer;
import net.samagames.core.api.hydroangeas.connection.Packet;

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
public class QueueInfosUpdatePacket extends Packet
{
    private Type type;
    private boolean success;
    private String errorMessage;

    private List<String> message;

    private String game;
    private String map;

    private QPlayer player;

    public QueueInfosUpdatePacket() {}

    public QueueInfosUpdatePacket(QPlayer player, Type type, boolean success, String errorMessage)
    {
        this.player = player;
        this.type = type;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public QueueInfosUpdatePacket(QPlayer player, Type type, String game, String map)
    {
        this.player = player;
        this.type = type;
        this.game = game;
        this.map = map;
    }

    public Type getType()
    {
        return this.type;
    }

    public boolean isSuccess()
    {
        return this.success;
    }

    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    public String getGame()
    {
        return this.game;
    }

    public String getMap()
    {
        return this.map;
    }

    public QPlayer getPlayer()
    {
        return this.player;
    }

    public List<String> getMessage()
    {
        return this.message;
    }

    public void setMessage(List<String> message)
    {
        this.message = message;
    }

    public enum Type {ADD, REMOVE, INFO}
}
