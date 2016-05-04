package net.samagames.core.api.shops;

import net.samagames.api.shops.IPlayerShop;
import net.samagames.api.shops.ITransaction;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.shop.TransactionBean;

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
        if (System.currentTimeMillis() - lastUpdate > 1000*60*5)
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

    public void update()
    {

        //Nothing to save yet we update every time
    }

    @Override
    public boolean addItem(ITransaction item)
    {
        PlayerData playerData = api.getPlayerManager().getPlayerData(uuid);
        //Directly update in base for security
        api.getPlugin().getExecutor().execute(() -> {
            try {
                api.getGameServiceManager().createTransaction(playerData.getPlayerBean(),
                        (TransactionBean) item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //Cache
        items.add((TransactionItem) item);

        return true;
    }

    @Override
    public List<ITransaction> getTransactionsByID(int itemID)
    {
        //Auto refresh if more than 5min
        refresh();
        return items.stream().filter(item -> item.getItem_id() == itemID).collect(Collectors.toList());
    }

    @Override
    public TransactionItem getTransactionSelectedByID(int itemID)
    {
        //Auto refresh if more than 5min
        refresh();
        for (TransactionItem item : items)
        {
            if (item.getItem_id() == itemID && item.isSelected())
            {
                return item;
            }
        }
        //No item selected
        return null;
    }
}
