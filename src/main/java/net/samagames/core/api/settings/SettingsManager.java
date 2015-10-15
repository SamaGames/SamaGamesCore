package net.samagames.core.api.settings;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.settings.ISettingsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingsManager implements ISettingsManager
{

    private final SamaGamesAPI api;

    public SettingsManager(SamaGamesAPI api)
    {
        this.api = api;
    }

    public Map<String, String> getSettings(UUID player)
    {
        Map<String, String> data = api.getPlayerManager().getPlayerData(player).getValues();
        HashMap<String, String> settings = new HashMap<>();
        data.entrySet().stream().filter(line -> line.getKey().startsWith("settings.")).forEach(line -> {
            String setting = line.getKey().split(".")[0];
            settings.put(setting, line.getValue());
        });

        return settings;
    }

    public String getSetting(UUID player, String setting)
    {
        return api.getPlayerManager().getPlayerData(player).get("settings." + setting);
    }

    public void setSetting(UUID player, String setting, String value)
    {
        api.getPlayerManager().getPlayerData(player).set("settings." + setting, value);
    }

    public void setSetting(UUID player, String setting, String value, Runnable callback)
    {
        api.getPlayerManager().getPlayerData(player).set("settings." + setting, value);
        callback.run();
    }
}
