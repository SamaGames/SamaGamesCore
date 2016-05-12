package net.samagames.core.api.hydroangeas.packets.queues;


import net.samagames.core.api.hydroangeas.QPlayer;

import java.util.List;

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
