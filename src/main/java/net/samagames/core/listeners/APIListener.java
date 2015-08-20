package net.samagames.core.listeners;

import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
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
    final SamaGamesAPI api;

    APIListener(APIPlugin plugin)
    {
        this.plugin = plugin;
        api = plugin.getAPI();
    }
}
