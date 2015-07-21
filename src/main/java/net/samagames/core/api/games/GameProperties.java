package net.samagames.core.api.games;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.samagames.api.games.IGameProperties;
import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;

public class GameProperties implements IGameProperties
{
    private String mapName;
    private JsonObject options;
    private int minSlots;
    private int maxSlots;

    public GameProperties()
    {
        this.reload();
    }

    public void reload()
    {
        try
        {
            File file = new File(APIPlugin.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile(), "game.json");

            if(!file.exists())
            {
                APIPlugin.log(Level.WARNING, "No game properties file found! If this serveur isn't a game server, don't worry about this message!");
                return;
            }

            JsonObject rootJson = new JsonParser().parse(new FileReader(file)).getAsJsonObject();

            this.mapName = rootJson.get("map-name").getAsString();
            this.minSlots = rootJson.get("min-slots").getAsInt();
            this.maxSlots = rootJson.get("max-slots").getAsInt();
            this.options = rootJson.get("options").getAsJsonObject();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            APIPlugin.log(Level.SEVERE, "Can't open the game properties file. Abort start!");

            Bukkit.shutdown();
        }
    }

    public String getMapName()
    {
        return this.mapName;
    }

    public int getMinSlots()
    {
        return this.minSlots;
    }

    public int getMaxSlots()
    {
        return this.maxSlots;
    }

    public JsonPrimitive getOption(String key, JsonPrimitive defaultValue)
    {
        if(this.options.has(key))
            return this.options.get(key).getAsJsonPrimitive();
        else
            return defaultValue;
    }

    public JsonObject getOptions()
    {
        return this.options;
    }
}
