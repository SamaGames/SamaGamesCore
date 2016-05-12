package net.samagames.core.api.hydroangeas.packets.queues;

import net.samagames.core.api.hydroangeas.QPlayer;

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
