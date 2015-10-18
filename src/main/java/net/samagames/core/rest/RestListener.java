package net.samagames.core.rest;

import net.samagames.api.network.IJoinHandler;
import net.samagames.api.permissions.rawtypes.IManager;
import net.samagames.api.permissions.restfull.RestfullManager;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.LoginResponse;
import net.samagames.restfull.response.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class RestListener implements IJoinHandler
{
    private final PlayerDataManager playerDataManager;
    private final APIPlugin pluginAPI;
    private RestAPI api;
    public RestListener(APIPlugin pluginAPI)
    {
        api = RestAPI.getInstance();
        this.pluginAPI = pluginAPI;
        playerDataManager = (PlayerDataManager) pluginAPI.getAPI().getPlayerManager();
    }

    @Override
    public void onLogin(UUID player, String username)
    {
        playerDataManager.load(player, new RestPlayerData(player, pluginAPI.getAPI(), playerDataManager), true);
        IManager permissionManager = pluginAPI.getAPI().getPermissionsManager().getApi().getManager();
        if (permissionManager instanceof RestfullManager)
            ((RestfullManager) permissionManager).loadUser(player);
    }
}
