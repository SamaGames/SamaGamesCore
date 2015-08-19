package net.samagames.core.rest.response;

import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class LoginResponse extends Response
{
    private UUID uuid;
    private String[] groups;
    private String[] permissions;
    private int stars;
    private int coins;

    public UUID getUuid()
    {
        return uuid;
    }

    public int getCoins()
    {
        return coins;
    }

    public int getStars()
    {
        return stars;
    }

    public String[] getGroups()
    {
        return groups;
    }

    public String[] getPermissions()
    {
        return permissions;
    }
}
