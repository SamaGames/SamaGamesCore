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
    private ConcurrentHashMap<UUID, PlayerSettings> cache;

    public SettingsManager(ApiImplementation api)
    {
        this.api = api;
        this.cache = new ConcurrentHashMap<>();
    }

    //TODO load at join
    public void loadPlayer(UUID uuid)
    {
        PlayerData playerData = api.getPlayerManager().getPlayerData(uuid);
        PlayerSettings playerSettings = new PlayerSettings(playerData);
        playerSettings.refresh();
        cache.put(uuid, playerSettings);
    }

    //TODO unload at leave
    public void unloadPlayer(UUID uuid)
    {
        //Update data to be sure we don't loose data
        cache.get(uuid).update();
        //Then remove
        cache.remove(uuid);
    }

    @Override
    public PlayerSettings getSettings(UUID uuid)
    {
        return cache.get(uuid);
    }
}
