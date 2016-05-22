package net.samagames.core.api.player;

import com.google.gson.Gson;
import net.md_5.bungee.api.chat.TextComponent;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.player.IPlayerDataManager;
import net.samagames.core.ApiImplementation;
import org.bukkit.Bukkit;

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

    public PlayerData getPlayerDataByName(String name)
    {
        for (PlayerData data : cache.values())
        {
            if (data.getEffectiveName().equals(name))
                return data;
        }

        return null;
    }

    public void loadPlayer(UUID player)
    {

        PlayerData playerData = new PlayerData(player, api, this);
        cache.put(player, playerData);
    }

    public void unloadPlayer(UUID player)
    {
        //Update data before delete
        /*if(cache.containsKey(player))
            cache.get(player).updateData();*/
        //Continuous update, save here result in data lose for shop

        //Schedule that because of nickname needs
        if (!api.isKeepCache())
        {
            Bukkit.getScheduler().runTaskLater(api.getPlugin(), () -> cache.remove(player), 2L);
        }

    }

    //TODO nickname
    @Override
    public void kickFromNetwork(UUID playerUUID, TextComponent reason)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.kick", playerUUID + " " + new Gson().toJson(reason));
    }

    @Override
    public void connectToServer(UUID playerUUID, String server)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.connect", playerUUID + " " + server);
    }

    @Override
    public void sendMessage(UUID playerUUID, TextComponent component)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.send", playerUUID + " " + new Gson().toJson(component));
    }


    public void onShutdown()
    {
        economyManager.onShutdown();
    }
}
