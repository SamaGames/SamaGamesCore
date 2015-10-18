package net.samagames.core.api.permissions;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.permissions.IPermissionsManager;
import net.samagames.api.permissions.PermissionsAPI;
import net.samagames.api.permissions.rawtypes.RawPlayer;
import net.samagames.api.permissions.restfull.RestfullManager;
import net.samagames.api.permissions.restfull.RestfullPlugin;
import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public abstract class BasicPermissionManager implements RestfullPlugin, IPermissionsManager
{

    private final ArrayList<BukkitTask> tasks = new ArrayList<>();
    private final boolean isLobby;
    private final HashMap<UUID, VirtualPlayer> players = new HashMap<>();
    final PermissionsAPI api;

    BasicPermissionManager(APIPlugin plugin)
    {
        isLobby = SamaGamesAPI.get().getServerName().startsWith("Hub");
        Bukkit.getLogger().info("Lobby mode was set to : " + isLobby);

        logInfo(">> LOADING PERMISSIONS API !");
        api = new PermissionsAPI(this, "Joueur").setCacheManager(plugin.getCacheHandler()).adaptator(RestfullManager.class).enableRefresh();
        api.getManager().refreshGroups();
        logInfo(">> LOADED PERMISSIONS API !");

        plugin.getServer().getPluginManager().registerEvents(new PlayerListeners(this), APIPlugin.getInstance());
    }

    public void disable()
    {
        tasks.stream().filter(task -> task != null).forEach(org.bukkit.scheduler.BukkitTask::cancel);
        logInfo("Cancelled tasks successfully.");
    }

    public boolean isLobby()
    {
        return isLobby;
    }

    @Override
    public PermissionsAPI getApi()
    {
        return api;
    }

    @Override
    public void logSevere(String log)
    {
        APIPlugin.log(Level.SEVERE, "[PERM] " + log);
    }

    @Override
    public void logWarning(String log)
    {
        APIPlugin.log(Level.WARNING, "[PERM] " + log);
    }

    @Override
    public void logInfo(String log)
    {
        APIPlugin.log(Level.INFO, "[PERM] " + log);
    }

    @Override
    public void runRepeatedTaskAsync(Runnable task, long delay, long timeBeforeRun)
    {
        tasks.add(Bukkit.getScheduler().runTaskTimerAsynchronously(APIPlugin.getInstance(), task, timeBeforeRun, delay));
    }

    @Override
    public void runAsync(Runnable task)
    {
        Bukkit.getScheduler().runTaskAsynchronously(APIPlugin.getInstance(), task);
    }

    @Override
    public boolean isOnline(UUID player)
    {
        Player p = Bukkit.getPlayer(player);
        return (p != null && p.isOnline());
    }

    @Override
    public RawPlayer getPlayer(UUID player)
    {
        Player p = Bukkit.getPlayer(player);
        if (p == null)
            return null;
        if (players.containsKey(player))
            return players.get(player);

        VirtualPlayer pl = new VirtualPlayer(p);
        players.put(player, pl);
        return pl;
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

    public void removePlayer(UUID uuid)
    {
        this.players.remove(uuid);
    }
}
