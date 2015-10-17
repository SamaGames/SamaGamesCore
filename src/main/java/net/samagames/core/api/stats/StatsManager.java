package net.samagames.core.api.stats;

import com.google.gson.reflect.TypeToken;
import net.samagames.api.stats.AbstractStatsManager;
import net.samagames.api.stats.Leaderboard;
import net.samagames.core.ApiImplementation;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.ErrorResponse;
import net.samagames.restfull.response.elements.LeaderboradElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private Map<String, PlayerStat> caches;

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
        this.setValue(player, stat, amount);
    }

    @Override
    public void setValue(UUID player, String stat, int value)
    {
        this.setValue(player, stat, (double) value);
    }

    public void setValue(UUID player, String stat, double value)
    {
        PlayerStat stats = caches.get(player);
        if (stats == null)
            return;
        stats.setValue(value);
    }


    @Override
    public void finish()
    {
        caches.values().forEach(net.samagames.core.api.stats.PlayerStat::send);
    }

    @Override
    public double getStatValue(UUID player, String stat)
    {
        if (caches.containsKey(player.toString() + ":" + stat))
            return caches.get(player.toString() + ":" + stat).getValue();
        else
        {
            PlayerStat playerStat = new PlayerStat(player, game, stat);
            playerStat.fill();
            caches.put(player.toString() + ":" + stat, playerStat);
            return playerStat.getValue();
        }
    }

    public double getRankValue(UUID player, String stat)
    {
        if (caches.containsKey(player.toString() + ":" + stat))
            return caches.get(player.toString() + ":" + stat).getRank();
        else
        {
            PlayerStat playerStat = new PlayerStat(player, game, stat);
            playerStat.fill();
            caches.put(player.toString() + ":" + stat, playerStat);
            return playerStat.getRank();
        }
    }

    @Override
    public Leaderboard getLeaderboard(String stat)
    {
        Object response = RestAPI.getInstance().sendRequest("statistics/leaderboard", new Request().addProperty("category", game).addProperty("key", stat), new TypeToken<List<LeaderboradElement>>() {}.getType(), "POST");

        if (response instanceof List && ((List) response).size() == 3)
        {
            List<LeaderboradElement> responseList = (List<LeaderboradElement>) response;
            return new Leaderboard(new PlayerStat(game, stat).readResponse(responseList.get(0)), new PlayerStat(game, stat).readResponse(responseList.get(1)), new PlayerStat(game, stat).readResponse(responseList.get(2)));
        }
        else if (response instanceof ErrorResponse)
            logger.warning(String.format("Error during recuperation of leaderboard for category %s and key %s (response: %s)", game, stat, response.toString()));
        return null;
    }

    @Override
    public void clearCache()
    {
        this.caches.clear();
    }
}
