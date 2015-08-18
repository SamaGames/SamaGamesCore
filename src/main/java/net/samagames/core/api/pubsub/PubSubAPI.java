package net.samagames.core.api.pubsub;

import net.samagames.api.pubsub.*;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class PubSubAPI implements IPubSubAPI {

	private Subscriber subscriber;
	private Sender sender;
	private boolean continueSub = true;

	public PubSubAPI(ApiImplementation api) {
		subscriber = new Subscriber();
		new Thread(() -> {
			while (continueSub) {
				Jedis jedis = api.getResource();
				try {
					jedis.psubscribe(subscriber, "*");
					subscriber.registerPattern("*", APIPlugin.getInstance().getDebugListener());
				} catch (Exception e) {
					e.printStackTrace();
				}

				Bukkit.getLogger().info("Disconnected from master.");
				jedis.close();
			}
		}).start();

		Bukkit.getLogger().info("Waiting for subscribing...");
		while (!subscriber.isSubscribed())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		Bukkit.getLogger().info("Correctly subscribed.");

		sender = new Sender(api);
		new Thread(sender, "SenderThread").start();
	}

	@Override
	public void subscribe(String channel, IPacketsReceiver receiver) {
		subscriber.registerReceiver(channel, receiver);
	}

	@Override
	public void subscribe(String pattern, IPatternReceiver receiver) {
		subscriber.registerPattern(pattern, receiver);
	}

	@Override
	public void send(String channel, String message) {
		sender.publish(new PendingMessage(channel, message));
	}

	@Override
	public void send(PendingMessage message) {
		sender.publish(message);
	}

	@Override
	public ISender getSender() {
		return sender;
	}

	public void disable() {
		continueSub = false;
		subscriber.unsubscribe();
		subscriber.punsubscribe();
		try {
			Thread.sleep(500);
		} catch (Exception ignored) {}
	}
}
