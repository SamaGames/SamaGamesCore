package net.samagames.core.utils;

import java.lang.String;
import net.samagames.persistanceapi.beans.players.GroupsBean;
import net.samagames.persistanceapi.beans.players.PlayerBean;
import net.samagames.tools.TypeConverter;
import redis.clients.jedis.Jedis;

public class CacheLoader {
  public static void load(Jedis jedis, String key, GroupsBean objet) {
    objet.setName(TypeConverter.convert(java.lang.String.class, jedis.hget(key, "Name")));
    objet.setMultiplier(TypeConverter.convert(int.class, jedis.hget(key, "Multiplier")));
    objet.setPrefix(TypeConverter.convert(java.lang.String.class, jedis.hget(key, "Prefix")));
    objet.setTag(TypeConverter.convert(java.lang.String.class, jedis.hget(key, "Tag")));
    objet.setRank(TypeConverter.convert(int.class, jedis.hget(key, "Rank")));
    objet.setSuffix(TypeConverter.convert(java.lang.String.class, jedis.hget(key, "Suffix")));
  }

  public static void load(Jedis jedis, String key, PlayerBean objet) {
    objet.setName(TypeConverter.convert(java.lang.String.class, jedis.hget(key, "Name")));
    objet.setUuid(TypeConverter.convert(java.util.UUID.class, jedis.hget(key, "Uuid")));
    objet.setStopTime(TypeConverter.convert(long.class, jedis.hget(key, "StopTime")));
    objet.setCoins(TypeConverter.convert(int.class, jedis.hget(key, "Coins")));
    objet.setToptpKey(TypeConverter.convert(java.lang.String.class, jedis.hget(key, "ToptpKey")));
    objet.setLastLogin(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget(key, "LastLogin")));
    objet.setStartTime(TypeConverter.convert(long.class, jedis.hget(key, "StartTime")));
    objet.setFirstLogin(TypeConverter.convert(java.sql.Timestamp.class, jedis.hget(key, "FirstLogin")));
    objet.setNickNale(TypeConverter.convert(java.lang.String.class, jedis.hget(key, "NickNale")));
    objet.setGroupId(TypeConverter.convert(long.class, jedis.hget(key, "GroupId")));
    objet.setStars(TypeConverter.convert(int.class, jedis.hget(key, "Stars")));
    objet.setLastIP(TypeConverter.convert(java.lang.String.class, jedis.hget(key, "LastIP")));
  }

  public static void send(Jedis jedis, String key, GroupsBean objet) {
    jedis.hset(key, "Rank", "" + objet.getRank());
    jedis.hset(key, "GroupId", "" + objet.getGroupId());
    jedis.hset(key, "Multiplier", "" + objet.getMultiplier());
    jedis.hset(key, "Prefix", "" + objet.getPrefix());
    jedis.hset(key, "Suffix", "" + objet.getSuffix());
    jedis.hset(key, "Tag", "" + objet.getTag());
    jedis.hset(key, "PgroupName", "" + objet.getPgroupName());
  }

  public static void send(Jedis jedis, String key, PlayerBean objet) {
    jedis.hset(key, "Name", "" + objet.getName());
    jedis.hset(key, "ToptpKey", "" + objet.getToptpKey());
    jedis.hset(key, "PlayedTime", "" + objet.getPlayedTime());
    jedis.hset(key, "NickName", "" + objet.getNickName());
    jedis.hset(key, "LastLogin", "" + objet.getLastLogin());
    jedis.hset(key, "StartTime", "" + objet.getStartTime());
    jedis.hset(key, "StopTime", "" + objet.getStopTime());
    jedis.hset(key, "GroupId", "" + objet.getGroupId());
    jedis.hset(key, "Uuid", "" + objet.getUuid());
    jedis.hset(key, "Coins", "" + objet.getCoins());
    jedis.hset(key, "Stars", "" + objet.getStars());
    jedis.hset(key, "FirstLogin", "" + objet.getFirstLogin());
    jedis.hset(key, "LastIP", "" + objet.getLastIP());
  }
}
