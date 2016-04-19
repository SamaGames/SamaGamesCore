package net.samagames.core.api.stats.games;

import java.lang.Override;
import net.samagames.api.stats.games.IJukeBoxStatistics;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.statistics.JukeBoxStatisticsBean;
import net.samagames.tools.TypeConverter;
import redis.clients.jedis.Jedis;

public class JukeBoxStatistics extends JukeBoxStatisticsBean implements IJukeBoxStatistics {
  private PlayerData playerData;

  private ApiImplementation api;

  private long PlayedTimeVector = 0;

  private int MehsVector = 0;

  private int WootsVector = 0;

  public JukeBoxStatistics(PlayerData playerData, JukeBoxStatisticsBean bean) {
    super(playerData.getPlayerID()
        ,bean.getMehs()
        ,bean.getWoots()
        ,bean.getCreationDate()
        ,bean.getUpdateDate()
        ,bean.getPlayedTime()
        );
    this.api = (ApiImplementation) ApiImplementation.get();
    this.playerData = playerData;
  }

  public JukeBoxStatistics(PlayerData playerData) {
    super(playerData.getPlayerID()
        ,0
        ,0
        , null
        , null
        ,0
        );
    this.api = (ApiImplementation) ApiImplementation.get();
    this.playerData = playerData;
  }

  public void incrByPlayedTime(long arg0) {
    PlayedTimeVector += arg0;
  }

  public void incrByMehs(int arg0) {
    MehsVector += arg0;
  }

  public void incrByWoots(int arg0) {
    WootsVector += arg0;
  }

  @Override
  public void update() {
    Jedis jedis = this.api.getBungeeResource();
    jedis.hset("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "UpdateDate", "" + getUpdateDate());
    jedis.hset("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "CreationDate", "" + getCreationDate());
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "PlayedTime", PlayedTimeVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "Mehs", MehsVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "Woots", WootsVector);
    jedis.close();
  }

  @Override
  public void refresh() {
    Jedis jedis = this.api.getBungeeResource();
    setCreationDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "CreationDate")));
    setPlayedTime(TypeConverter.convert(long.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "PlayedTime")));
    setMehs(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "Mehs")));
    setUpdateDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "UpdateDate")));
    setWoots(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":JukeBoxStatisticsBean", "Woots")));
    jedis.close();
  }
}
