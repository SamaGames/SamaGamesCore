package net.samagames.core.api.parties;

import net.samagames.api.pubsub.IPacketsReceiver;
import net.samagames.core.APIPlugin;

import java.util.UUID;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 08/08/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class PartyListener implements IPacketsReceiver
{

    private final PartiesManager partiesManager;
    private final APIPlugin plugin;

    public PartyListener(APIPlugin plugin, PartiesManager partiesManager)
    {
        this.plugin = plugin;
        this.partiesManager = partiesManager;
    }

    @Override
    public void receive(String channel, String message) {
        String[] parts = channel.split("\\.");
        if (parts.length < 2)
            return;

        String action = parts[1];
        String[] args = message.split(" ");

        switch (action)
        {
            case "disband":
            {
                if (args.length < 1)
                    return;

                UUID player = UUID.fromString(args[0]);
                Party partyForPlayer = partiesManager.getPartyForPlayer(player);
                if (partyForPlayer != null)
                {
                    partyForPlayer.getPlayers().clear();
                    partiesManager.unloadParties();
                }
                break;
            }
            case "join":
            case "kick":
            case "leave":
            case "lead":
            case "disconnect":
            {
                if (args.length < 2)
                    return;

                UUID party = UUID.fromString(args[0]);
                partiesManager.loadParty(party);
                //clear cache if no player
                partiesManager.unloadParties();
                break;
            }
        }
    }
}