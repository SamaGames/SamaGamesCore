package net.samagames.core.api.pubsub;

import net.samagames.api.pubsub.IPacketsReceiver;
import net.samagames.api.pubsub.IPatternReceiver;
import net.samagames.core.APIPlugin;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class Subscriber extends JedisPubSub {

	protected HashMap<String, HashSet<IPacketsReceiver>> packetsReceivers = new HashMap<>();
	protected HashMap<String, HashSet<IPatternReceiver>> patternsReceivers = new HashMap<>();

	public void registerReceiver(String channel, IPacketsReceiver receiver) {
		HashSet<IPacketsReceiver> receivers = packetsReceivers.get(channel);
		if (receivers == null)
			receivers = new HashSet<>();
		receivers.add(receiver);
		this.subscribe(channel);
		packetsReceivers.put(channel, receivers);
	}

	public void registerPattern(String pattern, IPatternReceiver receiver) {
		HashSet<IPatternReceiver> receivers = patternsReceivers.get(pattern);
		if (receivers == null)
			receivers = new HashSet<>();
		receivers.add(receiver);
		this.psubscribe(pattern);
		patternsReceivers.put(pattern, receivers);
	}

	@Override
	public void onMessage(String channel, String message) {
		try {
			HashSet<IPacketsReceiver> receivers = packetsReceivers.get(channel);
			if (receivers != null)
				receivers.forEach((IPacketsReceiver receiver) -> receiver.receive(channel, message));
			else
				APIPlugin.log(Level.WARNING, "{PubSub} Received message on a channel, but no packetsReceivers were found.");
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}

	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		try {
			HashSet<IPatternReceiver> receivers = patternsReceivers.get(pattern);
			if (receivers != null)
				receivers.forEach((IPatternReceiver receiver) -> receiver.receive(pattern, channel, message));
			else
				APIPlugin.log(Level.WARNING, "{PubSub} Received pmessage on a channel, but no packetsReceivers were found.");
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
	}
}
