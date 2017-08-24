package net.samagames.core.api.network;

import net.md_5.bungee.api.ChatColor;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.network.IJoinHandler;
import net.samagames.api.network.IJoinManager;
import net.samagames.api.network.JoinResponse;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.parties.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

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
public class JoinManagerImplement implements IJoinManager
{
    private final TreeMap<Integer, IJoinHandler> joiners = new TreeMap<>();
    private final List<UUID> moderatorsExpected = new ArrayList<>();
    private final List<UUID> playersExpected = new ArrayList<>();
    private final boolean isPartyLimited;

    private ApiImplementation api;

    public JoinManagerImplement(ApiImplementation api)
    {
        this.api = api;
        this.isPartyLimited = !api.getPlugin().isHub();
    }

    private boolean isPartyLimited()
    {
        return this.isPartyLimited;
    }

    @Override
    public void registerHandler(IJoinHandler handler, int priority)
    {
        this.joiners.put(priority, handler);
    }

    @Override
    public int countExpectedPlayers()
    {
        return this.getExpectedPlayers().size() + this.getModeratorsExpected().size();
    }

    @Override
    public List<UUID> getExpectedPlayers()
    {
        return this.playersExpected;
    }


    private JoinResponse requestSoloJoin(UUID player)
    {
        JoinResponse response = new JoinResponse();

        for (IJoinHandler handler : this.joiners.values())
        {
            response = handler.requestJoin(player, response);

            if (!response.isAllowed())
                break;
        }

        if (response.isAllowed())
        {
            playersExpected.add(player);
            Bukkit.getScheduler().runTaskLater(APIPlugin.getInstance(), () -> playersExpected.remove(player), 20 * 15L);
        }

        return response;
    }

    public JoinResponse requestPartyJoin(Party party, UUID joiningPlayer, boolean alreadyConnected)
    {
        UUID leader = party.getLeader();
        List<UUID> members = party.getPlayers();

        JoinResponse response = new JoinResponse();

        //On verifie que l'equipe peu rejoindre
        for (IJoinHandler handler : joiners.values())
        {
            response = handler.requestPartyJoin(party.getParty(), joiningPlayer, response);

            if (!response.isAllowed())
                break;
        }

        if (response.isAllowed())
        {
            //C'est bon, si c'est le leader on teleporte toute la partie
            if(leader.equals(joiningPlayer) && !alreadyConnected)
            {
                members.stream()
                        .filter(player ->
                                Bukkit.getPlayer(player) == null && !player.equals(joiningPlayer))
                        .forEach(player -> {
                            playersExpected.add(player);
                            Bukkit.getScheduler().runTaskLater(APIPlugin.getInstance(),
                                    () -> playersExpected.remove(player), 20 * 15L);

                            api.getPlayerManager().connectToServer(player, SamaGamesAPI.get().getServerName());
                        });
            }

            //On sauvegarde le serveur ou se situe la partie (useless)
            new Thread(() -> {
                Jedis jedis = SamaGamesAPI.get().getBungeeResource();
                jedis.set("party:" + party.getParty() + ":server", SamaGamesAPI.get().getServerName());
                jedis.close();
            }, "PartyUpdater").start();
        }

        return response;
    }

    public JoinResponse requestPartyJoin(Party party)
    {
        return requestPartyJoin(party, party.getLeader(), false);
    }

    public JoinResponse requestJoin(UUID player, boolean alreadyConnected)
    {
        Party party = this.api.getPartiesManager().getPartyForPlayer(player);
        if (party != null && isPartyLimited())
        {
            return requestPartyJoin(party, player, alreadyConnected);
        }

        return requestSoloJoin(player);
    }

    public void onLogin(AsyncPlayerPreLoginEvent event)
    {
        UUID player = event.getUniqueId();

        if (moderatorsExpected.contains(player)) // On traite après
            return;

        if(!playersExpected.contains(player))
        {
            JoinResponse response = requestJoin(event.getUniqueId(), true);
            if (!response.isAllowed())
            {
                event.disallow(Result.KICK_OTHER, ChatColor.RED + response.getReason());
                return;
            }
        }

        playersExpected.remove(player);

        for (IJoinHandler handler : this.joiners.values())
            handler.onLogin(player, event.getName());
    }

    public void onJoin(Player player)
    {
        if (moderatorsExpected.contains(player.getUniqueId()))
        {
            for (IJoinHandler handler : joiners.values())
                handler.onModerationJoin(player);

            return;
        }

        for (IJoinHandler handler : joiners.values())
            handler.finishJoin(player);
    }

    public void onLogout(Player player)
    {
        if (moderatorsExpected.contains(player.getUniqueId()))
        {
            moderatorsExpected.remove(player.getUniqueId());
            return;
        }

        for (IJoinHandler handler : joiners.values())
        {
            handler.onLogout(player);
        }
    }

    public void addModerator(UUID moderator)
    {
        moderatorsExpected.add(moderator);
    }

    public List<UUID> getModeratorsExpected()
    {
        return moderatorsExpected;
    }
}
