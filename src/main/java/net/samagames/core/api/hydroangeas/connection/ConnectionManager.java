package net.samagames.core.api.hydroangeas.connection;

import com.google.gson.Gson;
import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.hydroangeas.HydroangeasManager;
import net.samagames.core.api.hydroangeas.packets.queues.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager
{
    private APIPlugin plugin;

    private final HydroangeasManager manager;
    private final Gson gson;
    private final Packet[] packets;

    public ConnectionManager(APIPlugin plugin, HydroangeasManager manager)
    {
        this.plugin = plugin;
        this.manager = manager;
        this.gson = new Gson();

        this.packets = new Packet[256];

        // Queues Packets (out)
        this.packets[100] = new QueueAddPlayerPacket();
        this.packets[101] = new QueueRemovePlayerPacket();
        this.packets[102] = new QueueAttachPlayerPacket();
        this.packets[103] = new QueueDetachPlayerPacket();
        this.packets[104] = new QueueInfosUpdatePacket();

    }

    public void getPacket(String packet)
    {
        String id;

        try
        {
            id = packet.split(":")[0];

            if(id == null || this.packets[Integer.parseInt(id)] == null)
            {
                //TODO change logger
                Logger.getAnonymousLogger().log(Level.SEVERE, "Error bad packet ID in the channel");
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //TODO change logger
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error packet no ID in the channel");

            return;
        }

        packet = packet.substring(id.length()+1, packet.length());

        this.handler(Integer.valueOf(id), packet);
    }

    public int packetId(Packet p)
    {
        for (int i = 0; i < this.packets.length; i++)
        {
            if(this.packets[i] == null)
                continue;

            if(this.packets[i].getClass().equals(p.getClass()))
                return i;
        }

        return -1;
    }

    public void sendPacket(String channel, Packet data)
    {
        int id = this.packetId(data);

        if(id < 0)
        {
            //TODO change logger
            Logger.getAnonymousLogger().log(Level.SEVERE, "Bad packet ID: " + id);
            return;
        }
        else if(channel == null)
        {
            //TODO change logger
            Logger.getAnonymousLogger().log(Level.SEVERE, "Channel null !");
            return;
        }

        try
        {
            SamaGamesAPI.get().getPubSub().send(channel, id + ":" + this.gson.toJson(data));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet packet)
    {
        this.sendPacket("global@hydroangeas-server", packet);
    }

    public void handler(int id, String data)
    {
        try
        {
            this.manager.getPacketReceiver().callPacket(this.gson.fromJson(data, this.packets[id].getClass()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
