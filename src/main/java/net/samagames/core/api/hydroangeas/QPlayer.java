package net.samagames.core.api.hydroangeas;

import java.util.UUID;

public class QPlayer
{
    private UUID uuid;
    private int priority;

    public QPlayer(UUID uuid, int priority)
    {
        this.uuid = uuid;
        this.priority = priority;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public void setUUID(UUID uuid)
    {
        this.uuid = uuid;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }
}
