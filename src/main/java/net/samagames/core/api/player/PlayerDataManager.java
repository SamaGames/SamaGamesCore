package net.samagames.core.api.player;

import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.player.IPlayerDataManager;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.redis.RedisPlayerData;
import net.samagames.core.rest.RestPlayerData;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class PlayerDataManager implements IPlayerDataManager
{

    private final ApiImplementation api;
    private final ConcurrentHashMap<UUID, PlayerData> cachedData = new ConcurrentHashMap<>();
    private final CoinsManager coinsManager;
    private final StarsManager starsManager;
    private final boolean isRedis;

    public PlayerDataManager(ApiImplementation api, boolean isRedis)
    {
        this.api = api;
        coinsManager = new CoinsManager(api);
        starsManager = new StarsManager(api);
        this.isRedis = isRedis;
    }

    public CoinsManager getCoinsManager()
    {
        return coinsManager;
    }

    public StarsManager getStarsManager()
    {
        return starsManager;
    }

    @Override
    public AbstractPlayerData getPlayerData(UUID player)
    {
        return getPlayerData(player, false);
    }

    @Override
    public AbstractPlayerData getPlayerData(UUID player, boolean forceRefresh)
    {
        if (!cachedData.containsKey(player) && isRedis)
        {
            PlayerData data = new RedisPlayerData(player, api, this);
            cachedData.put(player, data);
            return data;
        }

        PlayerData data = cachedData.get(player);

        if (forceRefresh)
        {
            data.updateData();
            return data;
        }

        data.refreshIfNeeded();
        return data;
    }

    public void load(UUID player, PlayerData data, boolean forceLoad)
    {
        if (!cachedData.containsKey(player) || forceLoad)
        {
            cachedData.put(player, data);
        }
    }

    @Override
    public void unload(UUID player)
    {
        cachedData.remove(player);
    }
}
