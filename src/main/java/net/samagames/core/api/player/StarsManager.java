package net.samagames.core.api.player;

import net.md_5.bungee.api.ChatColor;
import net.samagames.api.permissions.permissions.PermissionUser;
import net.samagames.core.ApiImplementation;
import net.samagames.tools.Promo;

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
    private Date promoNextCheck;

    public StarsManager(ApiImplementation api)
    {
        this.api = api;
    }

    public Multiplier getCurrentMultiplier(UUID player, String type)
    {
        Date current = new Date();
        Multiplier ret = new Multiplier();

        if(type != null)
        {
            // TODO: Rest promo type of game
        }

        // TODO: Set currentPromo to other value than null

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
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.AQUA + "+" + amount + " étoiles (" + reason + ")");

        if (multiplier != null)
        {
            for (String multCause : multiplier.data.keySet())
            {
                if(multCause.equals(""))
                {
                    builder.append(" [*" + multiplier.data.get(multCause) + "]");
                }
                else
                {
                    String causes = multCause;

                    if (multiplier.data.containsKey(multCause))
                        causes = " *" + multiplier.data.get(multCause);

                    builder.append(" [" + causes + "]");
                }
            }
        }

        return builder.toString();
    }
}
