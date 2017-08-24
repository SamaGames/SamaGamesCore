package net.samagames.core.api.player;

import net.md_5.bungee.api.ChatColor;
import net.samagames.core.ApiImplementation;
import net.samagames.persistanceapi.beans.shop.PromotionsBean;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * This file is part of SamaGamesCore.
 *
 * SamaGamesCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesCore.  If not, see <http://www.gnu.org/licenses/>.
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
            groupMultiplier = api.getGameServiceManager().getPlayerGroup(user.getPlayerBean()).getMultiplier();
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
            if (promotion.getPromotionType() == -1 || promotion.getPromotionType() == type) //Check type (global coins or stars)
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
