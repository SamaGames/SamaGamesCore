package net.samagames.core.api.friends;

import net.samagames.api.friends.IFriendsManager;
import net.samagames.api.player.AbstractPlayerData;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.FriendshipBean;

import java.util.*;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * Created by Silvanosky
 * All rights reserved.
 */
public class FriendsManagement implements IFriendsManager
{

    private final ApiImplementation api;

    private HashMap<UUID, FriendPlayer> cache;

    public FriendsManagement(ApiImplementation api)
    {
        this.api = api;
        this.cache = new HashMap<>();
    }

    public void loadFriends(UUID player)
    {
        PlayerData playerData = (PlayerData) api.getPlayerManager().getPlayerData(player);
        ArrayList<FriendshipBean> friendshipList = api.getGameServiceManager().getFriendshipList(playerData.getPlayerBean());
        //FriendPlayer friendPlayer = new FriendPlayer(player)
        //TODO do with satch because brain fuck
    }

    @Override
    public boolean areFriends(UUID from, UUID isFriend)
    {
        if (cache.containsKey(from))
        {
            FriendPlayer friendPlayer = cache.get(from);
            return friendPlayer.areFriend(isFriend);
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

    @Override
    public List<String> requests(UUID asking)
    {
        return null;
    }

    @Override
    public List<String> sentRequests(UUID asking)
    {
        //TODO get request from player
        return null;
    }

    @Override
    public boolean removeFriend(UUID asking, UUID target)
    {
        //TODO update player in db and cache
        return false;
    }

}
