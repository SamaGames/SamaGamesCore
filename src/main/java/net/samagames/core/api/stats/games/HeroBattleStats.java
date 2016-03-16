package net.samagames.core.api.stats.games;

import net.samagames.api.stats.games.IHeroBattleStats;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.utils.ReflectionUtils;
import net.samagames.persistanceapi.beans.statistics.HeroBattleStatisticsBean;


/**
 * Created by Silvanosky on 15/03/2016.
 */
public class HeroBattleStats extends HeroBattleStatisticsBean implements IHeroBattleStats {

    private ApiImplementation api;
    private PlayerData playerData;

    public HeroBattleStats(PlayerData playerData, HeroBattleStatisticsBean heroBattleStatisticsBean) {
        super(playerData.getPlayerID(),
                heroBattleStatisticsBean.getDeaths(),
                heroBattleStatisticsBean.getElo(),
                heroBattleStatisticsBean.getKills(),
                heroBattleStatisticsBean.getPlayedGames(),
                heroBattleStatisticsBean.getPowerUpTaken(),
                heroBattleStatisticsBean.getWins(),
                heroBattleStatisticsBean.getCreationDate(),
                heroBattleStatisticsBean.getUpdateDate(),
                heroBattleStatisticsBean.getPlayedTime());

        this.api = (ApiImplementation) ApiImplementation.get();
        this.playerData = playerData;
    }

    @Override
    public void update() {
        if(playerData != null)
        {
            api.getGameServiceManager().updateHeroBattleStatistics(playerData.getPlayerBean(), this);
        }
    }

    @Override
    public void refresh() {
        update();

        if(playerData != null)
        {
            ReflectionUtils.copySameFields(api.getGameServiceManager().getHeroBattleStatistics(playerData.getPlayerBean()), this);
        }
    }
}
