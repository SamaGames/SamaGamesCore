package net.samagames.core.api.player;

import net.samagames.api.player.IPlayerDataManager;
import net.samagames.core.ApiImplementation;

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
    private final ConcurrentHashMap<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    private final EconomyManager economyManager;


    public PlayerDataManager(ApiImplementation api)
    {
        this.api = api;
        economyManager = new EconomyManager(api);
    }

    public EconomyManager getEconomyManager()
    {
        return economyManager;
    }

    @Override
    public PlayerData getPlayerData(UUID player)
    {
        return getPlayerData(player, false);
    }

    @Override
    public PlayerData getPlayerData(UUID player, boolean forceRefresh)
    {
        if (player == null)
            return null;

        PlayerData data = cache.get(player);

        /*if (forceRefresh)
        {
            data.refreshData();
            return data;
        }*/

        //data.refreshIfNeeded();
        return data;
    }

    public void loadPlayer(UUID player)
    {

        PlayerData playerData = new PlayerData(player, api, this);
        cache.put(player, playerData);
    }

    public void unloadPlayer(UUID player)
    {
        //Update data before delete
        if(cache.contains(player))
            cache.get(player).updateData();

        cache.remove(player);
    }

    //TODO nickname


    public void onShutdown()
    {
        economyManager.onShutdown();
    }
}
