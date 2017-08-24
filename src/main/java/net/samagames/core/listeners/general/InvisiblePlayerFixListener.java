package net.samagames.core.listeners.general;

import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

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
public class InvisiblePlayerFixListener implements Listener
{
    private final APIPlugin pluginAPI;

    public InvisiblePlayerFixListener(APIPlugin pluginAPI)
    {
        this.pluginAPI = pluginAPI;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        //Don't force if player is hided moderator
        Bukkit.getScheduler().runTaskLater(pluginAPI, () -> {
            if(!pluginAPI.getAPI().getJoinManager().getModeratorsExpected().contains(event.getPlayer().getUniqueId()))
            {
                try{
                    sendPlayerToAll(event.getPlayer());
                    sendAllToPlayer(event.getPlayer());
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, 5L);
    }

    public void sendAllToPlayer(Player current)
    {
        if (current == null)
            return;
        //final EntityPlayer currentNMS = ((CraftPlayer) current).getHandle();
       // Bukkit.getScheduler().runTaskAsynchronously(pluginAPI, () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player == null || current.getName().equals(player.getName()))
                    continue;

                current.hidePlayer(player);
                //TODO show if not moderator
                current.showPlayer(player);

                /*
                EntityPlayer entity = ((CraftPlayer) player).getHandle();
                if (entity == null || currentNMS == null || currentNMS.playerConnection == null)
                    continue;
                currentNMS.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));*/
            }
       // });
    }

    public void sendPlayerToAll(Player current)
    {
        if (current == null)
            return;
       // final EntityPlayer currentNMS = ((CraftPlayer) current).getHandle();
       // Bukkit.getScheduler().runTaskAsynchronously(pluginAPI, () -> {
            //PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, currentNMS);

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p == null || current.getName().equals(p.getName()))
                    continue;

                p.hidePlayer(current);
                p.showPlayer(current);
                /*EntityPlayer entity = ((CraftPlayer) p).getHandle();
                if (entity == null|| currentNMS == null || entity.playerConnection == null)
                    continue;
                entity.playerConnection.sendPacket(packet);*/
            }
        //});
    }

}
