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
    public void onLogin(UUID player)
    {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        Response response = (Response) api.sendRequest("player/login", new Request().addProperty("playerUUID", player).addProperty("playerName", bukkitPlayer == null ? "null" : bukkitPlayer.getName()), LoginResponse.class, "POST");
        //Bukkit.broadcastMessage(api.getGSON().toJson(response));
        if (response instanceof LoginResponse)
        {
            LoginResponse repLogin = (LoginResponse) response;
            RestPlayerData data = new RestPlayerData(player, pluginAPI.getAPI(), playerDataManager);
            data.onLogin(repLogin);
            playerDataManager.load(player, data, true);
            IManager permissionManager = pluginAPI.getAPI().getPermissionsManager().getApi().getManager();
            if (permissionManager instanceof RestfullManager)
            {
                ((RestfullManager) permissionManager).loadUser(repLogin);
            }
        }
        else
            Bukkit.getLogger().warning(response.toString());
    }
}
