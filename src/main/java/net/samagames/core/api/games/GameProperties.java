package net.samagames.core.api.games;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.samagames.api.games.IGameProperties;
import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;

class GameProperties implements IGameProperties
{
    private String mapName;
    private JsonObject options;
    private JsonObject mapProperties;
    private int minSlots;
    private int maxSlots;

    public GameProperties()
    {
        reload();
    }

    public void reload()
    {
        try
        {
            File file = new File(APIPlugin.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile(), "game.json");

            if (!file.exists())
            {
                APIPlugin.log(Level.WARNING, "No game properties file found! If this serveur isn't a game server, don't worry about this message!");
                return;
            }

            JsonObject rootJson = new JsonParser().parse(new FileReader(file)).getAsJsonObject();

            mapName = rootJson.get("map-name").getAsString();
            minSlots = rootJson.get("min-slots").getAsInt();
            maxSlots = rootJson.get("max-slots").getAsInt();
            options = rootJson.get("options").getAsJsonObject();

            File worldFolder = new File(APIPlugin.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile(), "world");
            File arenaFile = new File(worldFolder, "arena.json");

            if (!arenaFile.exists())
            {
                APIPlugin.log(Level.WARNING, "No arena properties file found! If this serveur isn't a game server, don't worry about this message!");
                return;
            }

            mapProperties = new JsonParser().parse(new FileReader(arenaFile)).getAsJsonObject();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            APIPlugin.log(Level.SEVERE, "Can't open the game properties file. Abort start!");

            Bukkit.shutdown();
        }
    }

    public String getMapName()
    {
        return mapName;
    }

    public int getMinSlots()
    {
        return minSlots;
    }

    public int getMaxSlots()
    {
        return maxSlots;
    }

    public JsonElement getOption(String key, JsonElement defaultValue)
    {
        if (options.has(key))
            return options.get(key);
        else
            return defaultValue;
    }

    public JsonObject getOptions()
    {
        return options;
    }

    public JsonElement getConfig(String key, JsonElement defaultValue)
    {
        if (mapProperties.has(key))
            return mapProperties.get(key);
        else
            return defaultValue;
    }

    public JsonObject getConfigs()
    {
        return mapProperties;
    }
}
