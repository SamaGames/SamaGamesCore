package net.samagames.core.api.shops;

import net.samagames.api.games.GamesNames;
import net.samagames.api.shops.IShopsManager;
import net.samagames.core.ApiImplementation;
import net.samagames.persistanceapi.beans.shop.ItemDescriptionBean;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

    private ItemDescription[] itemsCache;
    private ConcurrentHashMap<UUID, PlayerShop> cache;
    private ApiImplementation api;

    public ShopsManager(ApiImplementation api)
    {
        this.api = api;
        this.cache = new ConcurrentHashMap<>();
        this.itemsCache = new ItemDescription[0];

        this.shopToLoad = new boolean[GamesNames.values().length];
        for (int i = 0; i < shopToLoad.length; i++)
        {
            shopToLoad[i] = api.getPlugin().isHub();
        }

        // load all item desc and refresh every 5 min
        api.getPlugin().getExecutor().scheduleAtFixedRate(() -> {
            try
            {
                List<ItemDescriptionBean> allItemDescription = api.getGameServiceManager().getAllItemDescription();
                int n = allItemDescription.size();

                itemsCache = new ItemDescription[Math.max(n, allItemDescription.get(n-1).getItemId())];
                for (ItemDescriptionBean bean : allItemDescription)
                {
                    itemsCache[bean.getItemId()] = new ItemDescription(bean);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.MINUTES);
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
            //playerShop.update();
        }
        cache.remove(player);
    }

    @Override
    public void setShopToLoad(GamesNames game, boolean value)
    {
        shopToLoad[game.intValue()] = value;
    }

    @Override
    public boolean isShopLoading(GamesNames game)
    {
        return shopToLoad[game.intValue()];
    }

    @Override
    public ItemDescription getItemDescription(int itemID) throws Exception {
        try {
            return itemsCache[itemID];
        } catch (Exception e) {
            throw new Exception("Item with id: " + itemID + " not found");
        }
    }

    @Override
    public ItemDescription getItemDescriptionByName(String itemName) throws Exception {
        for (ItemDescription description : itemsCache)
        {
            if (description.getItemName().equals(itemName))
            {
                return description;
            }
        }
        throw new Exception("Item with name: " + itemName + " not found");
    }

    @Override
    public PlayerShop getPlayer(UUID player)
    {
        return cache.get(player);
    }
}
