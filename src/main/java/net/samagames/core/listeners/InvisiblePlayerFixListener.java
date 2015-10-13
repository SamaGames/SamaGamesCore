package net.samagames.core.listeners;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.samagames.api.network.IJoinHandler;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.restfull.RestAPI;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

/**
 * Created by Silva on 13/10/2015.
 */
public class InvisiblePlayerFixListener implements Listener
{
    private final APIPlugin pluginAPI;

    public InvisiblePlayerFixListener(APIPlugin pluginAPI)
    {
        this.pluginAPI = pluginAPI;
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event)
    {
        try{
            sendPlayerToAll(event.getPlayer());
            sendAllToPlayer(event.getPlayer());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendAllToPlayer(Player current)
    {
        Bukkit.getScheduler().runTaskAsynchronously(pluginAPI, () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                EntityPlayer entity = ((CraftPlayer) player).getHandle();
                ((CraftPlayer) current).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));
            }
        });
    }

    public void sendPlayerToAll(Player player)
    {
        Bukkit.getScheduler().runTaskAsynchronously(pluginAPI, () -> {
            EntityPlayer entity = ((CraftPlayer) player).getHandle();
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity);

            for (Player p : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
        });
    }

}
