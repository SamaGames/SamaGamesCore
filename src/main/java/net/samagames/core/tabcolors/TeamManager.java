package net.samagames.core.tabcolors;

import net.minecraft.server.v1_9_R1.ScoreboardTeamBase;
import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.permissions.PermissionEntity;
import net.samagames.core.api.permissions.PermissionManager;
import net.samagames.tools.scoreboards.TeamHandler;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeamManager
{

    /**
     * The escape sequence for minecraft special chat codes
     */
    public static final char ESCAPE = '\u00A7';
    private final PermissionManager manager;
    private final TeamHandler teamHandler;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TeamManager(APIPlugin pl)
    {
        manager = pl.getAPI().getPermissionsManager();

        teamHandler = new TeamHandler();

        TeamHandler.VTeam npc = teamHandler.createNewTeam("NPC", "");
        npc.setRealName("NPC");
        npc.setNameVisible(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        APIPlugin.log("[TeamRegister] Team NPC ajoutée  --> " + npc.getPrefix() + " / " + npc);

    }

    private String getTeamName(String name, int rank)
    {
        String teamName = ((rank< 1000)?"0":"") +
                ((rank< 100)?"0":"") +
                ((rank< 10)?"0":"") +
                rank + name;
        return teamName.substring(0, Math.min(teamName.length(), 16));
    }

    /**
     * Takes a string and replaces &# color codes with ChatColors
     */

    public void playerLeave(final Player p)
    {
        executor.execute(() ->{
            final PermissionEntity user = manager.getPlayer(p.getUniqueId());
            teamHandler.removeTeam(getTeamName(p.getName(), user.getRank()));
            teamHandler.removeReceiver(p);
        });
    }

    public void playerJoin(final Player p)
    {
        executor.execute(() -> {
            teamHandler.addReceiver(p);

            if(SamaGamesAPI.get().getServerOptions().hasRankTabColor())
            {
                final PermissionEntity user = manager.getPlayer(p.getUniqueId());
                final String prefix = user.getTag();
                TeamHandler.VTeam newTeam = teamHandler.createNewTeam(getTeamName(p.getName(), user.getRank()), "");


                newTeam.setRealName(getTeamName(prefix, user.getRank()));
                newTeam.setPrefix(user.getTag());
                newTeam.setDisplayName(user.getTag());
                newTeam.setSuffix(user.getSuffix());
                teamHandler.addTeam(newTeam);

                teamHandler.addPlayerToTeam(p, newTeam);
            }
        });
    }

    public TeamHandler getTeamHandler()
    {
        return teamHandler;
    }

}
