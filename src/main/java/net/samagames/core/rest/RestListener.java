package net.samagames.core.rest;

import net.samagames.api.network.IJoinHandler;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.core.api.player.redis.RedisPlayerData;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.LoginResponse;
import net.samagames.restfull.response.Response;
import org.bukkit.Bukkit;

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
        api.setup("test", "test");
        this.pluginAPI = pluginAPI;
        playerDataManager = (PlayerDataManager) pluginAPI.getAPI().getPlayerManager();
    }
    public void onLogin(UUID player)
    {
        if (!pluginAPI.getAPI().useRestFull())
            return;
        Response response = api.sendRequest("player/login", new Request().addProperty("playerUUID", player), LoginResponse.class, "POST");
        //Bukkit.broadcastMessage(api.getGSON().toJson(response));
        if (response instanceof LoginResponse)
        {
            LoginResponse repLogin = (LoginResponse) response;
            RestPlayerData data = new RestPlayerData(player, pluginAPI.getAPI(), playerDataManager);
            data.onLogin(repLogin);
            playerDataManager.load(player, data, true);
        }
        else
            Bukkit.getLogger().warning(response.toString());
    }
}
