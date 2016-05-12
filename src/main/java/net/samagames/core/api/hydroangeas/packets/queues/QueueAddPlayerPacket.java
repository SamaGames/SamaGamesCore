package net.samagames.core.api.hydroangeas.packets.queues;


import net.samagames.core.api.hydroangeas.QPlayer;

public class QueueAddPlayerPacket extends QueuePacket
{
    private QPlayer player;

    public QueueAddPlayerPacket() {}

    public QueueAddPlayerPacket(QueuePacket.TypeQueue typeQueue, String game, String map, QPlayer player)
    {
        super(typeQueue, game, map);

        this.player = player;
    }

    public QueueAddPlayerPacket(QueuePacket.TypeQueue typeQueue, String templateID, QPlayer player)
    {
        super(typeQueue, templateID);

        this.player = player;
    }

    public QPlayer getPlayer()
    {
        return this.player;
    }
}
