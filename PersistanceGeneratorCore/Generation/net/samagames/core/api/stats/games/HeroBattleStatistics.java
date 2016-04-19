package net.samagames.core.api.stats.games;

import java.lang.Override;
import net.samagames.api.stats.games.IHeroBattleStatistics;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.statistics.HeroBattleStatisticsBean;
import net.samagames.tools.TypeConverter;
import redis.clients.jedis.Jedis;

public class HeroBattleStatistics extends HeroBattleStatisticsBean implements IHeroBattleStatistics {
  private PlayerData playerData;

  private ApiImplementation api;

  private int KillsVector = 0;

  private int DeathsVector = 0;

  private int EloVector = 0;

  private long PlayedTimeVector = 0;

  private int PlayedGamesVector = 0;

  private int WinsVector = 0;

  private int PowerUpTakenVector = 0;

  public HeroBattleStatistics(PlayerData playerData, HeroBattleStatisticsBean bean) {
    super(playerData.getPlayerID()
        ,bean.getDeaths()
        ,bean.getElo()
        ,bean.getKills()
        ,bean.getPlayedGames()
        ,bean.getPowerUpTaken()
        ,bean.getWins()
        ,bean.getCreationDate()
        ,bean.getUpdateDate()
        ,bean.getPlayedTime()
        );
    this.api = (ApiImplementation) ApiImplementation.get();
    this.playerData = playerData;
  }

  public HeroBattleStatistics(PlayerData playerData) {
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

  public void incrByKills(int arg0) {
    KillsVector += arg0;
  }

  public void incrByDeaths(int arg0) {
    DeathsVector += arg0;
  }

  public void incrByElo(int arg0) {
    EloVector += arg0;
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

  public void incrByPowerUpTaken(int arg0) {
    PowerUpTakenVector += arg0;
  }

  @Override
  public void update() {
    Jedis jedis = this.api.getBungeeResource();
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "Elo", EloVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "Deaths", DeathsVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "Wins", WinsVector);
    jedis.hset("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "UpdateDate", "" + getUpdateDate());
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "PowerUpTaken", PowerUpTakenVector);
    jedis.hset("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "CreationDate", "" + getCreationDate());
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "PlayedTime", PlayedTimeVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "PlayedGames", PlayedGamesVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "Kills", KillsVector);
    jedis.close();
  }

  @Override
  public void refresh() {
    Jedis jedis = this.api.getBungeeResource();
    setKills(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "Kills")));
    setDeaths(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "Deaths")));
    setElo(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "Elo")));
    setCreationDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "CreationDate")));
    setPlayedTime(TypeConverter.convert(long.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "PlayedTime")));
    setPlayedGames(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "PlayedGames")));
    setUpdateDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "UpdateDate")));
    setWins(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "Wins")));
    setPowerUpTaken(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":HeroBattleStatisticsBean", "PowerUpTaken")));
    jedis.close();
  }
}
