package net.samagames.core.api.settings;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.settings.ISettingsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingsManager implements ISettingsManager
{

    public Map<String, String> getSettings(UUID player)
    {
        Map<String, String> data = SamaGamesAPI.get().getPlayerManager().getPlayerData(player).getValues();
        HashMap<String, String> settings = new HashMap<>();
        data.entrySet().stream().filter(line -> line.getKey().startsWith("settings.")).forEach(line -> {
            String setting = line.getKey().split(".")[0];
            settings.put(setting, line.getValue());
        });

        return settings;
    }

    public String getSetting(UUID player, String setting)
    {
        return SamaGamesAPI.get().getPlayerManager().getPlayerData(player).get("settings." + setting);
    }

    public void setSetting(UUID player, String setting, String value)
    {
        SamaGamesAPI.get().getPlayerManager().getPlayerData(player).set("settings." + setting, value);
    }

    public void setSetting(UUID player, String setting, String value, Runnable callback)
    {
        SamaGamesAPI.get().getPlayerManager().getPlayerData(player).set("settings." + setting, value);
        callback.run();
    }
}
