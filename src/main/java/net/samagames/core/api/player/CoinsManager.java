package net.samagames.core.api.player;

import net.md_5.bungee.api.ChatColor;
import net.samagames.core.ApiImplementation;
import net.samagames.api.permissions.permissions.PermissionUser;
import net.samagames.tools.Promo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class CoinsManager
{

    private final ApiImplementation api;
    private Promo currentPromo;
    private Date promoNextCheck;
    private List<Multiplier> caches = new ArrayList<>();

    public CoinsManager(ApiImplementation api)
    {
        this.api = api;
    }

    public Multiplier getCurrentMultiplier(UUID player, String type)
    {
        Date current = new Date();
        Multiplier ret = new Multiplier();

        if (promoNextCheck == null || current.after(promoNextCheck))
        {
            // TODO: Rewrite the whole system to manage multi offer and keep it compatible with DB mode
        }

        if (currentPromo != null && current.before(currentPromo.end))
        {
            ret.globalAmount *= currentPromo.multiply;
            ret.data.put(currentPromo.message, currentPromo.multiply);
        }

        PermissionUser user = api.getPermissionsManager().getApi().getUser(player);
        int multiply = (user != null && user.getProperty("multiplier") != null) ? Integer.decode(user.getProperty("multiplier")) : 1;

        multiply = (multiply < 1) ? 1 : multiply;

        ret.globalAmount *= multiply;
        if (ret.globalAmount <= 0)
            ret.globalAmount = 1;
        return ret;
    }

    public String getCreditMessage(long amount, String reason, Multiplier multiplier)
    {
        String text = ChatColor.GOLD + "+" + amount + " pièces (" + reason + ")";

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
