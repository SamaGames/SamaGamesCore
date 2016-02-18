package net.samagames.core.api.network;

import com.google.gson.Gson;
import net.md_5.bungee.api.chat.TextComponent;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.network.IProxiedPlayer;
import net.samagames.api.player.AbstractPlayerData;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.core.rest.RestPlayerData;

import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Silvanosky
 * (C) Copyright Elydra Network 2016
 * All rights reserved.
 */
class ProxiedPlayer implements IProxiedPlayer
{

    private final UUID playerId;
    private AbstractPlayerData playerData;

    public ProxiedPlayer(UUID playerId)
    {
        this.playerData = SamaGamesAPI.get().getPlayerManager().getPlayerData(playerId);
        if (this.playerData == null)
            this.playerData = new RestPlayerData(playerId, APIPlugin.getInstance().getAPI(), (PlayerDataManager) APIPlugin.getInstance().getAPI().getPlayerManager());
        this.playerId = playerId;
    }

    @Override
    public String getServer()
    {
        //Todo redis implement
        return playerData.get("redis.server", "Inconnu");
    }

    @Override
    public String getProxy()
    {
        //Todo redis implement
        return playerData.get("redis.proxy", "Inconnu");
    }

    @Override
    public String getIp()
    {
        //Todo redis implement
        return playerData.get("redis.ip", "0.0.0.0");
    }

    @Override
    public UUID getUUID()
    {
        return playerId;
    }

    @Override
    public String getName()
    {
        return SamaGamesAPI.get().getUUIDTranslator().getName(playerId);
    }

    @Override
    public void disconnect(TextComponent reason)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.kick", playerId + " " + new Gson().toJson(reason));
    }

    @Override
    public void connect(String server)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.connect", playerId + " " + server);
    }

    @Override
    public void sendMessage(TextComponent component)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.message", playerId + " " + new Gson().toJson(component));
    }
}
