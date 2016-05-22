package net.samagames.core.api.settings;

import net.samagames.api.settings.ISettingsManager;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;

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
            ImpPlayerSettings playerSettings = new ImpPlayerSettings(playerData);
            playerSettings.refresh();
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
            //Update data to be sure we don't loose data
            cache.get(uuid).update();
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
