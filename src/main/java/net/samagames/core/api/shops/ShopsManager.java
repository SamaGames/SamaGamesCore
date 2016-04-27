package net.samagames.core.api.shops;

import net.samagames.api.shops.IShopsManager;
import net.samagames.api.stats.IStatsManager;
import net.samagames.core.ApiImplementation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Silvanoksy
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
//TODO
public class ShopsManager implements IShopsManager
{
    private boolean[] shopToLoad;

    private ConcurrentHashMap<UUID, PlayerShop> cache;
    private ApiImplementation api;

    public ShopsManager(ApiImplementation api)
    {
        this.api = api;
        this.cache = new ConcurrentHashMap<>();

        this.shopToLoad = new boolean[IStatsManager.StatsNames.values().length];
        for (int i = 0; i < shopToLoad.length; i++)
        {
            shopToLoad[i] = false;
        }
    }

    public void loadPlayer(UUID player)
    {
        PlayerShop playerShop = new PlayerShop(api, shopToLoad, player);
        playerShop.refresh();
        cache.put(player, playerShop);
    }

    public void unloadPlayer(UUID player)
    {
        PlayerShop playerShop = cache.get(player);
        if (playerShop != null)
        {
            playerShop.update();
        }
        cache.remove(player);
    }

    public void setShopToLoad(IStatsManager.StatsNames game, boolean value)
    {
        shopToLoad[game.intValue()] = value;
    }

    public boolean isShopLoading(IStatsManager.StatsNames game)
    {
        return shopToLoad[game.intValue()];
    }

    public void getItemDescription(int itemID) throws Exception {
        /*ItemDescriptionBean itemDescription = api.getGameServiceManager().getItemDescription(itemID);
        itemDescription.*/
    }

    @Override
    public String getItemLevelForPlayer(UUID player, String item) {
        return null;
    }

    @Override
    public List<String> getOwnedLevels(UUID player, String item) {
        return null;
    }

    @Override
    public void addOwnedLevel(UUID player, String item, String itemLevel) {

    }

    @Override
    public void setCurrentLevel(UUID player, String item, String level) {

    }

    @Override
    public void resetLevel(UUID uuid, String item) {

    }
}
