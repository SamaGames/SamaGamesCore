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

/**
 ╱╲＿＿＿＿＿＿╱╲
 ▏╭━━╮╭━━╮▕
 ▏┃＿＿┃┃＿＿┃▕
 ▏┃＿▉┃┃▉＿┃▕
 ▏╰━━╯╰━━╯▕
 ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 14/04/2016
 ╱╰╯╰╯╰╯╰╯╲
 ▏▕╰╯╰╯╰╯▏▕
 ▏▕╯╰╯╰╯╰▏▕
 ╲╱╲╯╰╯╰╱╲╱
 ＿＿╱▕▔▔▏╲＿＿
 ＿＿▔▔＿＿▔▔＿＿
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
                if (Bukkit.getOfflinePlayer(packetObj.player).isOnline())
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
