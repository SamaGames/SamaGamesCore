package net.samagames.core.api.permissions;

import net.samagames.api.permissions.IPermissionsEntity;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.persistanceapi.beans.GroupsBean;
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
    private final PlayerData playerData;
    private APIPlugin plugin;
    private final GameServiceManager manager;

    private long groupId;
    private String playerName;
    private int rank;
    private String tag;
    private String prefix;
    private String suffix;
    private int multiplier;

    private PermissionAttachment attachment;

    private Map<String, Boolean> permissions = new HashMap<>();

    public PermissionEntity(UUID player, PlayerData playerData, APIPlugin plugin)
    {
        this.uuid = player;
        this.playerData = playerData;
        this.plugin = plugin;
        this.manager = plugin.getGameServiceManager();

        this.attachment = null;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void refresh()
    {
        permissions.clear();
        permissions.putAll(manager.getAllPlayerPermission(playerData.getPlayerBean()).getHashMap());

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

        GroupsBean groupPlayer = manager.getGroupPlayer(playerData.getPlayerBean());
        groupId = groupPlayer.getGroupId();
        playerName = groupPlayer.getPlayerName();
        rank = groupPlayer.getRank();
        tag = groupPlayer.getTag();
        prefix = groupPlayer.getTag();
        suffix = groupPlayer.getSuffix();
        multiplier = groupPlayer.getMultiplier();
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
        String prefix = this.prefix;
        if (prefix == null)
            return "";
        prefix = prefix.replaceAll("&s", " ");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        return prefix;
    }

    @Override
    public String getSuffix() {
        String suffix = this.suffix;
        if (suffix == null)
            return "";
        suffix = suffix.replaceAll("&s", " ");
        suffix = ChatColor.translateAlternateColorCodes('&', suffix);
        return suffix;
    }

    @Override
    public long getGroupId() {
        return groupId;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public String getTag() {
        String display = tag;
        if (display == null)
            return "";
        return ChatColor.translateAlternateColorCodes('&', display.replaceAll("&s", " "));
    }

    @Override
    public int getMultiplier() {
        return multiplier;
    }
}
