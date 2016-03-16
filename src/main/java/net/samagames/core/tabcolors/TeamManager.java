package net.samagames.core.tabcolors;

import net.minecraft.server.v1_9_R1.ScoreboardTeamBase;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.permissions.IPermissionsManager;
import net.samagames.api.permissions.permissions.PermissionGroup;
import net.samagames.api.permissions.permissions.PermissionUser;
import net.samagames.api.permissions.restfull.RestfullManager;
import net.samagames.core.APIPlugin;
import net.samagames.tools.scoreboards.TeamHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TeamManager
{

    /**
     * The escape sequence for minecraft special chat codes
     */
    public static final char ESCAPE = '\u00A7';
    private final IPermissionsManager manager;
    private final List<PermissionGroup> groups = new ArrayList<>();
    private final TeamHandler teamHandler;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TeamManager(APIPlugin pl)
    {
        manager = pl.getAPI().getPermissionsManager();

        teamHandler = new TeamHandler();
        
        groups.addAll(((RestfullManager) manager.getApi().getManager()).getGroups().stream().collect(Collectors.toList()));

        for (PermissionGroup pg : groups)
        {
            if (pg == null)
                continue;

            //String teamName = pg.getProperty("team-name");
            String teamName = pg.getGroupName();

            if (teamHandler.getTeamByName(teamName) != null)
                continue;

            TeamHandler.VTeam vt = teamHandler.createNewTeam(teamName, "");

            vt.setRealName(getTeamName(pg));
            if (manager.getDisplay(pg) != null)
                vt.setPrefix(manager.getDisplay(pg));
            if (manager.getDisplay(pg) != null)
                vt.setDisplayName(manager.getDisplay(pg));
            if (manager.getSuffix(pg) != null)
                vt.setSuffix(manager.getSuffix(pg));

            teamHandler.addTeam(vt);
            APIPlugin.log("[TeamRegister] Team " + teamName + " ajoutée  --> " + vt.getPrefix() + " / " + vt);
        }

        TeamHandler.VTeam npc = teamHandler.createNewTeam("NPC", "");
        npc.setRealName("NPC");
        npc.setNameVisible(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        APIPlugin.log("[TeamRegister] Team NPC ajoutée  --> " + npc.getPrefix() + " / " + npc);

    }

    private String getTeamName(PermissionGroup group)
    {
        String teamName = ((group.getLadder()< 1000)?"0":"") +
                ((group.getLadder()< 100)?"0":"") +
                ((group.getLadder()< 10)?"0":"") +
                group.getLadder() + group.getGroupName();
        return teamName.substring(0, Math.min(teamName.length(), 16));
    }

    /**
     * Takes a string and replaces &# color codes with ChatColors
     */

    public void playerLeave(final Player p)
    {
        executor.execute(() -> teamHandler.removeReceiver(p));
    }

    public void playerJoin(final Player p)
    {
        executor.execute(() -> {
            teamHandler.addReceiver(p);

            if(SamaGamesAPI.get().getServerOptions().hasRankTabColor())
            {
                final PermissionUser user = manager.getApi().getUser(p.getUniqueId());
                final String prefix = user.getParents().last().getGroupName();

                TeamHandler.VTeam vtt = teamHandler.getTeamByName(prefix);
                if (vtt == null)
                {
                    vtt = teamHandler.getTeamByName("joueur");
                }

                teamHandler.addPlayerToTeam(p, vtt);
            }
        });
    }

    public TeamHandler getTeamHandler()
    {
        return teamHandler;
    }

}
