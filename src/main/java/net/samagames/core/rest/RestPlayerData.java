package net.samagames.core.rest;

import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.core.rest.request.Request;
import net.samagames.core.rest.response.*;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class RestPlayerData extends PlayerData
{
    private Logger logger;

    public RestPlayerData(UUID playerID, ApiImplementation api, PlayerDataManager manager)
    {
        super(playerID, api, manager);
        logger = api.getPlugin().getLogger();
    }

    public void onLogin(LoginResponse response)
    {
        if (!playerID.equals(response.getUuid()))
            return;

        logger.info("Loading " + response);
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
    public String get(String key)
    {
        if (key.equalsIgnoreCase("stars") && !playerData.containsKey(key))
            return getStarsInternal();
        else if (key.equalsIgnoreCase("coins") && !playerData.containsKey(key))
            return getCoinsInternal();
        else if (key.startsWith("settings.") && !playerData.containsKey(key))
            return getSetting(key.substring(key.indexOf(".") + 1));
        return super.get(key);
    }

    private String getSetting(String key)
    {
        Response response = RestAPI.getInstance().sendRequest("player/setting", new Request().addProperty("playerUUID", playerID).addProperty("key", key), ValueResponse.class, "POST");
        if (response instanceof ValueResponse)
        {
            String value = ((ValueResponse) response).getValue();
            playerData.put(key, value);
            return value;
        }
        return null;
    }

    @Override
    public Boolean getBoolean(String key)
    {
        return super.getBoolean(key);
    }

    @Override
    public void set(String key, String value)
    {
        if (key.equalsIgnoreCase("coins"))
        {
            String oldValue = playerData.get("coins");
            int toRemove = 0;
            if (oldValue != null)
                toRemove = Integer.valueOf(oldValue);
            increaseCoins((-toRemove) + Integer.valueOf(value));
        } else if (key.equalsIgnoreCase("stars"))
        {
            String oldValue = playerData.get("stars");
            int toRemove = 0;
            if (oldValue != null)
                toRemove = Integer.valueOf(oldValue);
            increaseStars((-toRemove) + Integer.valueOf(value));
        } else if (key.startsWith("settings."))
            setSetting(key.substring(key.indexOf(".") + 1), value);

        playerData.put(key, value);

        // Waiting for Raesta to implement it
        logger.info("Set (" + key + ": " + value + ")");
    }

    private void setSetting(String key, String value)
    {
        Response response = RestAPI.getInstance().sendRequest("player/setting", new Request().addProperty("playerUUID", playerID).addProperty("key", key).addProperty("value", value), StatusResponse.class, "PUT");
        boolean isErrored = true;
        if (response instanceof StatusResponse)
            isErrored = !((StatusResponse) response).getStatus();

        if (isErrored)
            logger.warning("Cannot set key " + key + " with value " + value);
    }

    @Override
    public void remove(String key)
    {
        playerData.remove(key);

        // Waiting for Raesta to implement it
        logger.info("Remove " + key);
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

    private String getCoinsInternal()
    {
        Response response = RestAPI.getInstance().sendRequest("economy/coins", new Request().addProperty("playerUUID", playerID), StarsResponse.class, "POST");
        if (response instanceof CoinsResponse)
        {
            CoinsResponse coinsResponse = (CoinsResponse) response;
            String value = String.valueOf(coinsResponse.getCoins());
            playerData.put("coins", value);
            return value;
        }
        return "0";
    }

    public String getStarsInternal()
    {
        Response response = RestAPI.getInstance().sendRequest("economy/stars", new Request().addProperty("playerUUID", playerID), StarsResponse.class, "POST");
        if (response instanceof StarsResponse)
        {
            StarsResponse starsResponse = (StarsResponse) response;
            String value = String.valueOf(starsResponse.getStars());
            playerData.put("stars", value);
            return value;
        }
        return "0";
    }
}
