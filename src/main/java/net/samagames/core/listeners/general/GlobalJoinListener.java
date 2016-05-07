package net.samagames.core.listeners.general;

import net.samagames.core.ApiImplementation;
import net.samagames.core.api.permissions.PermissionManager;
import net.samagames.core.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;

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
        long startTime = System.currentTimeMillis();
        UUID player = event.getUniqueId();
        //First load main data
        api.getPlayerManager().loadPlayer(event.getUniqueId());

        //Load permissions
        api.getPermissionsManager().loadPlayer(event.getUniqueId());

        api.getSettingsManager().loadPlayer(player);

        api.getStatsManager().loadPlayer(player);

        api.getShopsManager().loadPlayer(player);

        api.getPlayerManager().loadPlayer(player);

        api.getFriendsManager().loadPlayer(player);

        //Load in game api
        api.getJoinManager().onLogin(event);
        //api.getPlugin().getLogger().info("AsyncPrelogin Time: " + (System.currentTimeMillis() - startTime));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        PlayerData playerData = api.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
        /*if (playerData.hasNickname())
        {
            playerData.applyNickname(event.getPlayer());
        }*/
        long startTime = System.currentTimeMillis();

        //api.getPlugin().getLogger().info("Login Time: " + (System.currentTimeMillis() - startTime));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        long startTime = System.currentTimeMillis();

        //Permissions already loaded in async, just apply them
        PermissionManager permissionManager = api.getPermissionsManager();
        permissionManager.getPlayer(event.getPlayer().getUniqueId()).applyPermissions(event.getPlayer());

        //Remove natural join message
        event.setJoinMessage("");

        //Game join handle
        api.getJoinManager().onJoin(event.getPlayer());


        //api.getPlugin().getLogger().info("Join Time: " + (System.currentTimeMillis() - startTime));
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
        //first quit game
        api.getJoinManager().onLogout(p);

        //Unload friend from cache
        api.getFriendsManager().unloadPlayer(p.getUniqueId());

        //Unload all partis wihtout player
        api.getPartiesManager().unloadParties();

        //Unload settings from cache
        api.getSettingsManager().unloadPlayer(p.getUniqueId());

        //Unload stats from cache
        api.getStatsManager().unloadPlayer(p.getUniqueId());

        //Unload shops from cache
        api.getShopsManager().unloadPlayer(p.getUniqueId());

        //Unload permission player cache
        api.getPermissionsManager().unloadPlayer(p);

        // Last unload
        api.getPlayerManager().unloadPlayer(p.getUniqueId());

        //Reset player scoreboard on leave
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void onWillLeave(UUID player, String targetServer)
    {
        //TODO save all data in redis
        try{
            api.getPlayerManager().getPlayerData(player).updateData();
        }catch (Exception ignored){
        }
        try{
            api.getSettingsManager().getSettings(player).update();
        }catch (Exception ignored){
        }
        try{
            api.getStatsManager().getPlayerStats(player).updateStats();
        }catch (Exception ignored){
        }

    }
}
