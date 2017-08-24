package net.samagames.core.api.gui;

import net.samagames.api.gui.AbstractGui;
import net.samagames.api.gui.IGuiManager;
import net.samagames.core.APIPlugin;
import net.samagames.core.listeners.general.GuiListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This file is part of SamaGamesCore.
 *
 * SamaGamesCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesCore.  If not, see <http://www.gnu.org/licenses/>.
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
