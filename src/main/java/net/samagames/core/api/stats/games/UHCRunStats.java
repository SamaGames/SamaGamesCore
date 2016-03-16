package net.samagames.core.api.stats.games;

import net.samagames.api.stats.games.IUHCRunStats;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.utils.ReflectionUtils;
import net.samagames.persistanceapi.beans.statistics.UHCRunStatisticsBean;

/**
 * Created by Silvanosky on 15/03/2016.
 */
public class UHCRunStats extends UHCRunStatisticsBean implements IUHCRunStats {

    private ApiImplementation api;
    private PlayerData playerData;

    public UHCRunStats(PlayerData playerData, UHCRunStatisticsBean uhcRunStatisticsBean) {
        super(playerData.getPlayerID(),
                uhcRunStatisticsBean.getDamages(),
                uhcRunStatisticsBean.getDeaths(),
                uhcRunStatisticsBean.getKills(),
                uhcRunStatisticsBean.getMaxDamages(),
                uhcRunStatisticsBean.getPlayedGames(),
                uhcRunStatisticsBean.getWins(),
                uhcRunStatisticsBean.getCreationDate(),
                uhcRunStatisticsBean.getUpdateDate(),
                uhcRunStatisticsBean.getPlayedTime());
        this.api = (ApiImplementation) ApiImplementation.get();
        this.playerData = playerData;
    }

    @Override
    public void update() {
        if(playerData != null)
        {
            api.getGameServiceManager().updateUHCRunStatistics(playerData.getPlayerBean(), this);
        }
    }

    @Override
    public void refresh() {
        update();
        if(playerData != null)
        {
            ReflectionUtils.copySameFields(api.getGameServiceManager().getUHCRunStatistics(playerData.getPlayerBean()), this);
        }
    }
}
