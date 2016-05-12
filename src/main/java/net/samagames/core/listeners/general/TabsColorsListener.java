package net.samagames.core.listeners.general;

import net.samagames.core.APIPlugin;
import net.samagames.core.api.permissions.PermissionEntity;
import net.samagames.core.tabcolors.TeamManager;
import net.samagames.tools.scoreboards.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Silvanosky
 * (C) Copyright Elydra Network 2016 - 2017
 * All rights reserved.
 */
public class TabsColorsListener extends APIListener
{
    private final TeamManager manager;

    public TabsColorsListener(APIPlugin plugin)
    {
        super(plugin);

        manager = new TeamManager(plugin);

        plugin.getAPI().getNPCManager().setScoreBoardRegister((data, error) -> {
            TeamHandler.VTeam npc = manager.getTeamHandler().getTeamByName("NPC");
            if(npc != null)
            {
                manager.getTeamHandler().addPlayerToTeam(data.getName(), npc);
            }
        });
    }

    private String replaceColors(String message)
    {
        String s = message;
        for (ChatColor color : ChatColor.values())
        {
            s = s.replaceAll("(?i)&" + color.getChar(), "" + color);
        }
        return s;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        final Player p = event.getPlayer();
        manager.playerJoin(p); // Passer ça en sync si crash //
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PermissionEntity user = api.getPermissionsManager().getPlayer(p.getUniqueId());
            final String displayn = replaceColors(user.getTag() + "" + user.getPrefix()) + p.getName();
            p.setDisplayName(displayn);
        });
    }

    @EventHandler
    public void playerQuit(final PlayerQuitEvent event)
    {
        manager.playerLeave(event.getPlayer());
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    @EventHandler
    public void playerKick(final PlayerKickEvent event)
    {
        manager.playerLeave(event.getPlayer());
    }
}
