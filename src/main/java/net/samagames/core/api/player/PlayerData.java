package net.samagames.core.api.player;

import net.samagames.core.utils.ReflectionUtils;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.persistanceapi.beans.PlayerBean;
import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.player.IFinancialCallback;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.persistanceapi.beans.SanctionBean;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.lang.reflect.Field;
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

    private final GameServiceManager gameServiceManager;

    private final static String key = "playerdata:";

    private SanctionBean muteSanction = null;

    protected PlayerData(UUID playerID, ApiImplementation api, PlayerDataManager manager)
    {
        this.playerUUID = playerID;
        this.api = api;
        this.manager = manager;
        this.gameServiceManager = api.getGameServiceManager();

        logger = api.getPlugin().getLogger();

        refreshData();
    }

    public boolean refreshData()
    {
        lastRefresh = System.currentTimeMillis();
        //Load from redis

        Jedis jedis = api.getBungeeResource();
        try{
            if (jedis.exists(key + playerUUID))
            {
                ReflectionUtils.deserialiseFromRedis(jedis, key + playerUUID, playerBean);
                if (jedis.exists("mute:" + playerUUID))
                {
                    muteSanction = new SanctionBean(playerUUID,
                            SanctionBean.MUTE,
                            jedis.hget("mute:" + playerUUID, "reason"),
                            UUID.fromString(jedis.hget("mute:" + playerUUID, "by")),
                            new Timestamp(Long.valueOf(jedis.hget("mute:" + playerUUID, "expireAt"))),
                            false, null, null);
                }
                return true;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return false;
    }

    public void updateData()
    {
        if(playerBean != null)
        {
            //Save in redis
            Jedis jedis = api.getBungeeResource();
            Pipeline pipeline = jedis.pipelined();
            Field[] declaredFields = PlayerBean.class.getDeclaredFields();
            for (Field field : declaredFields)
            {
                field.setAccessible(true);
                try {
                    pipeline.hset(key + playerUUID, field.getName(), field.get(playerBean).toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            pipeline.exec();
            pipeline.discard();
            jedis.close();
        }
    }

    public SanctionBean getMuteSanction()
    {
        return muteSanction;
    }

    @Override
    public long getCoins()
    {
        refreshIfNeeded();
        return playerBean.getCoins();
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
                    String name = ApiImplementation.get().getGameManager().getGame().getGameCodeName();
                    //Todo handle game name to number need the satch enum
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

    /**
     *  Need to be call before edit data
     */
    public void refreshIfNeeded()
    {
        if (lastRefresh + 1000 * 60 < System.currentTimeMillis())
        {
            refreshData();
        }
    }

    public PlayerBean getPlayerBean()
    {
        return playerBean;
    }

}
