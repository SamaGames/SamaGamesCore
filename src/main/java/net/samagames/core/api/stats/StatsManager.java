package net.samagames.core.api.stats;

import net.samagames.api.stats.AbstractStatsManager;
import net.samagames.api.stats.Leaderboard;
import net.samagames.core.ApiImplementation;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.Response;
import net.samagames.restfull.response.StatusResponse;

import java.util.HashMap;
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
        this.setValue(player, stat, getStatValue(player, stat) + amount);
    }

    @Override
    public void setValue(UUID player, String stat, int value)
    {
        this.setValue(player, stat, (double) value);
    }

    public void setValue(UUID player, String stat, double value)
    {
        Object response = RestAPI.getInstance().sendRequest("player/statistic", new Request().addProperty("playerUUID", player).addProperty("category", game).addProperty("key", stat).addProperty("value", value), StatusResponse.class, "PUT");
        boolean isErrored = true;
        if (response instanceof StatusResponse)
            isErrored = !((StatusResponse) response).getStatus();

        if (isErrored)
            logger.warning("Cannot set key " + stat + " with value " + value + "for uuid " + player + " (DEBUG: " + response + ")");
    }

    @Override
    public double getStatValue(UUID player, String stat)
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

    @Override
    public Leaderboard getLeaderboard(String stat)
    {
        // TODO: Leaderboard for the RestfullAPI
        return null;
    }
}
