package net.samagames.core.api.stats;

import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ChatColor;
import net.samagames.api.stats.IPlayerStats;
import net.samagames.api.stats.IStatsManager;
import net.samagames.api.stats.Leaderboard;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.stats.games.*;
import net.samagames.persistanceapi.beans.statistics.PlayerStatisticsBean;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.ErrorResponse;
import net.samagames.restfull.response.elements.LeaderboradElement;

import java.util.*;
import java.util.logging.Logger;

import static net.samagames.core.api.stats.StatsManager.StatsNames.*;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class StatsManager implements IStatsManager
{
    private final Logger logger;
    private final String game;
    private ApiImplementation api;
    private Map<UUID, PlayerStats> caches;

    private boolean[] statsToLoad;

    public enum StatsNames{
        GLOBAL(0),
        DIMENSION(1),
        HEROBATTLE(2),
        JUKEBOX(3),
        QUAKE(4),
        UHCRUN(5),
        UPPERVOID(6);

        private int value;
        StatsNames(int value)
        {
            this.value = value;
        }

        int intValue()
        {
            return value;
        }
    }

    public StatsManager(String game, ApiImplementation apiImplementation)
    {
        this.game = game;
        this.api = apiImplementation;
        this.caches = new HashMap<>();
        this.statsToLoad = new boolean[StatsNames.values().length];
        for (int i = 0; i < statsToLoad.length; i++)
        {
            statsToLoad[i] = false;
        }

        logger = api.getPlugin().getLogger();
    }

    //TODO load at join
    public void loadPlayer(UUID player)
    {
        PlayerData playerData = (PlayerData) api.getPlayerManager().getPlayerData(player);
        PlayerStats playerStats = new PlayerStats(api, player);
        boolean global = statsToLoad[GLOBAL.intValue()];
        if(global || statsToLoad[DIMENSION.intValue()])
        {
            playerStats.setDimensionStats(new DimensionStats(playerData, api.getGameServiceManager().getDimensionStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[HEROBATTLE.intValue()])
        {
            playerStats.setHeroBattleStats(new HeroBattleStats(playerData, api.getGameServiceManager().getHeroBattleStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[JUKEBOX.intValue()])
        {
            playerStats.setJukeBoxStats(new JukeBoxStats(playerData, api.getGameServiceManager().getJukeBoxStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[QUAKE.intValue()])
        {
            playerStats.setQuakeStats(new QuakeStats(playerData, api.getGameServiceManager().getQuakeStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[UHCRUN.intValue()])
        {
            playerStats.setUhcRunStats(new UHCRunStats(playerData, api.getGameServiceManager().getUHCRunStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[UPPERVOID.intValue()])
        {
            playerStats.setUppervoidStats(new UppervoidStats(playerData, api.getGameServiceManager().getUpperVoidStatistics(playerData.getPlayerBean())));
        }
        //Add next stats there
    }

    //TODO unload at leave
    public void unloadPlayer(UUID player)
    {
        PlayerStats playerStats = caches.get(player);
        if(playerStats != null)
        {
            playerStats.updateStats();
            caches.remove(player);
        }
    }

    @Override
    public void finish()
    {
        caches.values().forEach(PlayerStats::updateStats);
    }

    @Override
    public Leaderboard getLeaderboard(String stat)
    {
        Object response = RestAPI.getInstance().sendRequest("statistics/leaderboard", new Request().addProperty("category", game).addProperty("key", stat), new TypeToken<List<LeaderboradElement>>() {}.getType(), "POST");

        if (response instanceof List && ((List) response).size() == 3)
        {
            List<LeaderboradElement> responseList = (List<LeaderboradElement>) response;
            return new Leaderboard(new PlayerStats(game, stat).readResponse(responseList.get(0)), new PlayerStats(game, stat).readResponse(responseList.get(1)), new PlayerStats(game, stat).readResponse(responseList.get(2)));
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

    @Override
    public IPlayerStats getPlayerStats(UUID player) {
        return caches.get(player);
    }
}
