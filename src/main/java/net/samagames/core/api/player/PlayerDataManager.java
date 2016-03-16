package net.samagames.core.api.player;

import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.player.IPlayerDataManager;
import net.samagames.core.ApiImplementation;
import net.samagames.core.rest.RestPlayerData;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Silvanosky
 * (C) Copyright Elydra Network 2016-2017
 * All rights reserved.
 */
public class PlayerDataManager implements IPlayerDataManager
{

    private final ApiImplementation api;
    private final ConcurrentHashMap<UUID, PlayerData> cachedData = new ConcurrentHashMap<>();
    private final EconomyManager economyManager;
    private final BukkitTask discountTask;

    public PlayerDataManager(ApiImplementation api)
    {
        this.api = api;
        economyManager = new EconomyManager(api);
        // Run task every 30 minutes
        discountTask = this.api.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(this.api.getPlugin(), economyManager::reload, 0L, 36000L);
    }

    public EconomyManager getEconomyManager()
    {
        return economyManager;
    }

    @Override
    public AbstractPlayerData getPlayerData(UUID player)
    {
        return getPlayerData(player, false);
    }

    @Override
    public AbstractPlayerData getPlayerData(UUID player, boolean forceRefresh)
    {
        if (player == null)
            return null;

        PlayerData data = cachedData.get(player);

        if (data == null)
            return new PlayerData(player, api, (PlayerDataManager) api.getPlayerManager());

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
        if(cachedData.contains(player))
            cachedData.get(player).updateData();
        cachedData.remove(player);
    }


    public void onShutdown()
    {
        discountTask.cancel();
    }
}
