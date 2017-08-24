package net.samagames.core.api.stats;

import net.samagames.api.games.GamesNames;
import net.samagames.api.stats.IStatsManager;
import net.samagames.api.stats.Leaderboard;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.persistanceapi.beans.statistics.LeaderboardBean;

import java.util.*;

/*
 * This file is part of SamaGamesCore.
 *
 * SamaGamesCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesCore.  If not, see <http://www.gnu.org/licenses/>.
 */
public class StatsManager implements IStatsManager
{
    private ApiImplementation api;
    private Map<UUID, PlayerStats> caches;

    private boolean[] statsToLoad;

    public StatsManager(ApiImplementation apiImplementation)
    {
        this.api = apiImplementation;
        this.caches = new HashMap<>();
        this.statsToLoad = new boolean[GamesNames.values().length];
        for (int i = 0; i < statsToLoad.length; i++)
        {
            statsToLoad[i] = api.getPlugin().isHub();
        }
    }

    public void loadPlayer(UUID player)
    {
        try{
            PlayerData playerData = api.getPlayerManager().getPlayerData(player);
            PlayerStats playerStats = new PlayerStats(api, playerData, statsToLoad);
            playerStats.refreshStats();
            caches.put(player, playerStats);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

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

    public void setStatsToLoad(GamesNames game, boolean value)
    {
        statsToLoad[game.intValue()] = value;
    }

    public boolean isStatsLoading(GamesNames game)
    {
        return statsToLoad[game.intValue()];
    }

    public Leaderboard getLeaderboard(GamesNames game, String category)
    {
        GameServiceManager gameServiceManager = api.getGameServiceManager();
        List<LeaderboardBean> list = new ArrayList<>();
        //TODO add annotation in api for simplify method

        try {
            switch (game) {
                case DIMENSION:
                    list = gameServiceManager.getDimensionsLeaderBoard(category);
                    break;
                case JUKEBOX:
                    list = gameServiceManager.getJukeBoxLeaderBoard(category);
                    break;
                case QUAKE:
                    list = gameServiceManager.getQuakeLeaderBoard(category);
                    break;
                case UHCORIGINAL:
                    list = gameServiceManager.getUHCOriginalLeaderBoard(category);
                    break;
                case UHCRUN:
                    list = gameServiceManager.getUHCRunLeaderBoard(category);
                    break;
                case DOUBLERUNNER:
                    list = gameServiceManager.getDoubleRunnerLeaderBoard(category);
                    break;
                case UHCRANDOM:
                    list = gameServiceManager.getUHCRandomLeaderBoard(category);
                    break;
                case RANDOMRUN:
                    list = gameServiceManager.getRandomRunLeaderBoard(category);
                    break;
                case ULTRAFLAGKEEPER:
                    list = gameServiceManager.getUltraFlagKeeperLeaderBoard(category);
                    break;
                case UPPERVOID:
                    list = gameServiceManager.getUppervoidLeaderBoard(category);
                    break;
                case CHUNKWARS:
                    list = gameServiceManager.getChunkWarsLeaderBoard(category);
                    break;
                case THEDROPPER:
                    list = gameServiceManager.getTheDropperLeaderBoard(category);
                    break;
                default:
                    list = new ArrayList<>();
                    break;
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return new Leaderboard(
                list.size() > 0 ? new Leaderboard.PlayerStatData(list.get(0).getName(), list.get(0).getScore()) : null,
                list.size() > 1 ? new Leaderboard.PlayerStatData(list.get(1).getName(), list.get(1).getScore()) : null,
                list.size() > 2 ? new Leaderboard.PlayerStatData(list.get(2).getName(), list.get(2).getScore()) : null
                );
    }

    @Override
    public void clearCache()
    {
        this.caches.clear();
    }

    @Override
    public PlayerStats getPlayerStats(UUID player) {
        return caches.get(player);
    }
}
