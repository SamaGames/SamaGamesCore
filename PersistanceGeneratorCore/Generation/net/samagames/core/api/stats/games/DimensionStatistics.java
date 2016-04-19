package net.samagames.core.api.stats.games;

import java.lang.Override;
import net.samagames.api.stats.games.IDimensionStatistics;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.statistics.DimensionStatisticsBean;
import net.samagames.tools.TypeConverter;
import redis.clients.jedis.Jedis;

public class DimensionStatistics extends DimensionStatisticsBean implements IDimensionStatistics {
  private PlayerData playerData;

  private ApiImplementation api;

  private int KillsVector = 0;

  private int DeathsVector = 0;

  private long PlayedTimeVector = 0;

  private int PlayedGamesVector = 0;

  private int WinsVector = 0;

  public DimensionStatistics(PlayerData playerData, DimensionStatisticsBean bean) {
    super(playerData.getPlayerID()
        ,bean.getDeaths()
        ,bean.getKills()
        ,bean.getPlayedGames()
        ,bean.getWins()
        ,bean.getCreationDate()
        ,bean.getUpdateDate()
        ,bean.getPlayedTime()
        );
    this.api = (ApiImplementation) ApiImplementation.get();
    this.playerData = playerData;
  }

  public DimensionStatistics(PlayerData playerData) {
    super(playerData.getPlayerID()
        ,0
        ,0
        ,0
        ,0
        , null
        , null
        ,0
        );
    this.api = (ApiImplementation) ApiImplementation.get();
    this.playerData = playerData;
  }

  public void incrByKills(int arg0) {
    KillsVector += arg0;
  }

  public void incrByDeaths(int arg0) {
    DeathsVector += arg0;
  }

  public void incrByPlayedTime(long arg0) {
    PlayedTimeVector += arg0;
  }

  public void incrByPlayedGames(int arg0) {
    PlayedGamesVector += arg0;
  }

  public void incrByWins(int arg0) {
    WinsVector += arg0;
  }

  @Override
  public void update() {
    Jedis jedis = this.api.getBungeeResource();
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "Deaths", DeathsVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "Wins", WinsVector);
    jedis.hset("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "UpdateDate", "" + getUpdateDate());
    jedis.hset("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "CreationDate", "" + getCreationDate());
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "PlayedTime", PlayedTimeVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "PlayedGames", PlayedGamesVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "Kills", KillsVector);
    jedis.close();
  }

  @Override
  public void refresh() {
    Jedis jedis = this.api.getBungeeResource();
    setKills(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "Kills")));
    setDeaths(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "Deaths")));
    setCreationDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "CreationDate")));
    setPlayedTime(TypeConverter.convert(long.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "PlayedTime")));
    setPlayedGames(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "PlayedGames")));
    setUpdateDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "UpdateDate")));
    setWins(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":DimensionStatisticsBean", "Wins")));
    jedis.close();
  }
}
