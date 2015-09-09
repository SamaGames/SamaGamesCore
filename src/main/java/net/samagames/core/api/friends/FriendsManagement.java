package net.samagames.core.api.friends;

import net.samagames.api.friends.IFriendsManager;
import net.samagames.core.ApiImplementation;

import java.util.*;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class FriendsManagement implements IFriendsManager
{

    private final ApiImplementation api;

    public FriendsManagement(ApiImplementation api)
    {
        this.api = api;
    }

    @Override
    public boolean areFriends(UUID p1, UUID p2)
    {
        return uuidFriendsList(p1).contains(p2);
    }

    @Override
    public List<String> namesFriendsList(UUID asking)
    {
        return new ArrayList<>();
    }

    @Override
    public List<UUID> uuidFriendsList(UUID asking)
    {
        return new ArrayList<>();
    }

    public Map<UUID, String> associatedFriendsList(UUID asking)
    {
        HashMap<UUID, String> ret = new HashMap<>();

        for (UUID id : uuidFriendsList(asking))
        {
            String name = api.getUUIDTranslator().getName(id, true);
            if (name == null)
            {
                continue;
            }
            ret.put(id, name);
        }
        return ret;
    }

    @Override
    public List<String> requests(UUID asking)
    {
        return new ArrayList<>();
    }

    @Override
    public List<String> sentRequests(UUID asking)
    {
        return new ArrayList<>();
    }

}
