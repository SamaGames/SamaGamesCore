package net.samagames.core.api.settings;

import java.lang.Override;
import net.samagames.api.settings.IPlayerSettings;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.players.PlayerSettingsBean;
import net.samagames.tools.TypeConverter;
import redis.clients.jedis.Jedis;

public class PlayerSettings extends PlayerSettingsBean implements IPlayerSettings {
  private PlayerData playerData;

  private ApiImplementation api;

  public PlayerSettings(PlayerData playerData, PlayerSettingsBean bean) {
    super(playerData.getPlayerID()
        ,bean.isJukeboxListen()
        ,bean.isGroupDemandReceive()
        ,bean.isFriendshipDemandReceive()
        ,bean.isNotificationReceive()
        ,bean.isPrivateMessageReceive()
        ,bean.isChatVisible()
        ,bean.isPlayerVisible()
        ,bean.isWaitingLineNotification()
        ,bean.isOtherPlayerInteraction()
        ,bean.isClickOnMeActivation()
        ,bean.isAllowStatisticOnClick()
        ,bean.isAllowCoinsOnClick()
        ,bean.isAllowStarsOnclick()
        ,bean.isAllowClickOnOther()
        );
    this.api = (ApiImplementation) ApiImplementation.get();
    this.playerData = playerData;
  }

  public PlayerSettings(PlayerData playerData) {
    super(playerData.getPlayerID()
        ,false
        ,false
        ,false
        ,false
        ,false
        ,false
        ,false
        ,false
        ,false
        ,false
        ,false
        ,false
        ,false
        ,false
        );
    this.api = (ApiImplementation) ApiImplementation.get();
    this.playerData = playerData;
  }

  @Override
  public void update() {
    Jedis jedis = this.api.getBungeeResource();
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "PlayerVisible", "" + isPlayerVisible());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "JukeboxListen", "" + isJukeboxListen());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "Uuid", "" + getUuid());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "ChatVisible", "" + isChatVisible());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "NotificationReceive", "" + isNotificationReceive());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "AllowCoinsOnClick", "" + isAllowCoinsOnClick());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "FriendshipDemandReceive", "" + isFriendshipDemandReceive());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "GroupDemandReceive", "" + isGroupDemandReceive());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "AllowStatisticOnClick", "" + isAllowStatisticOnClick());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "ClickOnMeActivation", "" + isClickOnMeActivation());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "AllowStarsOnclick", "" + isAllowStarsOnclick());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "AllowClickOnOther", "" + isAllowClickOnOther());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "PrivateMessageReceive", "" + isPrivateMessageReceive());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "OtherPlayerInteraction", "" + isOtherPlayerInteraction());
    jedis.hset("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "WaitingLineNotification", "" + isWaitingLineNotification());
    jedis.close();
  }

  @Override
  public void refresh() {
    Jedis jedis = this.api.getBungeeResource();
    setChatVisible(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "ChatVisible")));
    setJukeboxListen(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "JukeboxListen")));
    setPlayerVisible(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "PlayerVisible")));
    setWaitingLineNotification(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "WaitingLineNotification")));
    setAllowStatisticOnClick(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "AllowStatisticOnClick")));
    setFriendshipDemandReceive(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "FriendshipDemandReceive")));
    setAllowClickOnOther(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "AllowClickOnOther")));
    setNotificationReceive(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "NotificationReceive")));
    setPrivateMessageReceive(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "PrivateMessageReceive")));
    setOtherPlayerInteraction(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "OtherPlayerInteraction")));
    setClickOnMeActivation(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "ClickOnMeActivation")));
    setAllowCoinsOnClick(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "AllowCoinsOnClick")));
    setAllowStarsOnclick(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "AllowStarsOnclick")));
    setGroupDemandReceive(TypeConverter.convert(boolean.class, jedis.hget("settings:" + playerData.getPlayerID() + ":PlayerSettingsBean", "GroupDemandReceive")));
    jedis.close();
  }
}
