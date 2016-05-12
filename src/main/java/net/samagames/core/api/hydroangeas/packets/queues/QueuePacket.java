package net.samagames.core.api.hydroangeas.packets.queues;

import net.samagames.core.api.hydroangeas.connection.Packet;

public class QueuePacket extends Packet
{
    public enum TypeQueue {NAMEDID, NAMED, RANDOM, FAST}

    private String game;
    private String map;
    private String templateID;
    private TypeQueue typeQueue;

    public QueuePacket() {}

    public QueuePacket(TypeQueue typeQueue, String game, String map)
    {
        this(typeQueue, game + "_" + map);

        this.game = game;
        this.map = map;
    }

    public QueuePacket(TypeQueue typeQueue, String templateID)
    {
        this.typeQueue = typeQueue;
        this.templateID = templateID;
    }

    public TypeQueue getTypeQueue()
    {
        return this.typeQueue;
    }

    public String getTemplateID()
    {
        return this.templateID;
    }

    public String getMap()
    {
        return this.map;
    }

    public String getGame()
    {
        return this.game;
    }
}
