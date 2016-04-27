package net.samagames.core.listeners.general;

import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import org.bukkit.event.Listener;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
abstract class APIListener implements Listener
{
    final APIPlugin plugin;
    final ApiImplementation api;

    APIListener(APIPlugin plugin)
    {
        this.plugin = plugin;
        this.api = plugin.getAPI();
    }
}
