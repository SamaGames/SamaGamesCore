package net.samagames.core.rest;

import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.*;
import net.samagames.restfull.response.elements.ShopElement;
import redis.clients.jedis.Jedis;

import java.util.*;
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

    private HashMap<String, ShopElement> shopCache = new HashMap<>();

    public RestPlayerData(UUID playerID, ApiImplementation api, PlayerDataManager manager)
    {
        super(playerID, api, manager);
        logger = api.getPlugin().getLogger();
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
        else if (key.startsWith("redis."))
            return getFromRedis(key.substring(key.indexOf(".") + 1));
        else if (!playerData.containsKey(key))
            logger.warning("Can't manage get " + key);
        return super.get(key);
    }

    private String getFromRedis(String key)
    {
        Jedis jedis = api.getBungeeResource();
        String result = jedis.hget("player:" + playerID, key);
        jedis.close();
        return result;
    }

    private String getSetting(String key)
    {
        Response response = (Response) RestAPI.getInstance().sendRequest("player/setting", new Request().addProperty("playerUUID", playerID).addProperty("key", key), ValueResponse.class, "POST");
        if (response instanceof ValueResponse)
        {
            String value = ((ValueResponse) response).getValue();
            if(value == null)value = "false";
            playerData.put("settings."+key, value);
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
                toRemove = Integer.parseInt(oldValue);
            increaseCoins((-toRemove) + Integer.parseInt(value));
        } else if (key.equalsIgnoreCase("stars"))
        {
            String oldValue = playerData.get("stars");
            int toRemove = 0;
            if (oldValue != null)
                toRemove = Integer.parseInt(oldValue);
            increaseStars((-toRemove) + Integer.parseInt(value));
        } else if (key.startsWith("settings."))
            setSetting(key.substring(key.indexOf(".") + 1), value);
        else if (key.startsWith("redis."))
            setFromRedis(key.substring(key.indexOf(".") + 1), value);
        else
            logger.warning("Can't manage set " + key + " for value: " + value);

        playerData.put(key, value);

        // Waiting for Raesta to implement it
        logger.info("Set (" + key + ": " + value + ")");
    }

    private void setFromRedis(String key, String value)
    {
        Jedis jedis = api.getBungeeResource();
        jedis.hset("player:" + playerID, key, value);
        jedis.close();
    }

    private void setSetting(String key, String value)
    {
        Response response = (Response) RestAPI.getInstance().sendRequest("player/setting", new Request().addProperty("playerUUID", playerID).addProperty("key", key).addProperty("value", value), StatusResponse.class, "PUT");
        boolean isErrored = true;
        if (response instanceof StatusResponse)
            isErrored = !((StatusResponse) response).getStatus();

        if (isErrored)
        {
            logger.warning("Cannot set key " + key + " with value " + value + "for uuid " + playerID);
        }else
        {
            playerData.put("settings."+key, value);
        }

        api.getPubSub().send("playerDataChange", playerID + ":" + "settings." + key + ":" + value);
    }

    @Override
    public void remove(String key)
    {
        playerData.remove(key);

        if (key.startsWith("redis."))
            removeFromRedis(key.substring(key.indexOf(".") + 1));
    }

    private void removeFromRedis(String key)
    {
        Jedis jedis = api.getBungeeResource();
        jedis.hdel("player:" + playerID, key);
        jedis.close();
    }

    @Override
    public long increaseCoins(long incrBy)
    {
        Response response = (Response) RestAPI.getInstance().sendRequest("economy/coins", new Request().addProperty("playerUUID", playerID).addProperty("count", incrBy), CoinsResponse.class, "PUT");
        if (response instanceof CoinsResponse)
        {
            CoinsResponse coinsResponse = (CoinsResponse) response;
            playerData.put("coins", String.valueOf(coinsResponse.getCoins()));
            return coinsResponse.getCoins();
        }
        return Integer.parseInt(playerData.get("coins"));
    }

    @Override
    public long increaseStars(long incrBy)
    {
        Response response = (Response) RestAPI.getInstance().sendRequest("economy/stars", new Request().addProperty("playerUUID", playerID).addProperty("count", incrBy), StarsResponse.class, "PUT");
        if (response instanceof StarsResponse)
        {
            StarsResponse starsResponse = (StarsResponse) response;
            playerData.put("stars", String.valueOf(starsResponse.getStars()));
            return starsResponse.getStars();
        }
        return Integer.parseInt(playerData.get("stars"));
    }

    private String getCoinsInternal()
    {
        Response response = (Response) RestAPI.getInstance().sendRequest("economy/coins", new Request().addProperty("playerUUID", playerID), CoinsResponse.class, "POST");
        if (response instanceof CoinsResponse)
        {
            CoinsResponse coinsResponse = (CoinsResponse) response;
            String value = String.valueOf(coinsResponse.getCoins());
            playerData.put("coins", value);
            return value;
        } else if (response instanceof ErrorResponse)
        {
            logger.warning("Can't manage coins of " + getEffectiveUUID() + " " + "(" + response + ")");
        }
        return "0";
    }

    public String getStarsInternal()
    {
        Response response = (Response) RestAPI.getInstance().sendRequest("economy/stars", new Request().addProperty("playerUUID", playerID), StarsResponse.class, "POST");
        if (response instanceof StarsResponse)
        {
            StarsResponse starsResponse = (StarsResponse) response;
            String value = String.valueOf(starsResponse.getStars());
            playerData.put("stars", value);
            return value;
        } else if (response instanceof ErrorResponse)
        {
            logger.warning("Can't manage stars of " + getEffectiveUUID() + " " + "(" + response + ")");
        }
        return "0";
    }

    public ShopElement getShopData(String category, String key)
    {
        String cacheName = category + "." + key;
        if (shopCache.containsKey(cacheName))
            return shopCache.get(cacheName);

        Object response = RestAPI.getInstance().sendRequest("player/shop", new Request().addProperty("playerUUID", playerID).addProperty("category", category).addProperty("key", key), ShopElement.class, "POST");
        if (response instanceof ShopElement)
        {
            ShopElement element = (ShopElement) response;
            if (element.getValue() == null)
            {
                element = new GhostShopElement();
            }
            shopCache.put(cacheName, element);
            return element;
        } else {
            ShopElement ghost = new GhostShopElement();
            shopCache.put(cacheName, ghost);
            return ghost;
        }
    }

    public void setShopData(String category, String key, String value)
    {
        String cacheName = category + "." + key;
        if (shopCache.containsKey(cacheName))
            shopCache.get(cacheName).getValue().add(value);
        else
        {
            getShopData(category, key);
            shopCache.get(cacheName).getValue().add(value);
        }

        Object response = RestAPI.getInstance().sendRequest("player/shop", new Request().addProperty("playerUUID", playerID).addProperty("category", category).addProperty("key", key).addProperty("value", value), StatusResponse.class, "PUT");
        if (!(response instanceof StatusResponse) || !((StatusResponse) response).getStatus())
            logger.warning("cannot set player/shop (" + response + ")");
    }

    public void setEquipped(String category, String key, String value)
    {
        Object response = RestAPI.getInstance().sendRequest("player/equipped", new Request().addProperty("playerUUID", playerID).addProperty("category", category).addProperty("key", key).addProperty("value", value), StatusResponse.class, "PUT");
        if ((!(response instanceof StatusResponse) || !((StatusResponse) response).getStatus()))
            logger.warning("cannot set player/equipped (" + response + ")");
    }

    public ValueResponse getEquipped(String category, String key)
    {
        Object response = RestAPI.getInstance().sendRequest("player/equipped", new Request().addProperty("playerUUID", playerID).addProperty("category", category).addProperty("key", key), ValueResponse.class, "POST");
        if (response instanceof ValueResponse)
            return (ValueResponse) response;

        return new ValueResponse();
    }

    @Override
    public String get(String key, String def)
    {
        String result = get(key);
        return result != null ? result : def;
    }

    public void resetEquipped(String gameType, String itemCategory)
    {
        Object response = RestAPI.getInstance().sendRequest("player/equipped", new Request().addProperty("playerUUID", playerID).addProperty("category", gameType).addProperty("key", itemCategory), StatusResponse.class, "DELETE");
        if ((!(response instanceof StatusResponse) || !((StatusResponse) response).getStatus()))
            logger.warning("cannot delete player/equipped (" + response + ")");
    }


    private static final class GhostShopElement extends ShopElement {
        private List<String> ghosts = new ArrayList<>();

        @Override
        public List<String> getValue()
        {
            return ghosts;
        }
    }
}
