package net.samagames.core.api.shops;

import net.samagames.api.shops.ITransaction;
import net.samagames.persistanceapi.beans.shop.TransactionBean;

import java.sql.Timestamp;
import java.util.UUID;

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
public class TransactionItem extends TransactionBean implements ITransaction {


    //Only to link interface from API
    public TransactionItem(long transactionId, int item_id, int priceCoins,
                           int priceStars, Timestamp transactionDate,
                           boolean selected, UUID uuidBuyer) {
        super(transactionId, item_id, priceCoins,
                priceStars, transactionDate, selected, uuidBuyer);
    }


}
