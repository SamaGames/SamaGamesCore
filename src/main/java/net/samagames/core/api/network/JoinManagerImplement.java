package net.samagames.core.api.network;

import net.md_5.bungee.api.ChatColor;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.network.IJoinHandler;
import net.samagames.api.network.IJoinManager;
import net.samagames.api.network.JoinResponse;
import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * How does that work ?
 * <p>
 * A. The proxy send a request OR the client connect without request
 * -> requestJoin is called
 * -> the system checks if the player is or isn't in a party
 * -> He's in party : call partyJoin
 * -> He's not in party : call soloJoin
 * -> if the player is already connected :
 * -> Connexion refused : he's kicked
 * -> if the player just sent a request
 * -> Connection refused : the system sends him a message
 * -> Connection accepted : the system request the proxy to moove him
 */

public class JoinManagerImplement implements IJoinManager
{

    private final TreeMap<Integer, IJoinHandler> handlerTreeMap = new TreeMap<>();
    private final HashSet<UUID> moderatorsExpected = new HashSet<>();
    private final HashSet<UUID> playersExpected = new HashSet<>();
    private final boolean isPartyLimited;

    public JoinManagerImplement()
    {
        isPartyLimited = !SamaGamesAPI.get().getServerName().startsWith("Hub");
    }

    private boolean isPartyLimited()
    {
        return isPartyLimited;
    }

    @Override
    public void registerHandler(IJoinHandler handler, int priority)
    {
        this.handlerTreeMap.put(priority, handler);
    }

    @Override
    public int countExpectedPlayers()
    {
        return getExpectedPlayers().size();
    }

    @Override
    public HashSet<UUID> getExpectedPlayers()
    {
        return playersExpected;
    }


    private JoinResponse requestSoloJoin(UUID player)
    {
        JoinResponse response = new JoinResponse();
        for (IJoinHandler handler : handlerTreeMap.values())
            response = handler.requestJoin(player, response);

        if (response.isAllowed())
        {
            playersExpected.add(player);
            Bukkit.getScheduler().runTaskLater(APIPlugin.getInstance(), () -> playersExpected.remove(player), 20 * 15L);
        }
        return response;
    }

    private JoinResponse requestPartyJoin(UUID partyID, HashSet<UUID> dontMove)
    {
        UUID leader = SamaGamesAPI.get().getPartiesManager().getLeader(partyID);
        Set<UUID> members = SamaGamesAPI.get().getPartiesManager().getPlayersInParty(partyID).keySet();

        JoinResponse response = new JoinResponse();
        for (IJoinHandler handler : handlerTreeMap.values())
            response = handler.requestPartyJoin(leader, members, response);

        if (response.isAllowed())
        {
            members.removeAll(dontMove);
            for (UUID player : members)
            {
                playersExpected.add(player);
                Bukkit.getScheduler().runTaskLater(APIPlugin.getInstance(), () -> playersExpected.remove(player), 20 * 15L);
                SamaGamesAPI.get().getProxyDataManager().getProxiedPlayer(player).connect(SamaGamesAPI.get().getServerName());
            }

            new Thread(() -> {
                Jedis jedis = SamaGamesAPI.get().getBungeeResource();
                jedis.set("party:" + partyID + ":server", SamaGamesAPI.get().getServerName());
                jedis.close();
            }, "PartyUpdater").start();
        }

        return response;
    }

    public JoinResponse requestPartyJoin(UUID partyID)
    {
        return requestPartyJoin(partyID, new HashSet<>());
    }

    public JoinResponse requestJoin(UUID player)
    {
        return requestJoin(player, false);
    }

    private JoinResponse requestJoin(UUID player, boolean alreadyConnected)
    {
        UUID party = SamaGamesAPI.get().getPartiesManager().getPlayerParty(player);
        if (party != null && isPartyLimited())
        {
            if (!SamaGamesAPI.get().getPartiesManager().getLeader(party).equals(player))
            {
                JoinResponse response = new JoinResponse();
                response.disallow("Seul le chef de partie peur rejoindre un jeu.");
                return response;
            } else
            {
                HashSet<UUID> dontMove = new HashSet<>();
                if (alreadyConnected)
                    dontMove.add(player);
                return requestPartyJoin(party, dontMove);
            }
        }

        return requestSoloJoin(player);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLogin(AsyncPlayerPreLoginEvent event)
    {
        UUID player = event.getUniqueId();

        if (moderatorsExpected.contains(player)) // On traite après
            return;

        if (!playersExpected.contains(player))
        {
            JoinResponse response = requestJoin(event.getUniqueId(), true);
            if (!response.isAllowed())
            {
                event.disallow(Result.KICK_OTHER, ChatColor.RED + response.getReason());
                return;
            }
        }

        playersExpected.remove(player);

        for (IJoinHandler handler : handlerTreeMap.values())
            handler.onLogin(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if (moderatorsExpected.contains(player.getUniqueId()))
        {
            for (IJoinHandler handler : handlerTreeMap.values())
                handler.onModerationJoin(player);

            return;
        }

        for (IJoinHandler handler : handlerTreeMap.values())
            handler.finishJoin(player);

        // Enregistrement du joueur
        APIPlugin.getInstance().getExecutor().execute(() -> {
            Jedis jedis = SamaGamesAPI.get().getBungeeResource();
            jedis.sadd("connectedonserv:" + APIPlugin.getInstance().getServerName(), player.getUniqueId().toString());
            jedis.close();
        });
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event)
    {
        if (moderatorsExpected.contains(event.getPlayer().getUniqueId()))
            moderatorsExpected.remove(event.getPlayer().getUniqueId());

        for (IJoinHandler handler : handlerTreeMap.values())
            handler.onLogout(event.getPlayer());

        APIPlugin.getInstance().getExecutor().execute(() -> {
            Jedis jedis = SamaGamesAPI.get().getBungeeResource();
            jedis.srem("connectedonserv:" + APIPlugin.getInstance().getServerName(), event.getPlayer().getUniqueId().toString());
            jedis.close();
        });
    }

    public void addModerator(UUID moderator)
    {
        moderatorsExpected.add(moderator);
    }
}
