package net.samagames.core.api.network;

import net.md_5.bungee.api.chat.TextComponent;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.network.JoinResponse;
import net.samagames.api.pubsub.IPacketsReceiver;

import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class PartiesPubSub implements IPacketsReceiver
{

    private final JoinManagerImplement implement;

    public PartiesPubSub(JoinManagerImplement implement)
    {
        this.implement = implement;
    }

	/*
    Protocol data :
	partyjoin <uuid of the party>
	 */

    @Override
    public void receive(String channel, String packet)
    {
        UUID partyID = UUID.fromString(packet);
        JoinResponse response = implement.requestPartyJoin(partyID);

        if (!response.isAllowed())
        {
            TextComponent component = new TextComponent("Impossible de vous connecter : " + response.getReason());
            component.setColor(net.md_5.bungee.api.ChatColor.RED);
            SamaGamesAPI.get().getPlayerManager()
                    .getPlayerData(SamaGamesAPI.get().getPartiesManager().getLeader(partyID))
                    .sendMessage(component);
        }
    }
}
