package net.samagames.core.api.player;

import com.google.gson.Gson;
import net.md_5.bungee.api.chat.TextComponent;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.player.IFinancialCallback;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.core.utils.CacheLoader;
import net.samagames.persistanceapi.beans.players.PlayerBean;
import net.samagames.persistanceapi.beans.players.SanctionBean;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Silvanosky
 * (C) Copyright Elydra Network 2016
 * All rights reserved.
 */
public class PlayerData extends AbstractPlayerData
{
    protected final ApiImplementation api;
    protected final PlayerDataManager manager;

    private PlayerBean playerBean;

    private long lastRefresh;
    private UUID playerUUID;

    private final static String key = "playerdata:";

    private SanctionBean muteSanction = null;

    protected PlayerData(UUID playerID, ApiImplementation api, PlayerDataManager manager)
    {
        this.playerUUID = playerID;
        this.api = api;
        this.manager = manager;

        refreshData();
    }

    //Warning load all data soi may be heavy
    public boolean refreshData()
    {
        lastRefresh = System.currentTimeMillis();
        //Load from redis

        Jedis jedis = api.getBungeeResource();
        try{
            CacheLoader.load(jedis, key + playerUUID, playerBean);
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
            //Generated class so FUCK IT i made it static
            CacheLoader.send(jedis, key + playerUUID, playerBean);
            jedis.close();
        }
    }

    public SanctionBean getMuteSanction()
    {
        return muteSanction;
    }

    @Override
    public void creditCoins(long amount, String reason, boolean applyMultiplier, IFinancialCallback financialCallback)
    {
        creditEconomy(1, amount, reason, applyMultiplier, financialCallback);
    }

    @Override
    public void creditStars(long amount, String reason, boolean applyMultiplier, IFinancialCallback financialCallback)
    {
        creditEconomy(2, amount, reason, applyMultiplier, financialCallback);
    }

    private void creditEconomy(int type, long amountFinal, String reason, boolean applyMultiplier, IFinancialCallback financialCallback)
    {
        int game = 0;
        APIPlugin.getInstance().getExecutor().execute(() -> {
            try
            {
                long amount = amountFinal;
                String message = null;

                if (applyMultiplier)
                {
                    String name = ApiImplementation.get().getGameManager().getGame().getGameCodeName();
                    //Todo handle game name to number need the satch enum
                    Multiplier multiplier = manager.getEconomyManager().getCurrentMultiplier(getPlayerID(), type, game);
                    amount *= multiplier.getGlobalAmount();

                    message = manager.getEconomyManager().getCreditMessage(amount, type, reason, multiplier);
                } else
                {
                    message = manager.getEconomyManager().getCreditMessage(amount, type, reason, null);
                }

                if (Bukkit.getPlayer(getPlayerID()) != null)
                    Bukkit.getPlayer(getPlayerID()).sendMessage(message);

                //edit here for more type of coins
                long result = (type == 2 ) ? increaseStars(amount) : increaseCoins(amount);

                if (financialCallback != null)
                    financialCallback.done(result, amount, null);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void withdrawCoins(long amount, IFinancialCallback financialCallback)
    {
        APIPlugin.getInstance().getExecutor().execute(() -> {
            long result = decreaseCoins(amount);
            if (financialCallback != null)
                financialCallback.done(result, -amount, null);

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
    public long increaseCoins(long incrBy) {
        refreshIfNeeded();
        int result = (int) (playerBean.getCoins() + incrBy);
        playerBean.setCoins(result);
        updateData();
        return result;
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
    public long decreaseCoins(long decrBy)
    {
        return increaseCoins(-decrBy);
    }

    @Override
    public long decreaseStars(long decrBy)
    {
        return increaseStars(-decrBy);
    }

    @Override
    public long getCoins()
    {
        refreshIfNeeded();
        return playerBean.getCoins();
    }

    @Override
    public long getStars() {
        refreshIfNeeded();
        return playerBean.getStars();
    }

    @Override
    public String getCustomName()
    {
        return playerBean.getNickName();
    }

    @Override
    public String getEffectiveName() {
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


    /**
     *  Need to be call before edit data
     */
    public void refreshIfNeeded()
    {
        /*if (lastRefresh + 1000 * 60 < System.currentTimeMillis())
        {*/
        //I don't want to loose data so refresh every time before change
            refreshData();
        //}
    }

    public PlayerBean getPlayerBean()
    {
        return playerBean;
    }

    @Override
    public void kickFromNetwork(TextComponent reason)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.kick", playerUUID + " " + new Gson().toJson(reason));
    }

    @Override
    public void connectToServer(String server)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.connect", playerUUID + " " + server);
    }

    @Override
    public void sendMessage(TextComponent component)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.message", playerUUID + " " + new Gson().toJson(component));
    }

}
