package net.samagames.core.api.shops;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.shops.AbstractShopsManager;

import java.util.List;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Silvanoksy
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
//TODO shop when ready in persistance api
public class ShopsManager extends AbstractShopsManager
{
    public ShopsManager(String gameType, SamaGamesAPI api)
    {
        super(gameType, api);
    }

    @Override
    public String getItemLevelForPlayer(UUID player, String itemCategory)
    {
        // FIXME
        return null;
    }

    @Override
    public List<String> getOwnedLevels(UUID player, String itemCategory)
    {
        //FixMe
        return null;
    }

    @Override
    public void addOwnedLevel(UUID player, String itemCategory, String itemName)
    {
        //FIXME
    }

    @Override
    public void setCurrentLevel(UUID player, String itemCategory, String itemName)
    {
        //FixMe
    }

    @Override
    public void resetLevel(UUID player, String itemCategory)
    {
        //FixME
    }
}
