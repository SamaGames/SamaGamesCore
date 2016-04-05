package net.samagames.core.api.permissions;

import net.samagames.api.permissions.IPermissionsEntity;
import net.samagames.core.APIPlugin;
import net.samagames.core.utils.CacheLoader;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.persistanceapi.beans.GroupsBean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import redis.clients.jedis.Jedis;

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

    private PermissionAttachment attachment;

    private Map<String, Boolean> permissions = new HashMap<>();
    private static final String key = "permissions:";
    private static final String subkeyPerms = ":list";

    public PermissionEntity(UUID player, APIPlugin plugin)
    {
        this.uuid = player;
        this.plugin = plugin;
        this.manager = plugin.getGameServiceManager();

        this.attachment = null;
        groupsBean = new GroupsBean();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void refresh()
    {
        Jedis jedis = plugin.getDatabaseConnector().getBungeeResource();
        try{
            if (jedis.exists(key + uuid))
            {
                // Reset variable
                groupsBean = new GroupsBean();

                //Get group (static because easier for generation FUCK YOU if you comment this)
                CacheLoader.load(jedis, key + uuid, groupsBean);

                //Get perm list
                Map<String, String> datas = jedis.hgetAll(key + uuid + subkeyPerms);
                permissions.clear();
                for (Map.Entry<String, String> entry : datas.entrySet())
                {
                    //Save cache
                    permissions.put(entry.getKey(), Boolean.valueOf(entry.getValue()));
                }

                //Apply to bukkit system
                applyPermissions();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
    }

    public void applyPermissions()
    {
        if(attachment != null)
        {
            attachment.remove();
        }

        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline())
        {
            attachment = player.addAttachment(plugin);

            for (Map.Entry<String, Boolean> data : permissions.entrySet())
            {
                attachment.setPermission(data.getKey(), data.getValue());
            }
        }
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
    public String getPrefix() {
        String prefix = this.groupsBean.getPrefix();
        if (prefix == null)
            return "";
        prefix = prefix.replaceAll("&s", " ");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        return prefix;
    }

    @Override
    public String getSuffix() {
        String suffix = this.groupsBean.getSuffix();
        if (suffix == null)
            return "";
        suffix = suffix.replaceAll("&s", " ");
        suffix = ChatColor.translateAlternateColorCodes('&', suffix);
        return suffix;
    }

    @Override
    public long getGroupId() {
        return this.groupsBean.getGroupId();
    }

    @Override
    public String getPlayerName() {
        return groupsBean.getPlayerName();
    }

    @Override
    public int getRank() {
        return groupsBean.getRank();
    }

    @Override
    public String getTag() {
        String display = groupsBean.getTag();
        if (display == null)
            return "";
        return ChatColor.translateAlternateColorCodes('&', display.replaceAll("&s", " "));
    }

    @Override
    public int getMultiplier() {
        return groupsBean.getMultiplier();
    }
}
