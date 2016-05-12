package net.samagames.core.api.network;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.network.JoinResponse;
import net.samagames.api.pubsub.IPacketsReceiver;

import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 *
 * (C) Copyright Elydra Network 2016
 * All rights reserved.
 */
public class RegularJoinHandler implements IPacketsReceiver
{

    private final JoinManagerImplement manager;

    public RegularJoinHandler(JoinManagerImplement manager)
    {
        this.manager = manager;
    }

    @Override
    public void receive(String channel, String packet)
    {
        UUID player = UUID.fromString(packet);
        JoinResponse response = manager.requestJoin(player, false);
        if (!response.isAllowed())
        {
            TextComponent component = new TextComponent(response.getReason());
            component.setColor(ChatColor.RED);
            SamaGamesAPI.get().getPlayerManager().sendMessage(player, component);
        } else
        {
            SamaGamesAPI.get().getPlayerManager().connectToServer(player, SamaGamesAPI.get().getServerName());
            SamaGamesAPI.get().getGameManager().refreshArena();
        }
    }
}
