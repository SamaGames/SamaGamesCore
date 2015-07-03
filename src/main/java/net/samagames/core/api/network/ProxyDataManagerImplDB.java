package net.samagames.core.api.network;

import net.samagames.api.network.ProxiedPlayer;
import net.samagames.api.network.ProxyDataManager;
import net.samagames.core.ApiImplementation;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class ProxyDataManagerImplDB implements ProxyDataManager {

	private final ApiImplementation api;

	public ProxyDataManagerImplDB(ApiImplementation api) {
		this.api = api;
	}

	@Override
	public Set<UUID> getPlayersOnServer(String server) {
		Jedis jedis = api.getBungeeResource();
		Set<String> data = jedis.smembers("connectedonserv:" + server);
		jedis.close();

		HashSet<UUID> ret = new HashSet<>();
		data.stream().forEach(str -> ret.add(UUID.fromString(str)));

		return ret;
	}

	@Override
	public Set<UUID> getPlayersOnProxy(String server) {
		Jedis jedis = api.getBungeeResource();
		Set<String> data = jedis.smembers("connected:" + server);
		jedis.close();

		HashSet<UUID> ret = new HashSet<>();
		data.stream().forEach(str -> ret.add(UUID.fromString(str)));

		return ret;
	}

	@Override
	public ProxiedPlayer getProxiedPlayer(UUID uuid) {
		return new ProxiedPlayerDB(uuid);
	}

	@Override
	public void apiexec(String command, String... args) {
		api.getPubSub().send("apiexec." + command, StringUtils.join(args, " "));
	}

	@Override
	public Map<String, String> getServers() {
		Jedis jedis = api.getBungeeResource();
		Map<String, String> servers = jedis.hgetAll("servers");
		jedis.close();

		return servers;
	}
}
