package net.samagames.core.api.shops;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.shops.AbstractShopsManager;
import net.samagames.core.rest.RestPlayerData;
import net.samagames.restfull.response.ValueResponse;
import net.samagames.restfull.response.elements.ShopElement;

import java.util.List;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */

public class ShopsManager extends AbstractShopsManager
{
    public ShopsManager(String gameType, SamaGamesAPI api)
    {
        super(gameType, api);
    }

    @Override
    public String getItemLevelForPlayer(UUID player, String itemCategory)
    {
        ValueResponse value = ((RestPlayerData)api.getPlayerManager().getPlayerData(player)).getEquipped(gameType, itemCategory);
        if (value.getValue() == null || value.getValue().equals("false"))
            return null;
        return value.getValue();
    }

    @Override
    public List<String> getOwnedLevels(UUID player, String itemCategory)
    {
        ShopElement value = ((RestPlayerData)api.getPlayerManager().getPlayerData(player)).getShopData(gameType, itemCategory);
        if (value == null)
            return null;
        return value.getValue();
    }

    @Override
    public void addOwnedLevel(UUID player, String itemCategory, String itemName)
    {
        ((RestPlayerData)api.getPlayerManager().getPlayerData(player)).setShopData(gameType, itemCategory, itemName);
    }

    @Override
    public void setCurrentLevel(UUID player, String itemCategory, String itemName)
    {
        ((RestPlayerData)api.getPlayerManager().getPlayerData(player)).setEquipped(gameType, itemCategory, itemName);
    }

    @Override
    public void resetLevel(UUID player, String itemCategory)
    {
        ((RestPlayerData)api.getPlayerManager().getPlayerData(player)).resetEquipped(gameType, itemCategory);
    }
}
