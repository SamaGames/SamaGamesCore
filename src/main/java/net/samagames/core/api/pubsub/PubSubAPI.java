package net.samagames.core.api.pubsub;

import net.samagames.api.pubsub.*;
import net.samagames.core.ApiImplementation;
import redis.clients.jedis.Jedis;

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
public class PubSubAPI implements IPubSubAPI
{

    private Subscriber subscriberPattern;
    private Subscriber subscriberChannel;

    private Sender sender;
    private ApiImplementation api;

    boolean working = true;

    private Thread senderThread;
    private Thread patternThread;
    private Thread channelThread;

    // Avoid to init Threads before the subclass constructor is started (Fix possible atomicity violation)
    public PubSubAPI(ApiImplementation api)
    {
        this.api = api;
        subscriberPattern = new Subscriber();
        subscriberChannel = new Subscriber();

        sender = new Sender(api);
        senderThread = new Thread(sender, "SenderThread");
        senderThread.start();

        startThread();
    }

    private void startThread()
    {
        patternThread = new Thread(() -> {
            while (working)
            {
                Jedis jedis = api.getBungeeResource();
                try
                {
                    String[] patternsSuscribed = subscriberPattern.getPatternsSuscribed();
                    if(patternsSuscribed.length > 0)
                        jedis.psubscribe(subscriberPattern, patternsSuscribed);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                jedis.close();
            }
        });
        patternThread.start();

        channelThread = new Thread(() -> {
            while (working)
            {
                Jedis jedis = api.getBungeeResource();
                try
                {
                    String[] channelsSuscribed = subscriberChannel.getChannelsSuscribed();
                    if (channelsSuscribed.length > 0)
                        jedis.subscribe(subscriberChannel, channelsSuscribed);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                jedis.close();
            }
        });
        channelThread.start();
    }

    @Override
    public void subscribe(String channel, IPacketsReceiver receiver)
    {
        subscriberChannel.registerReceiver(channel, receiver);
        if(subscriberChannel.isSubscribed())
            subscriberChannel.unsubscribe();
    }

    @Override
    public void subscribe(String pattern, IPatternReceiver receiver)
    {
        subscriberPattern.registerPattern(pattern, receiver);
        if(subscriberPattern.isSubscribed())
            subscriberPattern.punsubscribe();
    }

    @Override
    public void send(String channel, String message)
    {
        sender.publish(new PendingMessage(channel, message));
    }

    @Override
    public void send(PendingMessage message)
    {
        sender.publish(message);
    }

    @Override
    public ISender getSender()
    {
        return sender;
    }

    public void disable()
    {
        working = false;
        subscriberChannel.unsubscribe();
        subscriberPattern.punsubscribe();
        try
        {
            Thread.sleep(500);
        } catch (Exception ignored)
        {
        }

        senderThread.stop();
        patternThread.stop();
        channelThread.stop();
    }
}
