package net.samagames.core.api.stats;

import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ChatColor;
import net.samagames.api.stats.IPlayerStats;
import net.samagames.api.stats.IStatsManager;
import net.samagames.api.stats.Leaderboard;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.stats.games.*;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.persistanceapi.beans.statistics.LeaderboardBean;
import net.samagames.persistanceapi.beans.statistics.PlayerStatisticsBean;

import java.util.*;
import java.util.logging.Logger;


/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2016 & 2017
 * All rights reserved.
 */
public class StatsManager implements IStatsManager
{
    private final Logger logger;
    private ApiImplementation api;
    private Map<UUID, PlayerStats> caches;

    private boolean[] statsToLoad;

    public StatsManager(ApiImplementation apiImplementation)
    {
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
        boolean global = statsToLoad[StatsNames.GLOBAL.intValue()];
        if(global || statsToLoad[StatsNames.DIMENSION.intValue()])
        {
            playerStats.setDimensionStatistics(new DimensionStatistics(playerData, api.getGameServiceManager().getDimensionStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[StatsNames.HEROBATTLE.intValue()])
        {
            playerStats.setHeroBattleStats(new HeroBattleStats(playerData, api.getGameServiceManager().getHeroBattleStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[StatsNames.JUKEBOX.intValue()])
        {
            playerStats.setJukeBoxStats(new JukeBoxStats(playerData, api.getGameServiceManager().getJukeBoxStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[StatsNames.QUAKE.intValue()])
        {
            playerStats.setQuakeStats(new QuakeStats(playerData, api.getGameServiceManager().getQuakeStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[StatsNames.UHCRUN.intValue()])
        {
            playerStats.setUhcRunStats(new UHCRunStats(playerData, api.getGameServiceManager().getUHCRunStatistics(playerData.getPlayerBean())));
        }
        if(global || statsToLoad[StatsNames.UPPERVOID.intValue()])
        {
            playerStats.setUppervoidStats(new UppervoidStats(playerData, api.getGameServiceManager().getUpperVoidStatistics(playerData.getPlayerBean())));
        }
        //Add next stats there
    }


    @Override
    public void finish()
    {
        caches.values().forEach(net.samagames.core.api.stats.PlayerStat::send);
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

    public void setStatsToLoad(StatsNames game, boolean value)
    {
        statsToLoad[game.intValue()] = value;
    }

    public boolean isStatsLoading(StatsNames game)
    {
        return statsToLoad[game.intValue()];
    }

    public Leaderboard getLeaderboard(StatsNames game, String category)
    {
        GameServiceManager gameServiceManager = api.getGameServiceManager();
        List<LeaderboardBean> list = new ArrayList<>();
        //TODO add annotation in api for simplify method
        switch (game)
        {
            case DIMENSION:
                list = gameServiceManager.getDimmensionLeaderBoard(category);
            case HEROBATTLE:
                list = gameServiceManager.getHeroBattleLeaderBoard(category);
                break;
            case JUKEBOX:
                list = gameServiceManager.getJukeBoxLeaderBoard(category);
                break;
            case QUAKE:
                list = gameServiceManager.getQuakeLeaderBoard(category);
                break;
            case UHCRUN:
                list = gameServiceManager.getUhcLeaderBoard(category);
                break;
            case UPPERVOID:
                list = gameServiceManager.getUpperVoidLeaderBoard(category);
                break;
            default:
                list = new ArrayList<>();
                break;
        }

        //TODO fill leaderboard

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
