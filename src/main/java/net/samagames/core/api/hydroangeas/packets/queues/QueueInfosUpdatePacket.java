package net.samagames.core.api.hydroangeas.packets.queues;


import net.samagames.core.api.hydroangeas.QPlayer;
import net.samagames.core.api.hydroangeas.connection.Packet;

import java.util.List;

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
