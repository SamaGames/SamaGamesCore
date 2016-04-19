package net.samagames.core.api.stats.games;

import java.lang.Override;
import net.samagames.api.stats.games.IUHCRunStatistics;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.statistics.UHCRunStatisticsBean;
import net.samagames.tools.TypeConverter;
import redis.clients.jedis.Jedis;

public class UHCRunStatistics extends UHCRunStatisticsBean implements IUHCRunStatistics {
  private PlayerData playerData;

  private ApiImplementation api;

  private int DamagesVector = 0;

  private int KillsVector = 0;

  private int DeathsVector = 0;

  private long PlayedTimeVector = 0;

  private int MaxDamagesVector = 0;

  private int PlayedGamesVector = 0;

  private int WinsVector = 0;

  public UHCRunStatistics(PlayerData playerData, UHCRunStatisticsBean bean) {
    super(playerData.getPlayerID()
        ,bean.getDamages()
        ,bean.getDeaths()
        ,bean.getKills()
        ,bean.getMaxDamages()
        ,bean.getPlayedGames()
        ,bean.getWins()
        ,bean.getCreationDate()
        ,bean.getUpdateDate()
        ,bean.getPlayedTime()
        );
    this.api = (ApiImplementation) ApiImplementation.get();
    this.playerData = playerData;
  }

  public UHCRunStatistics(PlayerData playerData) {
    super(playerData.getPlayerID()
        ,0
        ,0
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

  public void incrByDamages(int arg0) {
    DamagesVector += arg0;
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

  public void incrByMaxDamages(int arg0) {
    MaxDamagesVector += arg0;
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
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "Deaths", DeathsVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "Wins", WinsVector);
    jedis.hset("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "UpdateDate", "" + getUpdateDate());
    jedis.hset("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "CreationDate", "" + getCreationDate());
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "PlayedTime", PlayedTimeVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "PlayedGames", PlayedGamesVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "Damages", DamagesVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "Kills", KillsVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "MaxDamages", MaxDamagesVector);
    jedis.close();
  }

  @Override
  public void refresh() {
    Jedis jedis = this.api.getBungeeResource();
    setDamages(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "Damages")));
    setKills(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "Kills")));
    setDeaths(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "Deaths")));
    setCreationDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "CreationDate")));
    setPlayedTime(TypeConverter.convert(long.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "PlayedTime")));
    setMaxDamages(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "MaxDamages")));
    setPlayedGames(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "PlayedGames")));
    setUpdateDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "UpdateDate")));
    setWins(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UHCRunStatisticsBean", "Wins")));
    jedis.close();
  }
}
