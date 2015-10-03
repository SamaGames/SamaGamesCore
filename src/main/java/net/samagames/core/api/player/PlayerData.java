package net.samagames.core.api.player;

import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.player.IFinancialCallback;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import org.bukkit.Bukkit;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public abstract class PlayerData extends AbstractPlayerData
{
    protected final ApiImplementation api;
    protected final PlayerDataManager manager;

    protected PlayerData(UUID playerID, ApiImplementation api, PlayerDataManager manager)
    {
        super(playerID);
        this.api = api;
        this.manager = manager;
        lastRefresh = new Date();
    }

    public void creditStars(long famount, String reason, boolean applyMultiplier, IFinancialCallback<Long> financialCallback)
    {
        APIPlugin.getInstance().getExecutor().execute(() -> {
            try
            {
                long amount = famount;
                String message = null;

                if (applyMultiplier)
                {
                    Multiplier multiplier = manager.getStarsManager().getCurrentMultiplier(playerID, ApiImplementation.get().getGameManager().getGame().getGameCodeName());
                    amount *= multiplier.getGlobalAmount();

                    message = manager.getStarsManager().getCreditMessage(amount, reason, multiplier);
                } else
                {
                    message = manager.getStarsManager().getCreditMessage(amount, reason, null);
                }

                if (Bukkit.getPlayer(playerID) != null)
                    Bukkit.getPlayer(playerID).sendMessage(message);

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
    public void creditCoins(long famount, String reason, boolean applyMultiplier, IFinancialCallback<Long> financialCallback)
    {
        APIPlugin.getInstance().getExecutor().execute(() -> {
            try
            {
                long amount = famount;
                String message = null;

                if (applyMultiplier)
                {
                    Multiplier multiplier = manager.getCoinsManager().getCurrentMultiplier(playerID, ApiImplementation.get().getGameManager().getGame().getGameCodeName());
                    amount *= multiplier.getGlobalAmount();

                    message = manager.getCoinsManager().getCreditMessage(amount, reason, multiplier);
                } else
                {
                    message = manager.getCoinsManager().getCreditMessage(amount, reason, null);
                }

                if (Bukkit.getPlayer(playerID) != null)
                    Bukkit.getPlayer(playerID).sendMessage(message);

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
    public void withdrawStars(long amount, IFinancialCallback<Long> financialCallback)
    {
        APIPlugin.getInstance().getExecutor().execute(() -> {
            long result = decreaseStars(amount);

            if (financialCallback != null)
                financialCallback.done(result, -amount, null);

        });
    }

    @Override
    public void withdrawCoins(long famount, IFinancialCallback<Long> financialCallback)
    {
        APIPlugin.getInstance().getExecutor().execute(() -> {
            long result = decreaseCoins(famount);
            if (financialCallback != null)
                financialCallback.done(result, -famount, null);

        });
    }

    @Override
    public long decreaseStars(long decrBy)
    {
        return increaseStars(-decrBy);
    }

    @Override
    public long decreaseCoins(long decrBy)
    {
        return increaseCoins(-decrBy);
    }

    @Override
    public void setInt(String key, int value)
    {
        set(key, String.valueOf(value));
    }

    @Override
    public void setBoolean(String key, boolean value)
    {
        set(key, String.valueOf(value));
    }

    @Override
    public void setDouble(String key, double value)
    {
        set(key, String.valueOf(value));
    }

    @Override
    public void setLong(String key, long value)
    {
        set(key, String.valueOf(value));
    }

    public void refreshIfNeeded()
    {
        if (lastRefresh.getTime() + 1000 * 60 < System.currentTimeMillis())
            updateData();
    }

    public abstract void updateData();

    @Override
    public String get(String key)
    {
        refreshIfNeeded();
        return super.get(key);
    }

    @Override
    public Set<String> getKeys()
    {
        refreshIfNeeded();
        return super.getKeys();
    }

    @Override
    public Map<String, String> getValues()
    {
        refreshIfNeeded();
        return super.getValues();
    }

    @Override
    public boolean contains(String key)
    {
        refreshIfNeeded();
        return super.contains(key);
    }

}
