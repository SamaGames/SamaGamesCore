package net.samagames.core.api.permissions;

import net.samagames.api.permissions.IPermissionsEntity;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.persistanceapi.beans.permissions.PlayerPermissionBean;
import net.samagames.persistanceapi.beans.players.GroupsBean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Silvanosky on 16/03/2016.
 */
public class PermissionEntity implements IPermissionsEntity {

    private UUID uuid;
    private APIPlugin plugin;
    private final GameServiceManager manager;

    private GroupsBean groupsBean;

    //private PermissionAttachment attachment;

    private Map<String, Boolean> permissions = new HashMap<>();
    private static final String key = "permissions:";
    private static final String subkeyPerms = ":list";

    private PlayerData playerData;

    private PermissionAttachment attachment;

    public PermissionEntity(UUID player, APIPlugin plugin)
    {
        this.uuid = player;
        this.plugin = plugin;
        this.manager = plugin.getGameServiceManager();
        this.playerData = plugin.getAPI().getPlayerManager().getPlayerData(player);

        //this.attachment = null;
        groupsBean = new GroupsBean();
    }

    @Override
    public UUID getUUID()
    {
        return uuid;
    }

    @Override
    public void refresh()
    {
        //Jedis jedis = plugin.getDatabaseConnector().getBungeeResource();
        try{


            //Get group (static because easier for generation FUCK YOU if you comment this)
            //CacheLoader.load(jedis, key + uuid, groupsBean);

            PlayerPermissionBean allPlayerPermission = null;
            try {
                this.groupsBean = plugin.getGameServiceManager().getGroupPlayer(playerData.getPlayerBean());
                allPlayerPermission = plugin.getGameServiceManager().getAllPlayerPermission(playerData.getPlayerBean());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Get perm list
            //Map<String, String> datas = jedis.hgetAll(key + uuid + subkeyPerms);
            permissions.clear();
            if (allPlayerPermission != null)
            {
                for (Map.Entry<String, Boolean> entry : allPlayerPermission.getHashMap().entrySet())
                {
                    //Save cache
                    permissions.put(entry.getKey(), entry.getValue());
                }
            }

            reloadPermissions(Bukkit.getPlayer(uuid));

        }catch (Exception e)
        {
            e.printStackTrace();
        }/*finally {
            //jedis.close();
        }*/
    }
    public void reloadPermissions(Player player)
    {
        if (attachment != null) {
            attachment.getPermissions().keySet().stream().forEach(attachment::unsetPermission);
        }
        applyPermissions(player);
    }

    public void applyPermissions(Player player)
    {
        if(player != null)
        {
            if (attachment == null)
                attachment = player.addAttachment(plugin);

            for (Map.Entry<String, Boolean> data : permissions.entrySet())
            {
                //System.out.print("Permission " + data.getKey() + " value: " + data.getValue());
                attachment.setPermission(data.getKey(), data.getValue());
            }
        }
    }

    public void unloadPlayer(Player player)
    {
        permissions.clear();
        reloadPermissions(player);
        attachment.remove();
    }

    public GroupsBean getDisplayGroup()
    {
        return (playerData.hasNickname()) ? plugin.getAPI().getPermissionsManager().getFakeGroupBean() : this.groupsBean;
    }

    @Override
    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    @Override
    public boolean hasPermission(String name) {
        Boolean value = permissions.get(name);
        return value != null && value;//if null return false
    }

    @Override
    public String getDisplayPrefix()
    {
        return formatText(getDisplayGroup().getPrefix());
    }

    @Override
    public String getPrefix()
    {
        return formatText(this.groupsBean.getPrefix());
    }

    @Override
    public String getDisplaySuffix()
    {
        return formatText(getDisplayGroup().getSuffix());
    }

    @Override
    public String getSuffix() {
        return formatText(this.groupsBean.getSuffix());
    }

    @Override
    public long getDisplayGroupId() {
        return this.getDisplayGroup().getGroupId();
    }

    @Override
    public long getGroupId() {
        return this.groupsBean.getGroupId();
    }

    @Override
    public int getDisplayRank() {
        return getDisplayGroup().getRank();
    }

    @Override
    public int getRank() {
        return groupsBean.getRank();
    }

    @Override
    public String getDisplayTag() {
        return formatText(getDisplayGroup().getTag());
    }

    @Override
    public String getTag() {
        return formatText(this.groupsBean.getTag());
    }

    private String formatText(String value)
    {
        if (value == null)
            return "";
        return ChatColor.translateAlternateColorCodes('&', value.replaceAll("&s", " "));
    }

    public String getDisplayGroupName()
    {
        return getDisplayGroup().getPgroupName();
    }

    public String getGroupName()
    {
        return this.groupsBean.getPgroupName();
    }

    @Override
    public int getMultiplier() {
        return groupsBean.getMultiplier();
    }
}
