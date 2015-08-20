package net.samagames.core.api.player;

import net.md_5.bungee.api.ChatColor;
import net.samagames.api.SamaGamesAPI;
import net.samagames.core.ApiImplementation;
import net.samagames.permissionsapi.permissions.PermissionUser;
import net.samagames.tools.Promo;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class StarsManager
{

    private final ApiImplementation api;
    private Promo currentPromo;
    private Date promoNextCheck = null;

    public StarsManager(ApiImplementation api)
    {
        this.api = api;
    }

    public Multiplier getCurrentMultiplier(UUID joueur)
    {
        Date current = new Date();
        Multiplier ret = new Multiplier();

        if (promoNextCheck == null || current.after(promoNextCheck))
        {
            Jedis jedis = api.getResource();
            //TODO: Use RestAPI
            String prom = jedis.get("stars:currentpromo"); // On get la promo
            jedis.close();

            if (prom == null)
            {
                currentPromo = null;
            } else
            {
                currentPromo = new Promo(prom);
            }

            promoNextCheck = new Date();
            promoNextCheck.setTime(promoNextCheck.getTime() + (60 * 1000));
        }

        if (currentPromo != null && current.before(currentPromo.end))
        {
            ret.globalAmount *= currentPromo.multiply;
            ret.data.put(currentPromo.message, currentPromo.multiply);
        }

        PermissionUser user = SamaGamesAPI.get().getPermissionsManager().getApi().getUser(joueur);
        int multiply = (user != null && user.getProperty("stars-multiplier") != null) ? Integer.decode(user.getProperty("stars-multiplier")) : 1;

        multiply = (multiply < 1) ? 1 : multiply;

        ret.globalAmount *= multiply;
        if (ret.globalAmount <= 0)
            ret.globalAmount = 1;
        return ret;
    }

    public String getCreditMessage(long amount, String reason, Multiplier multiplier)
    {
        String text = ChatColor.AQUA + "+" + amount + " étoiles (" + reason + ")";

        if (multiplier != null)
        {
            for (String multCause : multiplier.data.keySet())
            {
                String causes = "";

                if (multiplier.data.containsKey(multCause))
                    causes += " *" + multiplier.data.get(multCause);

                text += " [" + causes + "]";
            }
        }

        return text;
    }
}
