package net.samagames.core.api.pubsub;

import net.samagames.api.pubsub.*;
import net.samagames.core.ApiImplementation;
import redis.clients.jedis.Jedis;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by .....
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
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
                    jedis.psubscribe(subscriberPattern, subscriberPattern.getPatternsSuscribed());
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
                    jedis.subscribe(subscriberChannel, subscriberChannel.getChannelsSuscribed());
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
        subscriberChannel.unsubscribe();
    }

    @Override
    public void subscribe(String pattern, IPatternReceiver receiver)
    {
        subscriberPattern.registerPattern(pattern, receiver);
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
