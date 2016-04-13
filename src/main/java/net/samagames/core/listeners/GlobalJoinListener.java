package net.samagames.core.listeners;

import net.samagames.core.ApiImplementation;
import net.samagames.core.api.permissions.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

/**
 * Created by Silvanosky on 22/03/2016.
 */
public class GlobalJoinListener implements Listener {

    private ApiImplementation api;

    public GlobalJoinListener(ApiImplementation api)
    {

        this.api = api;
        //TODO register the object in listeners
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event)
    {

        //First load main data
        api.getPlayerManager().loadPlayer(event.getUniqueId());

        //Load permissions
        api.getPermissionsManager().loadPlayer(event.getUniqueId());

        //TODO load all managers

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        //On join reload permissions to add in the bukkit system
        PermissionManager permissionManager = api.getPermissionsManager();
        if (!permissionManager.isLobby())
            permissionManager.refreshPlayer(event.getPlayer());
        else
            Bukkit.getScheduler().runTaskAsynchronously(api.getPlugin(),
                    () -> permissionManager.refreshPlayer(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        //Remove natural join message
        event.setJoinMessage("");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        // Remove quit message
        event.setQuitMessage("");
        this.onLeaveEvent(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKicked(PlayerKickEvent event)
    {
        // Remove leave message
        event.setLeaveMessage("");
        this.onLeaveEvent(event.getPlayer());
    }

    private void onLeaveEvent(Player p)
    {
        //Unload permission player cache
        api.getPermissionsManager().unloadPlayer(p);

        // Last unload
        api.getPlayerManager().unloadPlayer(p.getUniqueId());
        //Reset player scoreboard on leave
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
