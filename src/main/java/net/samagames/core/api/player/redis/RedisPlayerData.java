package net.samagames.core.api.player.redis;

import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.player.PlayerDataManager;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class RedisPlayerData extends PlayerData
{

    public RedisPlayerData(UUID player, ApiImplementation api, PlayerDataManager manager)
    {
        super(player, api, manager);
        updateData();
    }

    @Override
    public void updateData()
    {
        Jedis jedis = api.getResource();
        Map<String, String> data = jedis.hgetAll("player:" + playerID);
        jedis.close();

        playerData = data;
        lastRefresh = new Date();
    }

    @Override
    public void set(String key, String value)
    {
        playerData.put(key, value);

        Jedis jedis = api.getResource();
        jedis.hset("player:" + playerID, key, value);
        jedis.close();
    }

    @Override
    public void remove(String key)
    {
        playerData.remove(key);

        Jedis jedis = api.getResource();
        jedis.hdel("player:" + playerID, key);
        jedis.close();
    }

    @Override
    public long increaseCoins(long incrBy)
    {
        Jedis jedis = api.getResource();
        long newValue = jedis.hincrBy("player:" + playerID, "coins", incrBy);
        jedis.close();

        playerData.put("coins", String.valueOf(newValue));
        return newValue;
    }

    @Override
    public long increaseStars(long incrBy)
    {
        Jedis jedis = api.getResource();
        long newValue = jedis.hincrBy("player:" + playerID, "stars", incrBy);
        jedis.close();

        playerData.put("stars", String.valueOf(newValue));
        return newValue;
    }
}
