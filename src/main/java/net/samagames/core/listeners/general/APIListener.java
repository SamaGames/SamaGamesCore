package net.samagames.core.listeners.general;

import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import org.bukkit.event.Listener;

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
abstract class APIListener implements Listener
{
    final APIPlugin plugin;
    final ApiImplementation api;

    APIListener(APIPlugin plugin)
    {
        this.plugin = plugin;
        this.api = plugin.getAPI();
    }
}
