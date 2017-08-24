package net.samagames.core.api.permissions;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.permissions.IPermissionsEntity;
import net.samagames.api.permissions.IPermissionsManager;
import net.samagames.core.ApiImplementation;
import net.samagames.persistanceapi.beans.players.GroupsBean;
import net.samagames.persistanceapi.beans.players.PlayerBean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/*
 * This file is part of SamaGamesCore.
 *
 * SamaGamesCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesCore.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PermissionManager implements IPermissionsManager
{
    private final boolean isLobby;
    private final HashMap<UUID, PermissionEntity> cache = new HashMap<>();
    private ApiImplementation api;

    private GroupsBean fakeGroupBean;

    public PermissionManager(ApiImplementation api)
    {
        this.api = api;
        this.isLobby = SamaGamesAPI.get().getServerName().startsWith("Hub");
        Bukkit.getLogger().info("Lobby mode was set to : " + isLobby);
    }

    public void loadPlayer(UUID player)
    {
        try{
            PermissionEntity permissionEntity = new PermissionEntity(player, api.getPlugin());
            permissionEntity.refresh();
            cache.put(player, permissionEntity);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void refreshPlayer(Player p)
    {
        cache.get(p.getUniqueId()).refresh();
    }

    public void unloadPlayer(Player player)
    {
        if (!api.isKeepCache())
        {
            cache.get(player.getUniqueId()).unloadPlayer(player);
            cache.remove(player.getUniqueId());
        }
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
        PlayerBean group = new PlayerBean(null, null, null, 0, 0, 0, null, null, null, null, id);
        try {
            return api.getGameServiceManager().getPlayerGroup(group);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GroupsBean getFakeGroupBean() {
        return fakeGroupBean;
    }

    public void setFakeGroupBean(GroupsBean fakeGroupBean) {
        this.fakeGroupBean = fakeGroupBean;
    }
}
