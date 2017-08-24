package net.samagames.core.listeners.pluginmessages;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.samagames.api.achievements.Achievement;
import net.samagames.api.achievements.IncrementationAchievement;
import net.samagames.core.ApiImplementation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener
{
    private ApiImplementation api;

    public PluginMessageListener(ApiImplementation api)
    {
        this.api = api;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message)
    {
        if (channel.equals("Network"))
        {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subChannel = in.readUTF();

            if (subChannel.equals("addFriend"))
            {
                UUID receiver = UUID.fromString(in.readUTF());
                UUID toAdd = UUID.fromString(in.readUTF());

                api.getFriendsManager().getFriendPlayer(receiver).addFriend(toAdd);
            }
            else if(subChannel.equals("removeFriend"))
            {
                UUID receiver = UUID.fromString(in.readUTF());
                UUID toRemove = UUID.fromString(in.readUTF());

                //Not safe but don't care
                try
                {
                    api.getFriendsManager().getFriendPlayer(receiver).getFriends().remove(toRemove);
                }
                catch (Exception ignored) {}

                try
                {
                    api.getFriendsManager().getFriendPlayer(toRemove).getFriends().remove(receiver);
                }
                catch (Exception ignored) {}
            }
            else if(subChannel.equals("updateParty"))
            {
                UUID party = UUID.fromString(in.readUTF());
                api.getPartiesManager().loadParty(party);
            }
        }
        else if (channel.equals("Achievement"))
        {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);

            UUID playerForUnlock = UUID.fromString(in.readUTF());
            int achievementId = in.readInt();
            boolean forTheOthers = in.readBoolean();
            boolean isIncrementType = in.readBoolean();

            Bukkit.getScheduler().runTask(this.api.getPlugin(), () ->
            {
                Achievement achievement = this.api.getAchievementManager().getAchievementByID(achievementId);

                if (forTheOthers)
                {
                    Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId() != playerForUnlock).forEach(p ->
                    {
                        if (isIncrementType)
                            ((IncrementationAchievement) achievement).increment(p.getUniqueId(), in.readInt());
                        else
                            achievement.unlock(p.getUniqueId());
                    });
                }
                else
                {
                    if (isIncrementType)
                        ((IncrementationAchievement) achievement).increment(playerForUnlock, in.readInt());
                    else
                        achievement.unlock(playerForUnlock);
                }
            });
        }
    }
}
