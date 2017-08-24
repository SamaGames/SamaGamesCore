package net.samagames.core.listeners.general;

import net.samagames.core.ApiImplementation;
import net.samagames.core.api.permissions.PermissionEntity;
import net.samagames.core.api.permissions.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
public class GlobalJoinListener implements Listener {

    private ApiImplementation api;

    public GlobalJoinListener(ApiImplementation api)
    {

        this.api = api;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event)
    {
        try{
            long startTime = System.currentTimeMillis();
            UUID player = event.getUniqueId();

            //First load main data
            api.getPlayerManager().loadPlayer(player);
            AtomicInteger number = new AtomicInteger(0);

            api.getPlugin().getExecutor().execute(() -> {
                //Load permissions
                api.getPermissionsManager().loadPlayer(player);
                number.incrementAndGet();
            });

            api.getPlugin().getExecutor().execute(() -> {
                api.getSettingsManager().loadPlayer(player);
                number.incrementAndGet();
            });

            api.getPlugin().getExecutor().execute(() -> {
                api.getStatsManager().loadPlayer(player);
                number.incrementAndGet();
            });

            api.getPlugin().getExecutor().execute(() -> {
                api.getShopsManager().loadPlayer(player);
                number.incrementAndGet();
            });

            api.getPlugin().getExecutor().execute(() -> {
                api.getFriendsManager().loadPlayer(player);
                number.incrementAndGet();
            });

            api.getPlugin().getExecutor().execute(() -> {
                api.getPartiesManager().loadPlayer(player);
                number.incrementAndGet();
            });

            api.getPlugin().getExecutor().execute(() -> {
                api.getAchievementManager().loadPlayer(player);
                number.incrementAndGet();
            });

            while (number.get() < 7);

            //Load in game api
            api.getJoinManager().onLogin(event);
            api.getPlugin().getLogger().info("AsyncPrelogin Time: " + (System.currentTimeMillis() - startTime));
        }catch (Exception e)
        {
            e.printStackTrace();
            event.setKickMessage("Erreur lors du chargement de votre profil.");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        long startTime = System.currentTimeMillis();

        //PlayerData playerData = api.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
        /*if (playerData.hasNickname())
        {
            playerData.applyNickname(event.getPlayer());
        }*/

        //Permissions already loaded in async, just apply them
        PermissionManager permissionManager = api.getPermissionsManager();
        PermissionEntity permissionEntity = permissionManager.getPlayer(event.getPlayer().getUniqueId());
        if (permissionEntity == null)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Erreur lors du chargement de votre profil");
            return ;
        }
        permissionEntity.applyPermissions(event.getPlayer());

        if (permissionEntity.hasPermission("network.admin"))
            event.getPlayer().setOp(true);

        api.getPlugin().getLogger().info("Login Time: " + (System.currentTimeMillis() - startTime));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        long startTime = System.currentTimeMillis();

        //Remove natural join message
        event.setJoinMessage("");

        //Game join handle
        api.getJoinManager().onJoin(event.getPlayer());

        api.getPlugin().getLogger().info("Join Time: " + (System.currentTimeMillis() - startTime));
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

        //Unload player party
        api.getPartiesManager().unloadPlayer(p.getUniqueId());

        //Unload friend from cache
        api.getFriendsManager().unloadPlayer(p.getUniqueId());

        //Unload all partis wihtout player
        api.getPartiesManager().unloadParties();

        //Unload settings from cache
        api.getSettingsManager().unloadPlayer(p.getUniqueId());

        //Unload stats from cache
        api.getStatsManager().unloadPlayer(p.getUniqueId());

        //Unload shops
        api.getShopsManager().unloadPlayer(p.getUniqueId());

        //Unload achievements
        api.getAchievementManager().unloadPlayer(p.getUniqueId());

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
        /*try{
            api.getPlayerManager().getPlayerData(player).updateData();
        }catch (Exception ignored){
        }*/
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
