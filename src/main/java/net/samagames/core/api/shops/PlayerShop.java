package net.samagames.core.api.shops;

import net.samagames.api.shops.IPlayerShop;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.shop.TransactionBean;
import net.samagames.tools.CallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 26/04/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class PlayerShop implements IPlayerShop {

    private ApiImplementation api;
    private boolean[] shopToLoad;
    private UUID uuid;
    private List<TransactionItem> items;

    private long lastUpdate = 0;

    public PlayerShop(ApiImplementation api, boolean[] shopToLoad, UUID uuid)
    {
        this.api = api;
        this.shopToLoad = shopToLoad;
        this.items = new ArrayList<>();
        this.uuid = uuid;
    }

    @Override
    public void refresh()
    {
        refresh(false);
    }

    public void refresh(boolean force)
    {
        if (force || System.currentTimeMillis() - lastUpdate > 1000*60*5)
        {
            PlayerData playerData = api.getPlayerManager().getPlayerData(uuid);
            List<TransactionItem> items = new ArrayList<>();
            for (int i = 0; i < shopToLoad.length; i++)
            {
                if (shopToLoad[i])
                {
                    try {
                        List<TransactionBean> transactionBeen = api.getGameServiceManager().getPlayerGameSelectedTransactions(playerData.getPlayerBean(), i);
                        items.addAll(transactionBeen.stream().map(bean -> (TransactionItem) bean).collect(Collectors.toList()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            this.items = items;
            lastUpdate = System.currentTimeMillis();
        }
    }

    @Override
    public void addItem(int itemID, int priceCoins, int priceStars, boolean selected)
    {
        addItem(itemID, priceCoins, priceStars, selected, null);
    }

    @Override
    public void addItem(int itemID, int priceCoins, int priceStars, boolean selected, CallBack<Boolean> callBack)
    {
        PlayerData playerData = api.getPlayerManager().getPlayerData(uuid);
        TransactionItem transactionItem = new TransactionItem(0, itemID, priceCoins, priceStars, null, selected, uuid);

        //Directly update in base for security
        api.getPlugin().getExecutor().execute(() -> {
            try {
                api.getGameServiceManager().createTransaction(playerData.getPlayerBean(), transactionItem);
                //May be optimised
                refresh(true);
                if (callBack != null)
                    callBack.done(true, null);
            } catch (Exception e) {
                if (callBack != null)
                    callBack.done(false, e);
            }
        });
    }

    @Override
    public void setSelectedItem(int itemID, boolean selected) throws Exception {
        //Auto refresh if more than 5min
        refresh();

        //Cache
        TransactionItem transactionItem = getTransactionsByID(itemID);
        if (transactionItem == null)
        {
            throw new Exception("Item with id: " + itemID + " not found");
        }
        transactionItem.setSelected(selected);

        //Directly update in base for security
        api.getPlugin().getExecutor().execute(() -> {
            try {
                api.getGameServiceManager().updateTransaction(transactionItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean isSelectedItem(int itemID) throws Exception {
        //Cache
        TransactionItem transactionItem = getTransactionsByID(itemID);
        if (transactionItem == null)
        {
            throw new Exception("Item with id: " + itemID + " not found");
        }
        return transactionItem.isSelected();
    }


    @Override
    public TransactionItem getTransactionsByID(int itemID)
    {
        //Auto refresh if more than 5min
        refresh();
        for (TransactionItem item : items)
        {
            if (item.getItemId() == itemID)
            {
                return item;
            }
        }
        //No item bought
        return null;
    }
}
