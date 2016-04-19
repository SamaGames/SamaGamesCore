package net.samagames.core.api.stats.games;

import java.lang.Override;
import net.samagames.api.stats.games.IUppervoidStatistics;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.statistics.UppervoidStatisticsBean;
import net.samagames.tools.TypeConverter;
import redis.clients.jedis.Jedis;

public class UppervoidStatistics extends UppervoidStatisticsBean implements IUppervoidStatistics {
  private PlayerData playerData;

  private ApiImplementation api;

  private int GrenadesVector = 0;

  private int TntLaunchedVector = 0;

  private int KillsVector = 0;

  private long PlayedTimeVector = 0;

  private int BlocksVector = 0;

  private int PlayedGamesVector = 0;

  private int WinsVector = 0;

  public UppervoidStatistics(PlayerData playerData, UppervoidStatisticsBean bean) {
    super(playerData.getPlayerID()
        ,bean.getBlocks()
        ,bean.getGrenades()
        ,bean.getKills()
        ,bean.getPlayedGames()
        ,bean.getTntLaunched()
        ,bean.getWins()
        ,bean.getCreationDate()
        ,bean.getUpdateDate()
        ,bean.getPlayedTime()
        );
    this.api = (ApiImplementation) ApiImplementation.get();
    this.playerData = playerData;
  }

  public UppervoidStatistics(PlayerData playerData) {
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

  public void incrByGrenades(int arg0) {
    GrenadesVector += arg0;
  }

  public void incrByTntLaunched(int arg0) {
    TntLaunchedVector += arg0;
  }

  public void incrByKills(int arg0) {
    KillsVector += arg0;
  }

  public void incrByPlayedTime(long arg0) {
    PlayedTimeVector += arg0;
  }

  public void incrByBlocks(int arg0) {
    BlocksVector += arg0;
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
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "Wins", WinsVector);
    jedis.hset("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "UpdateDate", "" + getUpdateDate());
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "Grenades", GrenadesVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "TntLaunched", TntLaunchedVector);
    jedis.hset("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "CreationDate", "" + getCreationDate());
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "Blocks", BlocksVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "PlayedTime", PlayedTimeVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "PlayedGames", PlayedGamesVector);
    jedis.hincrBy("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "Kills", KillsVector);
    jedis.close();
  }

  @Override
  public void refresh() {
    Jedis jedis = this.api.getBungeeResource();
    setGrenades(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "Grenades")));
    setTntLaunched(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "TntLaunched")));
    setKills(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "Kills")));
    setCreationDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "CreationDate")));
    setPlayedTime(TypeConverter.convert(long.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "PlayedTime")));
    setBlocks(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "Blocks")));
    setPlayedGames(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "PlayedGames")));
    setUpdateDate(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "UpdateDate")));
    setWins(TypeConverter.convert(int.class, jedis.hget("statistic:" + playerData.getPlayerID() + ":UppervoidStatisticsBean", "Wins")));
    jedis.close();
  }
}
