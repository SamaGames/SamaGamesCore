package net.samagames.core.api.hydroangeas;

import net.samagames.core.APIPlugin;
import net.samagames.core.api.hydroangeas.connection.Packet;
import net.samagames.core.api.hydroangeas.packets.PacketCallBack;

import java.util.ArrayList;
import java.util.List;

public class PacketReceiver
{
    private final APIPlugin plugin;
    private final List<PacketCallBack> callbacks;

    public PacketReceiver(APIPlugin plugin)
    {
        this.plugin = plugin;
        this.callbacks = new ArrayList<>();
    }

    public void registerCallBack(PacketCallBack callBack)
    {
        this.callbacks.add(callBack);
    }

    public void clearCallbacks()
    {
        this.callbacks.clear();
    }

    public void callPacket(Packet packet)
    {
        this.callbacks.stream().filter(callBack -> callBack.getPacketClass().getName().equals(packet.getClass().getName())).forEach(callBack -> this.plugin.getExecutor().execute(() -> callBack.call(packet)));
    }
}
