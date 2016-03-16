package net.samagames.core.api.permissions;

import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class PermissionListeners implements Listener
{

    private APIPlugin plugin;
    private final PermissionManager manager;

    public PermissionListeners(APIPlugin plugin, PermissionManager manager)
    {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(final AsyncPlayerPreLoginEvent ev)
    {
        manager.loadPlayer(ev.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent ev)
    {
        if (!manager.isLobby())
            manager.refreshPlayer(ev.getPlayer());
        else
            Bukkit.getScheduler().runTaskAsynchronously(plugin,
                    () -> manager.refreshPlayer(ev.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeave(PlayerQuitEvent ev)
    {
        disconnect(ev.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent ev)
    {
        disconnect(ev.getPlayer());
    }

    private void disconnect(Player player)
    {
        manager.unloadPlayer(player);
    }



}
