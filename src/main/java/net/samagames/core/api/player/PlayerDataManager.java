package net.samagames.core.api.player;

import com.google.gson.Gson;
import net.md_5.bungee.api.chat.TextComponent;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.player.IPlayerDataManager;
import net.samagames.core.ApiImplementation;
import org.bukkit.Bukkit;

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
public class PlayerDataManager implements IPlayerDataManager
{

    private final ApiImplementation api;
    private final ConcurrentHashMap<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    private final EconomyManager economyManager;


    public PlayerDataManager(ApiImplementation api)
    {
        this.api = api;
        economyManager = new EconomyManager(api);
    }

    public EconomyManager getEconomyManager()
    {
        return economyManager;
    }

    @Override
    public PlayerData getPlayerData(UUID player)
    {
        return getPlayerData(player, false);
    }

    @Override
    public PlayerData getPlayerData(UUID player, boolean forceRefresh)
    {
        if (player == null)
        {
            throw new NullPointerException("Parameter player is null !");
        }

        PlayerData data = cache.get(player);

        /*if (forceRefresh)
        {
            data.refreshData();
            return data;
        }*/

        //data.refreshIfNeeded();
        if (data == null)
        {
            api.getPlugin().getLogger().severe(player + " is not in the cache !");
        }
        return data;
    }

    public PlayerData getPlayerDataByName(String name)
    {
        for (PlayerData data : cache.values())
        {
            if (data.getEffectiveName().equals(name))
                return data;
        }

        return null;
    }

    public void loadPlayer(UUID player)
    {
        try{
            PlayerData playerData = new PlayerData(player, api, this);
            cache.put(player, playerData);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void unloadPlayer(UUID player)
    {
        //Update data before delete
        /*if(cache.containsKey(player))
            cache.get(player).updateData();*/
        //Continuous update, save here result in data lose for shop

        //Schedule that because of nickname needs
        if (!api.isKeepCache())
        {
            Bukkit.getScheduler().runTaskLater(api.getPlugin(), () -> cache.remove(player), 2L);
        }

    }

    //TODO nickname
    @Override
    public void kickFromNetwork(UUID playerUUID, TextComponent reason)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.kick", playerUUID + " " + new Gson().toJson(reason));
    }

    @Override
    public void connectToServer(UUID playerUUID, String server)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.connect", playerUUID + " " + server);
    }

    @Override
    public void sendMessage(UUID playerUUID, TextComponent component)
    {
        SamaGamesAPI.get().getPubSub().send("apiexec.send", playerUUID + " " + new Gson().toJson(component));
    }


    public void onShutdown()
    {
        economyManager.onShutdown();
    }
}
