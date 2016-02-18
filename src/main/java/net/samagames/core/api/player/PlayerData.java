package net.samagames.core.api.player;

import net.samagames.persistanceapi.beans.PlayerBean;
import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.player.IFinancialCallback;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import org.bukkit.Bukkit;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by silvanosky
 * (C) Copyright Elydra Network 2016
 * All rights reserved.
 */
public class PlayerData extends AbstractPlayerData
{
    protected final ApiImplementation api;
    protected final PlayerDataManager manager;

    private Logger logger;

    private PlayerBean playerBean;

    private long lastRefresh;
    private UUID playerUUID;

    protected PlayerData(UUID playerID, ApiImplementation api, PlayerDataManager manager)
    {
        this.playerUUID = playerID;
        this.api = api;
        this.manager = manager;
        this.lastRefresh = System.currentTimeMillis();

        logger = api.getPlugin().getLogger();

    }

    public void updateData()
    {
        lastRefresh = System.currentTimeMillis();
        api.getGameServiceManager()
    }

    @Override
    public long getCoins()
    {
        //TODO update when needed
        return playerBean.getCoins();
    }

    @Override
    public String get(String key)
    {
        if ("stars".equalsIgnoreCase(key) && !playerData.containsKey(key))
            return Integer.toString(playerBean.getStars());
        else if ("coins".equalsIgnoreCase(key) && !playerData.containsKey(key))
            return Integer.toString(playerBean.getCoins());
        else if (key.startsWith("settings.") && !playerData.containsKey(key))
            return getSetting(key.substring(key.indexOf(".") + 1));
        else if (key.startsWith("redis."))
            return getFromRedis(key.substring(key.indexOf(".") + 1));
        else if (!playerData.containsKey(key))
            logger.warning("Can't manage get " + key);

        return super.get(key);
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

    @Override
    public void creditStars(long famount, String reason, boolean applyMultiplier, IFinancialCallback financialCallback)
    {
        APIPlugin.getInstance().getExecutor().execute(() -> {
            try
            {
                long amount = famount;
                String message = null;

                if (applyMultiplier)
                {
                    Multiplier multiplier = manager.getEconomyManager().getCurrentMultiplier(getPlayerID(), "stars", ApiImplementation.get().getGameManager().getGame().getGameCodeName());
                    amount *= multiplier.getGlobalAmount();

                    message = manager.getEconomyManager().getCreditMessage(amount, "stars", reason, multiplier);
                } else
                {
                    message = manager.getEconomyManager().getCreditMessage(amount, "stars", reason, null);
                }

                if (Bukkit.getPlayer(getPlayerID()) != null)
                    Bukkit.getPlayer(getPlayerID()).sendMessage(message);

                long result = increaseStars(amount);

                if (financialCallback != null)
                    financialCallback.done(result, amount, null);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    public String getCustomName() {
        //TODO not implemented
        return null;
    }

    @Override
    public UUID getPlayerID() {
        return playerUUID;
    }

    @Override
    public Date getLastRefresh() {
        return new Date(lastRefresh);
    }

    @Override
    public void creditCoins(long famount, String reason, boolean applyMultiplier, IFinancialCallback financialCallback)
    {
        APIPlugin.getInstance().getExecutor().execute(() -> {
            try
            {
                long amount = famount;
                String message = null;

                if (applyMultiplier)
                {
                    Multiplier multiplier = manager.getEconomyManager().getCurrentMultiplier(getPlayerID(), "coins", ApiImplementation.get().getGameManager().getGame().getGameCodeName());
                    amount *= multiplier.getGlobalAmount();

                    message = manager.getEconomyManager().getCreditMessage(amount, "coins", reason, multiplier);
                } else
                {
                    message = manager.getEconomyManager().getCreditMessage(amount, "coins", reason, null);
                }

                if (Bukkit.getPlayer(getPlayerID()) != null)
                    Bukkit.getPlayer(getPlayerID()).sendMessage(message);

                long result = increaseCoins(amount);

                if (financialCallback != null)
                    financialCallback.done(result, amount, null);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void withdrawStars(long amount, IFinancialCallback financialCallback)
    {
        APIPlugin.getInstance().getExecutor().execute(() -> {
            long result = decreaseStars(amount);

            if (financialCallback != null)
                financialCallback.done(result, -amount, null);

        });
    }

    @Override
    public long increaseStars(long incrBy) {
        //TODO update when needed
        int result = (int) (playerBean.getStars() + incrBy);
        playerBean.setStars(result);
        return result;
    }

    @Override
    public void withdrawCoins(long famount, IFinancialCallback financialCallback)
    {
        APIPlugin.getInstance().getExecutor().execute(() -> {
            long result = decreaseCoins(famount);
            if (financialCallback != null)
                financialCallback.done(result, -famount, null);

        });
    }

    @Override
    public long increaseCoins(long incrBy) {
        //TODO update when needed
        int result = (int) (playerBean.getCoins() + incrBy);
        playerBean.setCoins(result);
        return result;
    }

    @Override
    public long decreaseStars(long decrBy)
    {
        return increaseStars(-decrBy);
    }

    @Override
    public long getStars() {
        //TODO update when needed
        return playerBean.getStars();
    }

    @Override
    public long decreaseCoins(long decrBy)
    {
        return increaseCoins(-decrBy);
    }

    public void refreshIfNeeded()
    {
        if (lastRefresh + 1000 * 60 < System.currentTimeMillis())
            updateData();
    }

}
