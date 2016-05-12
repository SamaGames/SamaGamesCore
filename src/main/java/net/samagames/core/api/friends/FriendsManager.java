package net.samagames.core.api.friends;

import net.samagames.api.friends.IFriendsManager;
import net.samagames.core.ApiImplementation;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * Created by Silvanosky
 * All rights reserved.
 */
public class FriendsManager implements IFriendsManager
{
    private final ApiImplementation api;

    private HashMap<UUID, FriendPlayer> cache;

    private static final String key = "friends:";

    public FriendsManager(ApiImplementation api)
    {
        this.api = api;
        this.cache = new HashMap<>();
    }

    public void loadPlayer(UUID player)
    {
        FriendPlayer friendPlayer = new FriendPlayer(player);
        Jedis jedis = api.getBungeeResource();
        Set<String> smembers = jedis.smembers(key + player);
        jedis.close();
        for (String friend : smembers)
        {
            friendPlayer.addFriend(UUID.fromString(friend));
        }
    }

    public void unloadPlayer(UUID player)
    {
        //We don't edit data here so remove cache (bungee side)
        cache.remove(player);
    }

    @Override
    public boolean areFriends(UUID from, UUID isFriend)
    {
        if (cache.containsKey(from))
        {
            return cache.get(from).areFriend(isFriend);
        }else
        {
            return false;
        }
    }

    @Override
    public List<String> namesFriendsList(UUID asking)
    {
        if (cache.containsKey(asking))
        {
            List<String> names = new ArrayList<>();
            FriendPlayer friendPlayer = cache.get(asking);

            for (UUID uuid : friendPlayer.getFriends())
            {
                names.add(api.getUUIDTranslator().getName(uuid, false));
            }

            return names;
        }else
        {
            //TODO update player
            return null;
        }
    }

    @Override
    public List<UUID> uuidFriendsList(UUID asking)
    {
        if (cache.containsKey(asking))
        {
            FriendPlayer friendPlayer = cache.get(asking);

            return friendPlayer.getFriends();
        }else
        {
            //TODO update player
            return null;
        }
    }

    public Map<UUID, String> associatedFriendsList(UUID asking)
    {
        if (cache.containsKey(asking))
        {
            HashMap<UUID, String> names = new HashMap<>();
            FriendPlayer friendPlayer = cache.get(asking);

            for (UUID uuid : friendPlayer.getFriends())
            {
                String name;
                if((name = api.getUUIDTranslator().getName(uuid, false)) != null)
                {
                    continue;
                }

                names.put(uuid, name);
            }

            return names;
        }else
        {
            //TODO update player
            return null;
        }
    }

    public FriendPlayer getFriendPlayer(UUID uuid)
    {
        return cache.get(uuid);
    }

    @Override
    public List<String> requests(UUID asking)
    {
        //Bungee side
        return null;
    }

    @Override
    public List<String> sentRequests(UUID asking)
    {
        //Bungee side
        return null;
    }

    @Override
    public boolean removeFriend(UUID asking, UUID target)
    {
        //Bungee side
        return false;
    }

}
