package net.samagames.core.tabcolors;

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

        TeamHandler.VTeam npc = teamHandler.createNewTeam("NPC", "NPC");
        npc.setRealName("NPC");
        npc.setHideToOtherTeams(true);
        APIPlugin.log("[TeamRegister] Team NPC ajoutée  --> " + npc.getPrefix() + " / " + npc);
        teamHandler.addTeam(npc);

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
        executor.execute(() -> {
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
