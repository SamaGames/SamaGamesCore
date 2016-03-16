package net.samagames.core.api.stats.games;

import net.samagames.api.stats.games.IJukeBoxStats;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.stats.PlayerStats;
import net.samagames.core.utils.ReflectionUtils;
import net.samagames.persistanceapi.beans.statistics.JukeBoxStatisticsBean;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by Silvanosky on 15/03/2016.
 */
public class JukeBoxStats extends JukeBoxStatisticsBean implements IJukeBoxStats{

    private ApiImplementation api;
    private PlayerData playerData;

    public JukeBoxStats(PlayerData playerData, JukeBoxStatisticsBean jukeBoxStatisticsBean) {
        super(playerData.getPlayerID(),
                jukeBoxStatisticsBean.getMehs(),
                jukeBoxStatisticsBean.getWoots(),
                jukeBoxStatisticsBean.getCreationDate(),
                jukeBoxStatisticsBean.getUpdateDate(),
                jukeBoxStatisticsBean.getPlayedTime());
        this.api = (ApiImplementation) ApiImplementation.get();
        this.playerData = playerData;
    }

    @Override
    public void update() {
        if(playerData != null)
        {
            api.getGameServiceManager().updateJukeBoxStatistics(playerData.getPlayerBean(), this);
        }
    }

    @Override
    public void refresh() {
        update();
        if(playerData != null)
        {
            ReflectionUtils.copySameFields(api.getGameServiceManager().getJukeBoxStatistics(playerData.getPlayerBean()), this);
        }
    }
}
