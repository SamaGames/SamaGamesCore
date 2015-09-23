package net.samagames.core.api.friends;

import com.google.common.reflect.TypeToken;
import net.samagames.api.friends.IFriendsManager;
import net.samagames.core.ApiImplementation;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.StatusResponse;

import java.util.*;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * Created by Thog
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
    public boolean areFriends(UUID from, UUID isFriend)
    {
        Object result = RestAPI.getInstance().sendRequest("player/isfriend", new Request().addProperty("playerUUID", from).addProperty("friendUUID", isFriend), StatusResponse.class, "POST");
        if (result instanceof StatusResponse)
            return ((StatusResponse) result).getStatus();
        api.getPlugin().getLogger().warning("Error in player/isfriend (" + result + ")");
        return false;
    }

    @Override
    public List<String> namesFriendsList(UUID asking)
    {
        List<String> playerNames = new ArrayList<>();

        for (UUID id : uuidFriendsList(asking))
        {
            String name = api.getUUIDTranslator().getName(id, false);
            if (name == null)
            {
                continue;
            }
            playerNames.add(name);
        }
        return playerNames;
    }

    @Override
    public List<UUID> uuidFriendsList(UUID asking)
    {
        Object friendsResponse = RestAPI.getInstance().sendRequest("player/friends", new Request().addProperty("playerUUID", asking), new TypeToken<List<UUID>>() {}.getType(), "POST");
        if (friendsResponse instanceof List)
        {
            //noinspection unchecked
            return (List<UUID>) friendsResponse;
        }
        else
            api.getPlugin().getLogger().warning("Error in player/requester (" + friendsResponse + ")");
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
        Object friendsResponse = RestAPI.getInstance().sendRequest("player/requester", new Request().addProperty("playerUUID", asking), List.class, "POST");
        if (friendsResponse instanceof List)
        {
            //noinspection unchecked
            return (List<String>) friendsResponse;
        }
        else
            api.getPlugin().getLogger().warning("Error in player/requester (" + friendsResponse + ")");
        return new ArrayList<>();
    }

    @Override
    public List<String> sentRequests(UUID asking)
    {

        Object friendsResponse = RestAPI.getInstance().sendRequest("player/requested", new Request().addProperty("playerUUID", asking), List.class, "POST");
        if (friendsResponse instanceof List)
        {
            //noinspection unchecked
            return (List<String>) friendsResponse;
        }
        else
            api.getPlugin().getLogger().warning("Error in player/requester (" + friendsResponse + ")");
        return new ArrayList<>();
    }

}
