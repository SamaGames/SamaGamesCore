package net.samagames.core;

import net.samagames.api.pubsub.IPacketsReceiver;
import org.bukkit.Bukkit;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2016 & 2017
 * All rights reserved.
 */
class RemoteCommandsHandler implements IPacketsReceiver
{
    @Override
    public void receive(String channel, String command)
    {
        Bukkit.getLogger().info("Executing command remotely : " + command);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
