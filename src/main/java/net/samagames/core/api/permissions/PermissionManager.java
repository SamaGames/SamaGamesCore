package net.samagames.core.api.permissions;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.permissions.IPermissionsEntity;
import net.samagames.api.permissions.IPermissionsManager;
import net.samagames.core.APIPlugin;
import net.samagames.persistanceapi.beans.players.GroupsBean;
import net.samagames.persistanceapi.beans.players.PlayerBean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class PermissionManager implements IPermissionsManager
{
    private final boolean isLobby;
    private final HashMap<UUID, PermissionEntity> cache = new HashMap<>();
    private APIPlugin plugin;

    public PermissionManager(APIPlugin plugin)
    {
        this.plugin = plugin;
        this.isLobby = SamaGamesAPI.get().getServerName().startsWith("Hub");
        Bukkit.getLogger().info("Lobby mode was set to : " + isLobby);
    }

    public void loadPlayer(UUID player)
    {
        PermissionEntity permissionEntity = new PermissionEntity(player, plugin);
        permissionEntity.refresh();
        cache.put(player, permissionEntity);
    }

    public void refreshPlayer(Player p)
    {
        cache.get(p.getUniqueId()).refresh();
    }

    public void unloadPlayer(Player player)
    {
        cache.remove(player.getUniqueId());
    }

    public boolean isLobby()
    {
        return isLobby;
    }

    @Override
    public PermissionEntity getPlayer(UUID player)
    {
        return cache.get(player);
    }

    @Override
    public String getPrefix(IPermissionsEntity entity) {
        String value = entity.getPrefix();
        if (value == null)
            return "";
        value = value.replaceAll("&s", " ");
        value = ChatColor.translateAlternateColorCodes('&', value);
        return value;
    }

    @Override
    public String getSuffix(IPermissionsEntity entity) {
        String value = entity.getSuffix();
        if (value == null)
            return "";
        value = value.replaceAll("&s", " ");
        value = ChatColor.translateAlternateColorCodes('&', value);
        return value;
    }

    @Override
    public String getDisplay(IPermissionsEntity entity) {
        String value = entity.getTag();
        if (value == null)
            return "";
        value = value.replaceAll("&s", " ");
        value = ChatColor.translateAlternateColorCodes('&', value);
        return value;
    }

    @Override
    public boolean hasPermission(IPermissionsEntity entity, String permission) {
        return entity.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(UUID player, String permission) {
        PermissionEntity permissionEntity = cache.get(player);

        return (permissionEntity != null) && permissionEntity.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(Player player, String permission)
    {
        return hasPermission(player.getUniqueId(), permission);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permission)
    {
        if (sender instanceof ConsoleCommandSender)
            return true;
        else if (sender instanceof Player)
            return hasPermission((Player) sender, permission);
        return false;
    }

    public GroupsBean getGroupByID(long id)
    {
        PlayerBean group = new PlayerBean(null, null, null, 0, 0, null, null, null, null, id);
        try {
            return plugin.getAPI().getGameServiceManager().getGroupPlayer(group);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
