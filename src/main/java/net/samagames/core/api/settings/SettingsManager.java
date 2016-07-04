package net.samagames.core.api.settings;

import net.samagames.api.settings.ISettingsManager;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.players.PlayerSettingsBean;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
