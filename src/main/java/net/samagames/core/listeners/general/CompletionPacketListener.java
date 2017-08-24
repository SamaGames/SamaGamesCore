package net.samagames.core.listeners.general;

import io.netty.channel.Channel;
import net.minecraft.server.v1_12_R1.PacketPlayInTabComplete;
import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import net.samagames.tools.TinyProtocol;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * This file is part of SamaGamesCore.
 *
 * SamaGamesCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesCore.  If not, see <http://www.gnu.org/licenses/>.
 */
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
