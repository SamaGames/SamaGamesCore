package net.samagames.core.api.permissions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListeners implements Listener
{

    protected final BasicPermissionManager plugin;

    public PlayerListeners(BasicPermissionManager plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(final AsyncPlayerPreLoginEvent ev)
    {
        plugin.getApi().getManager().getUser(ev.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent ev)
    {
        if (!plugin.isLobby())
            plugin.api.getManager().refreshPerms(ev.getPlayer().getUniqueId());
        else
            plugin.runAsync(() -> plugin.getApi().getManager().refreshPerms(ev.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeave(PlayerQuitEvent ev)
    {
        disconnect(ev.getPlayer().getUniqueId());
    }

    public void disconnect(UUID player)
    {
        plugin.removePlayer(player);
    }

    @EventHandler
    public void onKick(PlayerKickEvent ev)
    {
        disconnect(ev.getPlayer().getUniqueId());
    }

}
