package net.samagames.core.api.stats.games;

import net.samagames.api.stats.games.IQuakeStats;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.utils.ReflectionUtils;
import net.samagames.persistanceapi.beans.statistics.QuakeStatisticsBean;

/**
 * Created by Silvanosky on 15/03/2016.
 */
public class QuakeStats extends QuakeStatisticsBean implements IQuakeStats {

    private ApiImplementation api;
    private PlayerData playerData;

    public QuakeStats(PlayerData playerData, QuakeStatisticsBean quakeStatisticsBean) {
        super(playerData.getPlayerID(),
                quakeStatisticsBean.getDeaths(),
                quakeStatisticsBean.getKills(),
                quakeStatisticsBean.getPlayedGames(),
                quakeStatisticsBean.getWins(),
                quakeStatisticsBean.getCreationDate(),
                quakeStatisticsBean.getUpdateDate(),
                quakeStatisticsBean.getPlayedTime());
        this.api = (ApiImplementation) ApiImplementation.get();
        this.playerData = playerData;
    }

    @Override
    public void update() {
        if(playerData != null)
        {
            api.getGameServiceManager().updateQuakeStatistics(playerData.getPlayerBean(), this);
        }
    }

    @Override
    public void refresh() {
        update();
        if(playerData != null)
        {
            ReflectionUtils.copySameFields(api.getGameServiceManager().getQuakeStatistics(playerData.getPlayerBean()), this);
        }
    }
}
