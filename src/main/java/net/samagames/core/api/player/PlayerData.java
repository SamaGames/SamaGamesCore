package net.samagames.core.api.player;

import net.samagames.persistanceapi.beans.PlayerBean;
import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.player.IFinancialCallback;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import org.bukkit.Bukkit;

import java.sql.Timestamp;
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

    private final PlayerBean defaut;

    protected PlayerData(UUID playerID, ApiImplementation api, PlayerDataManager manager)
    {
        this.playerUUID = playerID;
        this.api = api;
        this.manager = manager;

        logger = api.getPlugin().getLogger();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        //Should never be used since the player connect to bungee first
        defaut = new PlayerBean(playerID,
                api.getUUIDTranslator().getName(playerID),
                500,
                0,
                now,
                now,
                Bukkit.getPlayer(playerID).spigot().getRawAddress().getAddress().toString(),
                "",
                0);

        refreshData();
    }

    public void refreshData()
    {
        lastRefresh = System.currentTimeMillis();

        this.playerBean = api.getGameServiceManager().getPlayer(playerUUID, defaut);
    }

    public void updateData()
    {
        if(playerBean != null)
        {
            api.getGameServiceManager().updatePlayer(playerBean);
        }
    }

    @Override
    public long getCoins()
    {
        refreshIfNeeded();
        return playerBean.getCoins();
    }

    /*@Override
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
    }*/

    /*@Override
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
    }*/

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
                    String name = ApiImplementation.get().getGameManager().getGame().getGameCodeName();
                    //Todo handle game name to number
                    Multiplier multiplier = manager.getEconomyManager().getCurrentMultiplier(getPlayerID(), 2, 0);
                    amount *= multiplier.getGlobalAmount();

                    message = manager.getEconomyManager().getCreditMessage(amount, 2, reason, multiplier);
                } else
                {
                    message = manager.getEconomyManager().getCreditMessage(amount, 2, reason, null);
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
        refreshIfNeeded();
        return playerBean.getName();
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
                    String name = ApiImplementation.get().getGameManager().getGame().getGameCodeName();
                    //Todo handle game name to number
                    Multiplier multiplier = manager.getEconomyManager().getCurrentMultiplier(getPlayerID(), 1, 0);
                    amount *= multiplier.getGlobalAmount();

                    message = manager.getEconomyManager().getCreditMessage(amount, 1, reason, multiplier);
                } else
                {
                    message = manager.getEconomyManager().getCreditMessage(amount, 1, reason, null);
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
        refreshIfNeeded();

        int result = (int) (playerBean.getStars() + incrBy);
        playerBean.setStars(result);
        updateData();
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
        refreshIfNeeded();
        int result = (int) (playerBean.getCoins() + incrBy);
        playerBean.setCoins(result);
        updateData();
        return result;
    }

    @Override
    public long decreaseStars(long decrBy)
    {
        return increaseStars(-decrBy);
    }

    @Override
    public long getStars() {
        refreshIfNeeded();
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
        {
            updateData();
            refreshData();
        }
    }

    public PlayerBean getPlayerBean()
    {
        return playerBean;
    }

}
