package net.samagames.core.rest;

import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.core.rest.request.Request;
import net.samagames.core.rest.response.CoinsResponse;
import net.samagames.core.rest.response.LoginResponse;
import net.samagames.core.rest.response.Response;
import net.samagames.core.rest.response.StarsResponse;

import java.util.Date;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class RestPlayerData extends PlayerData
{
    protected RestPlayerData(UUID playerID, ApiImplementation api, PlayerDataManager manager)
    {
        super(playerID, api, manager);
    }

    public void readData(LoginResponse response)
    {
        if (playerID == response.getUuid())
            return;
        playerData.put("coins", String.valueOf(response.getCoins()));
        playerData.put("stars", String.valueOf(response.getStars()));
    }

    @Override
    public void updateData()
    {
        lastRefresh = new Date();

        // Waiting for Raesta to implement it
    }

    @Override
    public Integer getInt(String key)
    {
        if (key.equalsIgnoreCase("stars") && !playerData.containsKey(key))
        {
            Response response = RestAPI.getInstance().sendRequest("economy/stars", new Request().addProperty("playerUUID", playerID), StarsResponse.class, "POST");
            if (response instanceof StarsResponse)
            {
                StarsResponse starsResponse = (StarsResponse) response;
                playerData.put(key, String.valueOf(starsResponse.getStars()));
                return starsResponse.getStars();
            }

        } else if (key.equalsIgnoreCase("coins") && !playerData.containsKey(key))
        {
            Response response = RestAPI.getInstance().sendRequest("economy/coins", new Request().addProperty("playerUUID", playerID), StarsResponse.class, "POST");
            if (response instanceof CoinsResponse)
            {
                CoinsResponse coinsResponse = (CoinsResponse) response;
                playerData.put(key, String.valueOf(coinsResponse.getCoins()));
                return coinsResponse.getCoins();
            }
        }
        return super.getInt(key);
    }

    @Override
    public void set(String key, String value)
    {
        playerData.put(key, value);

        // Waiting for Raesta to implement it
    }

    @Override
    public void remove(String key)
    {
        playerData.remove(key);

        // Waiting for Raesta to implement it
    }

    @Override
    public long increaseCoins(long incrBy)
    {
        Response response = RestAPI.getInstance().sendRequest("economy/coins", new Request().addProperty("playerUUID", playerID).addProperty("count", incrBy), CoinsResponse.class, "PUT");
        if (response instanceof CoinsResponse)
        {
            CoinsResponse coinsResponse = (CoinsResponse) response;
            playerData.put("coins", String.valueOf(coinsResponse.getCoins()));
            return coinsResponse.getCoins();
        }
        return Integer.valueOf(playerData.get("coins"));
    }

    @Override
    public long increaseStars(long incrBy)
    {
        Response response = RestAPI.getInstance().sendRequest("economy/stars", new Request().addProperty("playerUUID", playerID).addProperty("count", incrBy), StarsResponse.class, "PUT");
        if (response instanceof StarsResponse)
        {
            StarsResponse starsResponse = (StarsResponse) response;
            playerData.put("stars", String.valueOf(starsResponse.getStars()));
            return starsResponse.getStars();
        }
        return Integer.valueOf(playerData.get("stars"));
    }
}
