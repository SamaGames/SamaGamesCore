package net.samagames.core.listeners.pluginmessages;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.samagames.core.ApiImplementation;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 27/04/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

    private ApiImplementation api;

    public PluginMessageListener(ApiImplementation api)
    {

        this.api = api;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("Network"))
        {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subChannel = in.readUTF();
            if (subChannel.equals("addFriend"))
            {
                UUID receiver = UUID.fromString(in.readUTF());
                UUID toAdd = UUID.fromString(in.readUTF());

                api.getFriendsManager().getFriendPlayer(receiver).addFriend(toAdd);
            }else if(subChannel.equals("removeFriend"))
            {
                UUID receiver = UUID.fromString(in.readUTF());
                UUID toRemove = UUID.fromString(in.readUTF());

                //Not safe but don't care
                api.getFriendsManager().getFriendPlayer(receiver).getFriends().remove(toRemove);
            }
        }
    }
}