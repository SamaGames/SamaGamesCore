package net.samagames.core.api.settings;

import net.samagames.api.settings.ISettingsManager;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.players.PlayerSettingsBean;

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
public class SettingsManager implements ISettingsManager
{
    private ApiImplementation api;

    //Thread safe to be sure
    private ConcurrentHashMap<UUID, ImpPlayerSettings> cache;

    public SettingsManager(ApiImplementation api)
    {
        this.api = api;
        this.cache = new ConcurrentHashMap<>();
    }

    public void loadPlayer(UUID uuid)
    {
        try{
            PlayerData playerData = api.getPlayerManager().getPlayerData(uuid);
            //First load from sql the save
            PlayerSettingsBean playerSettings1 = api.getGameServiceManager().getPlayerSettings(playerData.getPlayerBean());
            ImpPlayerSettings playerSettings = new ImpPlayerSettings(api.getGameServiceManager(), playerData, playerSettings1);

            //Don't refresh here, data are recent so it only will spam proxy
            cache.put(uuid, playerSettings);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void unloadPlayer(UUID uuid)
    {
        if (cache.containsKey(uuid))
        {
            //Just remove data because update data when we change it
            //cache.get(uuid).update();
            //Then remove
            cache.remove(uuid);
        }
    }

    @Override
    public ImpPlayerSettings getSettings(UUID uuid)
    {
        return cache.get(uuid);
    }
}
