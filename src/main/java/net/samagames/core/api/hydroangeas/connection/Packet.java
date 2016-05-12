package net.samagames.core.api.hydroangeas.connection;

public class Packet
{
    private final Runnable callback;

    public Packet(Runnable callback)
    {
        this.callback = callback;
    }

    public Packet()
    {
        this.callback = null;
    }

    public void callback()
    {
        try
        {
            if (this.callback != null)
                this.callback.run();
        }
        catch (Exception ignored) {}
    }
}
