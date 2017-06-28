package net.samagames.core.listeners.general;

import io.netty.channel.Channel;
import net.minecraft.server.v1_12_R1.PacketPlayInTabComplete;
import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import net.samagames.tools.TinyProtocol;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        if (PacketPlayInTabComplete.class.isAssignableFrom(packet.getClass()))
        {
            if (SamaGamesAPI.get().getPermissionsManager().hasPermission(receiver, "network.staff"))
                return super.onPacketInAsync(receiver, channel, packet);

            try
            {
                Method getCommandMethod = packet.getClass().getMethod("a");
                String command = (String) getCommandMethod.invoke(packet);

                if (command.startsWith("/") && command.split(" ").length == 1)
                {
                    this.sendPacket(receiver, new PacketPlayInTabComplete());
                    return null;
                }
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

        return super.onPacketInAsync(receiver, channel, packet);
    }
}
