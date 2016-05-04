package net.samagames.core.api.shops;

import net.samagames.api.shops.IItemDescription;
import net.samagames.persistanceapi.beans.shop.ItemDescriptionBean;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 03/05/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */

public class ItemDescription extends ItemDescriptionBean implements IItemDescription {

    //Only to hide in api

    public ItemDescription(int itemId, String itemName, String itemDesc,
                           int priceCoins, int priceStars, int gameCategory,
                           String itemMinecraftId, String itemRarity, int rankAccessibility) {
        super(itemId, itemName, itemDesc, priceCoins, priceStars, gameCategory,
                itemMinecraftId, itemRarity, rankAccessibility);
    }

}
