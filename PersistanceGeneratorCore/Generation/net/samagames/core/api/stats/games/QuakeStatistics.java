package net.samagames.core.api.stats.games;

import java.lang.Override;
import net.samagames.api.stats.games.IQuakeStatistics;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.statistics.QuakeStatisticsBean;
import net.samagames.tools.TypeConverter;
import redis.clients.jedis.Jedis;

public class QuakeStatistics extends QuakeStatisticsBean implements IQuakeStatistics {
  private PlayerData playerData;

  private ApiImplementation api;

  private int KillsVector = 0;

  private int DeathsVector = 0;

  private long PlayedTimeVector = 0;

  private int PlayedGamesVector = 0;

  private int WinsVector = 0;

  public QuakeStatistics(PlayerData playerData, QuakeStatisticsBean bean) {
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

  public QuakeStatistics(PlayerData playerData) {
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
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "Deaths", DeathsVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "Wins", WinsVector);
    jedis.hset("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "UpdateDate", "" + getUpdateDate());
    jedis.hset("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "CreationDate", "" + getCreationDate());
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "PlayedTime", PlayedTimeVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "PlayedGames", PlayedGamesVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "Kills", KillsVector);
    jedis.close();
  }

  @Override
  public void refresh() {
    Jedis jedis = this.api.getBungeeResource();
    setKills(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "Kills")));
    setDeaths(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "Deaths")));
    setCreationDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "CreationDate")));
    setPlayedTime(TypeConverter.convert(long.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "PlayedTime")));
    setPlayedGames(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "PlayedGames")));
    setUpdateDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "UpdateDate")));
    setWins(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":QuakeStatisticsBean", "Wins")));
    jedis.close();
  }
}
