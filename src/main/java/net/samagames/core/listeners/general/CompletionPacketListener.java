package net.samagames.core.listeners.general;

import io.netty.channel.Channel;
import net.minecraft.server.v1_9_R1.PacketPlayInTabComplete;
import net.minecraft.server.v1_9_R1.PacketPlayOutTabComplete;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.player.AbstractPlayerData;
import net.samagames.core.APIPlugin;
import net.samagames.tools.TinyProtocol;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

            if (SamaGamesAPI.get().getPermissionsManager().hasPermission(receiver, "network.staff"))
                return super.onPacketInAsync(receiver, channel, packet);

            if (p.a().startsWith("/") && p.a().split(" ").length == 1)
            {
                PacketPlayOutTabComplete newPacket = new PacketPlayOutTabComplete(new String[0]);
                this.sendPacket(receiver, newPacket);

                return null;
            }
            else if (p.a().length() >= 1)
            {
                String name = p.a();
                List<String> players = new ArrayList<>();

                Bukkit.broadcastMessage("Base: " + name);

                for (Player player : this.plugin.getServer().getOnlinePlayers())
                {
                    AbstractPlayerData playerData = SamaGamesAPI.get().getPlayerManager().getPlayerData(player.getUniqueId());

                    if (player.getName().startsWith(name) && !playerData.hasNickname())
                    {
                        Bukkit.broadcastMessage(player.getName() + "'s normal name matches.");
                        players.add(player.getName());
                    }
                    else if (playerData.hasNickname() && playerData.getCustomName().startsWith(name))
                    {
                        Bukkit.broadcastMessage(player.getName() + "'s custom name matches.");
                        players.add(playerData.getCustomName());
                    }
                }

                String[] playersName = new String[players.size()];

                for (int i = 0; i < players.size(); i++)
                    playersName[i] = players.get(i);

                Bukkit.broadcastMessage("Autocompletion: " + String.join(", ", playersName));

                PacketPlayOutTabComplete newPacket = new PacketPlayOutTabComplete(playersName);
                this.sendPacket(receiver, newPacket);

                return null;
            }
        }

        return super.onPacketInAsync(receiver, channel, packet);
    }
}
