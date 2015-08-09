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

public class GameProperties implements IGameProperties
{
    private String mapName;
    private JsonObject options;
    private JsonObject mapProperties;
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
            
            File arenaFile = new File(worldFolder, "arena.json");
            
            if(!arenaFile.exists())
            {
                APIPlugin.log(Level.WARNING, "No arena properties file found! If this serveur isn't a game server, don't worry about this message!");
                return;
            }
            
            this.mapProperties = new JsonParser().parse(new FileReader(arenaFile)).getAsJsonObject();
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

    public JsonElement getOption(String key, JsonElement defaultValue)
    {
        if(this.options.has(key))
            return this.options.get(key);
        else
            return defaultValue;
    }

    public JsonObject getOptions()
    {
        return this.options;
    }
    
    public JsonObject getMapProperties()
    {
        return this.mapProperties;
    }
}