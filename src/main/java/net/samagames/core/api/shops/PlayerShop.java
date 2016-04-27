package net.samagames.core.api.shops;

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
public class PlayerShop {

    private ApiImplementation api;
    private boolean[] shopToLoad;
    private UUID uuid;
    private List<TransactionItem> items;

    public PlayerShop(ApiImplementation api, boolean[] shopToLoad, UUID uuid)
    {
        this.api = api;
        this.shopToLoad = shopToLoad;
        this.items = new ArrayList<>();
        this.uuid = uuid;
    }

    public void refresh()
    {
        PlayerData playerData = api.getPlayerManager().getPlayerData(uuid);
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
    }

    public void update()
    {
        //Nothing to save yet
    }

    public boolean addItem(TransactionItem item)
    {
        PlayerData playerData = api.getPlayerManager().getPlayerData(uuid);
        try {
            api.getGameServiceManager().createTransaction(playerData.getPlayerBean(),
                    item);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
