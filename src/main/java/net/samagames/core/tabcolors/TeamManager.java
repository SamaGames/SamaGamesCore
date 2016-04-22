package net.samagames.core.tabcolors;

import net.minecraft.server.v1_9_R1.ScoreboardTeamBase;
import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.permissions.PermissionEntity;
import net.samagames.core.api.permissions.PermissionManager;
import net.samagames.persistanceapi.beans.players.GroupsBean;
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
    private final ApiImplementation api;
    private final TeamHandler teamHandler;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TeamManager(APIPlugin pl)
    {
        manager = pl.getAPI().getPermissionsManager();
        api = pl.getAPI();

        teamHandler = new TeamHandler();

        for (long i = 1; ; i++)
        {
            GroupsBean groupsBean = manager.getGroupByID(i);
            if (groupsBean == null)
                break;
            //String teamName = pg.getProperty("team-name");
            String teamName = groupsBean.getPgroupName();

            if (teamHandler.getTeamByName(teamName) != null)
                continue;

            TeamHandler.VTeam vt = teamHandler.createNewTeam(teamName, "");

            vt.setRealName(getTeamName(teamName, groupsBean.getRank()));
            vt.setPrefix(parseColor(groupsBean.getTag()));
            vt.setDisplayName(parseColor(groupsBean.getTag()));
            vt.setSuffix(parseColor(groupsBean.getSuffix()));

            teamHandler.addTeam(vt);
            APIPlugin.log("[TeamRegister] Team " + teamName + " ajoutée  --> " + vt.getPrefix() + " / " + vt.getName());
        }

        manager.setFakeGroupBean(manager.getGroupByID(2));

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
                //PlayerData playerData = api.getPlayerManager().getPlayerData(p.getUniqueId());
                TeamHandler.VTeam teamByName = teamHandler.getTeamByName(user.getDisplayGroupName());
                if (teamByName == null)
                {
                    teamByName = teamHandler.getTeamByName("Joueur");
                }
                teamHandler.addPlayerToTeam(p, teamByName);
            }
        });
    }

    private String parseColor(String value)
    {
        if (value == null)
            return "";
        value = value.replaceAll("&s", " ");
        value = org.bukkit.ChatColor.translateAlternateColorCodes('&', value);
        return value;
    }

    public TeamHandler getTeamHandler()
    {
        return teamHandler;
    }

}
