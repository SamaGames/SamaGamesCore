package net.samagames.core.listeners.general;

import net.samagames.api.gui.AbstractGui;
import net.samagames.core.api.gui.GuiManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 10/08/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class GuiListener implements Listener
{
    private final GuiManager manager;

    public GuiListener(GuiManager manager)
    {
        this.manager = manager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (event.getWhoClicked() instanceof Player)
        {
            Player player = (Player) event.getWhoClicked();
            AbstractGui gui = manager.getPlayerGui(player);

            if (gui != null)
            {
                if (event.getClickedInventory() instanceof PlayerInventory)
                    return;

                String action = gui.getAction(event.getSlot());

                if (action != null)
                    gui.onClick(player, event.getCurrentItem(), action, event.getClick());

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (manager.getPlayerGui(event.getPlayer()) != null)
            manager.removeClosedGui((Player) event.getPlayer());
    }
}