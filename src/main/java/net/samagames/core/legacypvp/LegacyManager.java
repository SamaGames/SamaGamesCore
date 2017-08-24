package net.samagames.core.legacypvp;

import net.samagames.core.APIPlugin;
import net.samagames.core.legacypvp.armors.ArmorModule;
import net.samagames.core.legacypvp.cooldown.CooldownModule;
import org.bukkit.Bukkit;

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
public class LegacyManager
{
    public LegacyManager(APIPlugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(new ArmorModule(plugin.getAPI()), plugin);
        Bukkit.getPluginManager().registerEvents(new CooldownModule(plugin.getAPI()), plugin);
    }
}
