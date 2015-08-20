package net.samagames.core.api.gui;

import net.samagames.api.gui.AbstractGui;
import net.samagames.api.gui.IGuiManager;
import net.samagames.core.APIPlugin;
import net.samagames.core.listeners.GuiListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 10/08/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class GuiManager implements IGuiManager
{

    private final ConcurrentHashMap<UUID, AbstractGui> currentGUIs;

    public GuiManager(APIPlugin plugin)
    {
        this.currentGUIs = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(new GuiListener(this), plugin);
    }

    public void openGui(Player player, AbstractGui gui)
    {
        if (this.currentGUIs.containsKey(player.getUniqueId()))
            this.closeGui(player);

        this.currentGUIs.put(player.getUniqueId(), gui);
        gui.display(player);
    }

    public void closeGui(Player player)
    {
        player.closeInventory();
        this.removeClosedGui(player);
    }

    public void removeClosedGui(Player player)
    {
        if (this.currentGUIs.containsKey(player.getUniqueId()))
        {
            this.getPlayerGui(player).onClose(player);
            this.currentGUIs.remove(player.getUniqueId());
        }
    }

    public AbstractGui getPlayerGui(HumanEntity player)
    {
        return getPlayerGui(player.getUniqueId());
    }

    public AbstractGui getPlayerGui(UUID player)
    {
        if (this.currentGUIs.containsKey(player))
            return this.currentGUIs.get(player);

        return null;
    }

    public ConcurrentHashMap<UUID, AbstractGui> getPlayersGui()
    {
        return this.currentGUIs;
    }
}
