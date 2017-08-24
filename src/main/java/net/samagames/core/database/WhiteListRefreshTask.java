package net.samagames.core.database;

import net.samagames.core.APIPlugin;
import redis.clients.jedis.Jedis;

import java.util.Set;

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
class WhiteListRefreshTask implements Runnable
{

    private final APIPlugin plugin;
    private final DatabaseConnector databaseConnector;

    WhiteListRefreshTask(APIPlugin plugin, DatabaseConnector connector)
    {
        this.plugin = plugin;
        this.databaseConnector = connector;
    }

    public void run()
    {
        Jedis jedis = databaseConnector.getBungeeResource();
        Set<String> whiteList = jedis.smembers("proxys");
        jedis.close();

        plugin.refreshIps(whiteList);
    }

}

