package net.samagames.core.api.pubsub;

import net.samagames.api.pubsub.IPacketsReceiver;
import net.samagames.api.pubsub.IPatternReceiver;
import net.samagames.core.APIPlugin;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

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
class Subscriber extends JedisPubSub
{

    private final HashMap<String, HashSet<IPacketsReceiver>> packetsReceivers = new HashMap<>();
    private final HashMap<String, HashSet<IPatternReceiver>> patternsReceivers = new HashMap<>();

    public void registerReceiver(String channel, IPacketsReceiver receiver)
    {
        HashSet<IPacketsReceiver> receivers = packetsReceivers.get(channel);
        if (receivers == null)
            receivers = new HashSet<>();
        receivers.add(receiver);
        packetsReceivers.put(channel, receivers);
    }

    public void registerPattern(String pattern, IPatternReceiver receiver)
    {
        HashSet<IPatternReceiver> receivers = patternsReceivers.get(pattern);
        if (receivers == null)
            receivers = new HashSet<>();
        receivers.add(receiver);
        patternsReceivers.put(pattern, receivers);
    }

    @Override
    public void onMessage(String channel, String message)
    {
        try
        {
            HashSet<IPacketsReceiver> receivers = packetsReceivers.get(channel);
            if (receivers != null)
                receivers.forEach((IPacketsReceiver receiver) -> receiver.receive(channel, message));
            else
                APIPlugin.log(Level.WARNING, "{PubSub} Received message on a channel, but no packetsReceivers were found. (channel: " + channel + ", message:" + message + ")");

            APIPlugin.getInstance().getDebugListener().receive("onlychannel", channel, message);
        } catch (Exception ignored)
        {
            ignored.printStackTrace();
        }

    }

    @Override
    public void onPMessage(String pattern, String channel, String message)
    {
        try
        {
            HashSet<IPatternReceiver> receivers = patternsReceivers.get(pattern);
            if (receivers != null)
                receivers.forEach((IPatternReceiver receiver) -> receiver.receive(pattern, channel, message));
            else
                APIPlugin.log(Level.WARNING, "{PubSub} Received pmessage on a channel, but no packetsReceivers were found.");

            APIPlugin.getInstance().getDebugListener().receive(pattern, channel, message);
        } catch (Exception ignored)
        {
            ignored.printStackTrace();
        }
    }

    public String[] getChannelsSuscribed()
    {
        Set<String> strings = packetsReceivers.keySet();
        return strings.toArray(new String[0]);
    }

    public String[] getPatternsSuscribed()
    {
        Set<String> strings = patternsReceivers.keySet();
        return strings.toArray(new String[0]);
    }
}
