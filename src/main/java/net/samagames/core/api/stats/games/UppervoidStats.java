package net.samagames.core.api.stats.games;

import net.samagames.api.stats.games.IUppervoidStats;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.utils.ReflectionUtils;
import net.samagames.persistanceapi.beans.statistics.UppervoidStatisticsBean;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by Silvanosky on 15/03/2016.
 */
public class UppervoidStats extends UppervoidStatisticsBean implements IUppervoidStats {

    private ApiImplementation api;
    private PlayerData playerData;

    public UppervoidStats(PlayerData playerData, UppervoidStatisticsBean uppervoidStatisticsBean) {
        super(playerData.getPlayerID(),
                uppervoidStatisticsBean.getBlocks(),
                uppervoidStatisticsBean.getGrenades(),
                uppervoidStatisticsBean.getKills(),
                uppervoidStatisticsBean.getPlayedGames(),
                uppervoidStatisticsBean.getTntLaunched(),
                uppervoidStatisticsBean.getWins(),
                uppervoidStatisticsBean.getCreationDate(),
                uppervoidStatisticsBean.getUpdateDate(),
                uppervoidStatisticsBean.getPlayedTime());

        this.api = (ApiImplementation) ApiImplementation.get();
        this.playerData = playerData;
    }

    @Override
    public void update() {
        if(playerData != null)
        {
            api.getGameServiceManager().updateUpperVoidStatistics(playerData.getPlayerBean(), this);
        }
    }

    @Override
    public void refresh() {
        update();
        if(playerData != null)
        {
            ReflectionUtils.copySameFields(api.getGameServiceManager().getUpperVoidStatistics(playerData.getPlayerBean()), this);
        }
    }
}
