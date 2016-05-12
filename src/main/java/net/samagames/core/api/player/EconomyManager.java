package net.samagames.core.api.player;

import net.md_5.bungee.api.ChatColor;
import net.samagames.core.ApiImplementation;
import net.samagames.persistanceapi.beans.shop.PromotionsBean;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Silvanosky
 * (C) Copyright Elydra Network 2016 & 2017
 * All rights reserved.
 */
public class EconomyManager
{
    private final ApiImplementation api;
    private List<PromotionsBean> promotions;

    private final BukkitTask discountTask;

    public EconomyManager(ApiImplementation api)
    {
        this.api = api;
        this.promotions = new ArrayList<>();

        // Run task every 30 minutes
        discountTask = api.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(this.api.getPlugin(), this::reload, 0L, 36000L);
    }

    public void reload()
    {
        promotions.clear();
        try {
            promotions.addAll(api.getGameServiceManager().getAllActivePromotions());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Multiplier getCurrentMultiplier(UUID player, int type, int game)
    {
        long currentTime = System.currentTimeMillis();

        //TODO cache and get promotions
        PlayerData user = api.getPlayerManager().getPlayerData(player);
        int groupMultiplier = 0;
        try {
            groupMultiplier = api.getGameServiceManager().getGroupPlayer(user.getPlayerBean()).getMultiplier();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Multiplier result = new Multiplier(groupMultiplier, 0);
        for (PromotionsBean promotion : promotions)
        {
            if (promotion.getTypePromotion() == 0 || promotion.getTypePromotion() == type) //Check type (global coins or stars)
            {
                if (promotion.getGame() == game //Check Game number
                    && promotion.getStartDate().getTime() < currentTime
                    && promotion.getEndDate().getTime() > currentTime)
                {
                    Multiplier multiplier = new Multiplier(promotion.getMultiplier(),
                            promotion.getEndDate().getTime(),
                            promotion.getMessage());

                    result.cross(multiplier);
                }
            }
        }

        return result;
    }

    public String getCreditMessage(long amount, int type, String reason, Multiplier multiplier)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(type == 1 ? ChatColor.GOLD + "+" + amount + " pièces (" + reason + ")" : ChatColor.AQUA + "+" + amount + " étoiles (" + reason + ")");

        if (multiplier != null)
        {
            for (String multCause : multiplier.getCombinedData().keySet())
            {
                if (multiplier.getCombinedData().get(multCause) == 1)
                    continue;

                if(multCause.isEmpty())
                {
                    builder.append(" [* " + multiplier.getCombinedData().get(multCause) + "]");
                }
                else
                {
                    builder.append(" [* ").append(multiplier.getCombinedData().get(multCause)).append(" ").append(multCause).append("]");
                }
            }
        }

        return builder.toString();
    }

    public void onShutdown()
    {
        discountTask.cancel();
    }
}
