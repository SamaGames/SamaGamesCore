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

    public Multiplier getGroupMultiplier(UUID player)
    {
        PlayerData user = api.getPlayerManager().getPlayerData(player);
        int groupMultiplier = 1;

        try {
            groupMultiplier = api.getGameServiceManager().getGroupPlayer(user.getPlayerBean()).getMultiplier();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Multiplier(groupMultiplier, 0);
    }

    public Multiplier getPromotionMultiplier( int type, int game)
    {
        Multiplier result = new Multiplier(1, 0);
        long currentTime = System.currentTimeMillis();
        for (PromotionsBean promotion : promotions)
        {
            if (promotion.getTypePromotion() == -1 || promotion.getTypePromotion() == type) //Check type (global coins or stars)
            {
                if ((promotion.getGame() == game || promotion.getGame() == -1) //Check Game number
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
        builder.append(type == 0 ? ChatColor.GOLD + "+" + amount + " pièces (" + reason + ChatColor.GOLD + ")" : ChatColor.AQUA + "+" + amount + " étoiles (" + reason + ChatColor.AQUA + ")");

        if (multiplier != null)
        {
            for (String multCause : multiplier.getCombinedData().keySet())
            {
                if (multCause == null || multiplier.getCombinedData().get(multCause) == 1)
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
