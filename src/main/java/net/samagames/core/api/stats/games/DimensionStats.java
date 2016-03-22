package net.samagames.core.api.stats.games;

import net.samagames.api.stats.games.IDimensionStats;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.stats.PlayerStats;
import net.samagames.core.utils.ReflectionUtils;
import net.samagames.persistanceapi.beans.statistics.DimensionStatisticsBean;


/**
 * Created by Silvanosky on 15/03/2016.
 */
public class DimensionStats extends DimensionStatisticsBean implements IDimensionStats {

    private ApiImplementation api;
    private PlayerData playerData;

    public DimensionStats(PlayerData playerData, DimensionStatisticsBean dimensionStatisticsBean) {
        super(playerData.getPlayerID(),
                dimensionStatisticsBean.getDeaths(),
                dimensionStatisticsBean.getKills(),
                dimensionStatisticsBean.getPlayedGames(),
                dimensionStatisticsBean.getWins(),
                dimensionStatisticsBean.getCreationDate(),
                dimensionStatisticsBean.getUpdateDate(),
                dimensionStatisticsBean.getPlayedTime());

        this.api = (ApiImplementation) ApiImplementation.get();
        this.playerData = playerData;
    }

    @Override
    public void update() {
        if(playerData != null)
        {
            api.getGameServiceManager().updateDimensionStatistics(playerData.getPlayerBean(), this);
        }
    }

    @Override
    public void refresh() {
        update();

        if(playerData != null)
        {
            ReflectionUtils.copySameFields(api.getGameServiceManager().getDimensionStatistics(playerData.getPlayerBean()), this);
        }
    }
}
