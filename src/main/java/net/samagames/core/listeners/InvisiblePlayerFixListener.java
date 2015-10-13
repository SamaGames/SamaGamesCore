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

import java.util.UUID;

/**
 * Created by Silva on 13/10/2015.
 */
public class InvisiblePlayerFixListener implements IJoinHandler
{
    private final APIPlugin pluginAPI;

    public InvisiblePlayerFixListener(APIPlugin pluginAPI)
    {
        this.pluginAPI = pluginAPI;
    }

    @Override
    public void onLogin(UUID player, String username)
    {
        try{
            sendPlayerToAll(player);
            sendAllToPlayer(player);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void sendPlayerToAll(UUID player)
    {
        Bukkit.getScheduler().runTaskAsynchronously(pluginAPI, () -> {
            EntityPlayer entity = ((CraftPlayer)Bukkit.getPlayer(player)).getHandle();
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity);

            for(Player p : Bukkit.getOnlinePlayers())
            {
                ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
            }
        });
    }

    public void sendAllToPlayer(UUID uuid)
    {
        Player p = Bukkit.getPlayer(uuid);

        Bukkit.getScheduler().runTaskAsynchronously(pluginAPI, () -> {

            for(Player player : Bukkit.getOnlinePlayers())
            {
                EntityPlayer entity = ((CraftPlayer)player).getHandle();
                ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));
            }
        });
    }

}
