package net.samagames.core.api.stats;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.stats.AbstractStatsManager;
import net.samagames.api.stats.IPlayerStat;
import net.samagames.api.stats.Leaderboard;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.Response;
import net.samagames.restfull.response.StatusResponse;
import net.samagames.restfull.response.ValueResponse;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.logging.Logger;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class StatsManager extends AbstractStatsManager
{
    private final Logger logger;
    private ApiImplementation api;
    private Map<UUID, PlayerStat> caches;
    public StatsManager(String game, ApiImplementation apiImplementation)
    {
        super(game);
        this.api = apiImplementation;
        this.caches = new HashMap<>();
        logger = api.getPlugin().getLogger();
    }

    @Override
    public void increase(final UUID player, final String stat, final int amount)
    {
        if (api.useRestFull())
        {
            this.setValue(player, stat, getStatValue(player, stat) + amount);
        }
        else
        {
            Bukkit.getScheduler().runTaskAsynchronously(APIPlugin.getInstance(), () -> {
                Jedis j = SamaGamesAPI.get().getResource();
                j.zincrby("gamestats:" + game + ":" + stat, amount, player.toString());
                j.close();
            });
        }

    }

    @Override
    public void setValue(UUID player, String stat, int value)
    {
        this.setValue(player, stat, (double)value);
    }

    public void setValue(UUID player, String stat, double value)
    {
        if (api.useRestFull())
        {
            Response response = (Response) RestAPI.getInstance().sendRequest("player/statistic", new Request().addProperty("playerUUID", player).addProperty("category", game).addProperty("key", stat).addProperty("value", value), StatusResponse.class, "PUT");
            boolean isErrored = true;
            if (response instanceof StatusResponse)
                isErrored = !((StatusResponse) response).getStatus();

            if (isErrored)
                logger.warning("Cannot set key " + stat + " with value " + value + "for uuid " + player + " (DEBUG: " + response + ")");
        }
        else
        {
            Bukkit.getScheduler().runTaskAsynchronously(APIPlugin.getInstance(), () -> {
                Jedis j = SamaGamesAPI.get().getResource();
                j.zadd("gamestats:" + game + ":" + stat, value, player.toString());
                j.close();
            });
        }
    }

    @Override
    public double getStatValue(UUID player, String stat)
    {
        if (api.useRestFull())
        {
            if (caches.containsKey(player))
                return caches.get(player).getValue();
            else
            {
                PlayerStat playerStat = new PlayerStat(player, game, stat);
                playerStat.fill();
                caches.put(player, playerStat);
                return playerStat.getValue();
            }
        }
        else
        {
            Jedis j = SamaGamesAPI.get().getResource();
            double value = j.zscore("gamestats:" + game + ":" + stat, player.toString());
            j.close();
            return value;
        }
    }

    @Override
    public Leaderboard getLeaderboard(String stat)
    {
        if (api.useRestFull())
        {
            // TODO: Leaderboard for the RestfullAPI
            return null;
        }
        else
        {
            ArrayList<IPlayerStat> leaderboard = new ArrayList<>();
            Jedis jedis = SamaGamesAPI.get().getResource();
            Set<String> ids = jedis.zrevrange("gamestats:" + game + ":" + stat, 0, 2);
            jedis.close();

            for (String id : ids)
            {
                IPlayerStat playerStat = new PlayerStat(UUID.fromString(id), this.game, stat);
                playerStat.fill();

                leaderboard.add(playerStat);
            }

            return new Leaderboard(leaderboard.get(0), leaderboard.get(1), leaderboard.get(2));
        }
    }
}
