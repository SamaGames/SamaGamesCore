package net.samagames.core.api.player;

import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.ChatColor;
import net.samagames.api.permissions.permissions.PermissionUser;
import net.samagames.core.ApiImplementation;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.elements.PromotionResponseElement;
import org.bukkit.Bukkit;

import java.util.*;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class EconomyManager
{
    private final ApiImplementation api;
    private Map<String, Multiplier> starsMultiplier;
    private Map<String, Multiplier> coinsMultiplier;

    public EconomyManager(ApiImplementation api)
    {
        this.api = api;
        this.coinsMultiplier = new HashMap<>();
        this.starsMultiplier = new HashMap<>();
    }

    public void reload()
    {
        this.internalGenericReload("coins", coinsMultiplier);
        this.internalGenericReload("stars", starsMultiplier);
    }

    private final void internalGenericReload(String type, Map<String, Multiplier> data)
    {
        Object result = RestAPI.getInstance().sendRequest("promotion/" + type, new Request(), new TypeToken<List<PromotionResponseElement>>() {}.getType(), "POST");
        if (!(result instanceof PromotionResponseElement))
        {
            Bukkit.getLogger().warning("Error during stars discount reload (" + result + ")");
        }

        Map<String, Multiplier> newData = new HashMap<>();
        List<PromotionResponseElement> rawData = (List<PromotionResponseElement>) result;
        for (PromotionResponseElement element : rawData)
        {
            // Security if something is break in the API
            if (element.getEnd() == null)
                continue;

            Multiplier multiplier = new Multiplier(element.getMultiplier(), element.getEnd().getTime(), element.getMessage());

            // Security if something is break in the API
            if (element.getType() != type || !multiplier.isValid())
                continue;
            if (newData.containsKey(element.getGame()))
            {
                Bukkit.getLogger().warning("Duplicate entry for game " + element.getGame() + ", type " + element.getType() + "! Ignore it...");
                continue;
            }
            newData.put(element.getGame() != null ? element.getGame() : "global", multiplier);
        }

        data.clear();
        data.putAll(newData);
    }

    public Multiplier getCurrentMultiplier(UUID player, String type, String game)
    {
        PermissionUser user = api.getPermissionsManager().getApi().getUser(player);
        int groupMultiplier = (user != null && user.getProperty("multiplier") != null) ? Integer.parseInt(user.getProperty("multiplier")) : 1;
        Multiplier result = new Multiplier(1, 0);
        Map<String, Multiplier> data = null;
        switch (type)
        {
            case "coins":
                data = coinsMultiplier;
                break;
            case "stars":
                data = starsMultiplier;
                break;
            default:
                break;
        }

        if (data != null && !data.isEmpty())
        {
            Multiplier globalDiscount = data.get("global");
            Multiplier gameDiscount = data.get(game);

            if (globalDiscount != null && globalDiscount.isValid())
                result = result.cross(globalDiscount);
            if (gameDiscount != null && gameDiscount.isValid())
                result = result.cross(gameDiscount);
        }

        return result.cross(groupMultiplier);
    }

    public String getCreditMessage(long amount, String type, String reason, Multiplier multiplier)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(type.equals("coins") ? ChatColor.GOLD + "+" + amount + " pièces (" + reason + ")" : ChatColor.AQUA + "+" + amount + " étoiles (" + reason + ")");

        if (multiplier != null)
        {
            for (String multCause : multiplier.getCombinedData().keySet())
            {
                if(multCause.isEmpty())
                {
                    builder.append(" [ *" + multiplier.getCombinedData().get(multCause) + "]");
                }
                else
                {
                    builder.append(" [ *").append(multiplier.getCombinedData().get(multCause)).append(" ").append(multCause).append("]");
                }
            }
        }

        return builder.toString();
    }
}
