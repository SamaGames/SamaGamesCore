package net.samagames.core.listeners.pubsub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.samagames.api.pubsub.IPacketsReceiver;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.permissions.PermissionEntity;
import org.bukkit.Bukkit;

import java.util.UUID;

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
public class GlobalUpdateListener implements IPacketsReceiver {

    private final APIPlugin plugin;
    private ApiImplementation api;
    private final Gson gson;

    public GlobalUpdateListener(APIPlugin plugin) {
        this.plugin = plugin;
        this.api = plugin.getAPI();
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void receive(String channel, String packet) {

        if (channel.equals("global"))
        {
            if (packet.equalsIgnoreCase("reboot"))
            {
                plugin.getServer().shutdown();
            } else if (packet.equalsIgnoreCase("rebootIfEmpty"))
            {
                if (plugin.getServer().getOnlinePlayers().size() == 0)
                    plugin.getServer().shutdown();
            }
        }else if (channel.equals("groupchange"))
        {
            try
            {
                GroupChangePacket packetObj = gson.fromJson(packet, GroupChangePacket.class);
                PermissionEntity user = api.getPermissionsManager().getPlayer(packetObj.playerUUID);
                user.refresh();
            } catch (JsonSyntaxException ignored)
            {
                //To be sure
                ignored.printStackTrace();
            }

        }else if (channel.equals("networkEvent_WillQuit"))
        {
            try
            {
                PlayerLeaveEvent packetObj = gson.fromJson(packet, PlayerLeaveEvent.class);
                if (Bukkit.getPlayer(packetObj.player) != null)
                {
                    plugin.getGlobalJoinListener().onWillLeave(packetObj.player, packetObj.targetServer);
                }
            } catch (JsonSyntaxException ignored)
            {
                //To be sure
                ignored.printStackTrace();
            }
        }
        //TODO listen shit

    }

    private class GroupChangePacket
    {
        private String type;
        private UUID playerUUID;
        private String target;
    }

    public class PlayerLeaveEvent
    {
        private UUID player;
        private String targetServer;
    }
}
