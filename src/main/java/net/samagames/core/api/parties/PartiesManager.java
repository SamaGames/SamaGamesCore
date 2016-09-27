package net.samagames.core.api.parties;

import net.samagames.api.parties.IPartiesManager;
import net.samagames.api.parties.IParty;
import net.samagames.core.ApiImplementation;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Silvanosky
 * (C) Copyright Elydra Network 2016
 * All rights reserved.
 */
public class PartiesManager implements IPartiesManager
{

    private final ApiImplementation api;
    private HashMap<UUID, Party> parties;

    public PartiesManager(ApiImplementation api)
    {
        this.parties = new HashMap<>();
        this.api = api;
    }

    //TODO add to listerner before join
    public void loadPlayer(UUID player)
    {
        //TODO create partie if not already
        try{
            Party party = getPartyForPlayer(player);

            if (party == null)
            {
                Jedis jedis = api.getBungeeResource();
                if (!jedis.exists("currentparty:" + player))
                {
                    jedis.close();
                    return;
                }
                UUID partieID = UUID.fromString(jedis.get("currentparty:" + player));
                loadParty(partieID);
                jedis.close();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void loadParty(UUID party)
    {
        Jedis jedis = api.getBungeeResource();
        String leader = jedis.get("party:" + party + ":lead");
        Map<String, String> data = jedis.hgetAll("party:" + party + ":members");
        jedis.close();
        if (leader == null)
            return;

        Party partie = new Party(party, UUID.fromString(leader), data.keySet().stream().map(UUID::fromString).collect(Collectors.toList()));
        parties.put(party, partie);
    }

    public void unloadPlayer(UUID player)
    {
        Party party = getPartyForPlayer(player);
        if (party != null)
        {
            unloadParties();
        }
    }

    //Check all in case of dead party
    public void unloadParties()
    {
        for (Party party : new ArrayList<>(parties.values()))
        {
            int online = 0;
            //Check si tous les joueurs se sont deconnecter
            for (UUID players : party.getPlayers())
            {
                if (Bukkit.getPlayer(players) != null)
                {
                    online++;
                }
            }
            if (online == 0)
            {
                parties.remove(party.getParty());
            }
        }
    }

    @Override
    public List<UUID> getPlayersInParty(UUID party)
    {
        Party party1 = getParty(party);
        if (party1 != null)
        {
            return party1.getPlayers();
        }else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getCurrentServer(UUID party)
    {
        Jedis jedis = api.getBungeeResource();
        String server = jedis.get("party:" + party + ":server");
        jedis.close();
        return server;
    }

    @Override
    public UUID getLeader(UUID party)
    {
        Party partie = getParty(party);
        return (partie != null) ? partie.getLeader() : null;
    }

    @Override
    public Party getParty(UUID partie)
    {
        //TODO load if not
        return parties.get(partie);
    }

    @Override
    public Party getPartyForPlayer(UUID player)
    {
        for (Party party : parties.values())
        {
            if (party.containsPlayer(player))
            {
                return party;
            }
        }
        return null;
    }

    @Override
    public HashMap<UUID, IParty> getParties() {
        return new HashMap<>(parties);
    }
}
