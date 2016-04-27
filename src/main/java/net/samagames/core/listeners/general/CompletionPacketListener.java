package net.samagames.core.listeners.general;

import io.netty.channel.Channel;
import net.minecraft.server.v1_9_R1.PacketPlayInTabComplete;
import net.minecraft.server.v1_9_R1.PacketPlayOutTabComplete;
import net.samagames.core.APIPlugin;
import net.samagames.tools.TinyProtocol;
import org.bukkit.entity.Player;

public class CompletionPacketListener extends TinyProtocol
{
    /**
     * Construct a new instance of TinyProtocol, and start intercepting packets for all connected clients and future clients.
     * <p>
     * You can construct multiple instances per plugin.
     *
     * @param plugin - the plugin.
     */
    public CompletionPacketListener(APIPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public Object onPacketInAsync(Player receiver, Channel channel, Object packet)
    {
        if (packet instanceof PacketPlayInTabComplete)
        {
            PacketPlayInTabComplete p = (PacketPlayInTabComplete) packet;

            if (p.a().startsWith("/") && p.a().split(" ").length == 1)
            {
                PacketPlayOutTabComplete newPacket = new PacketPlayOutTabComplete(new String[0]);
                this.sendPacket(receiver, newPacket);
            }
        }

        return super.onPacketInAsync(receiver, channel, packet);
    }
}
