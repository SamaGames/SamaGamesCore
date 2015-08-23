package net.samagames.core.api.stats;

import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.stats.IPlayerStat;
import net.samagames.core.ApiImplementation;
import net.samagames.core.rest.RestAPI;
import net.samagames.core.rest.request.Request;
import net.samagames.core.rest.response.Response;
import net.samagames.core.rest.response.ValueResponse;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@Deprecated
public class PlayerStat implements IPlayerStat
{
    private final UUID playerUUID;
    private final String game;
    private final String stat;
    private final ApiImplementation api;
    private Double value;
    private Long rank;

    private AbstractPlayerData playerData;

    public PlayerStat(UUID playerUUID, String game, String stat)
    {
        this.playerUUID = playerUUID;
        this.game = game;
        this.stat = stat;
        this.api = (ApiImplementation) ApiImplementation.get();
        this.playerData = api.getPlayerManager().getPlayerData(playerUUID);
    }

    public boolean fill()
    {
        if (api.useRestFull())
        {
            Response response = RestAPI.getInstance().sendRequest("player/statistic", new Request().addProperty("playerUUID", playerUUID).addProperty("category", game).addProperty("key", stat), ValueResponse.class, "POST");
            if (response instanceof ValueResponse)
            {
                String newValue = ((ValueResponse) response).getValue();
                if (newValue == null || newValue.isEmpty())
                {
                    this.value = 0.0D;
                    return false;
                }
                else
                    this.value = Double.parseDouble(newValue);
                this.rank = 1L; // TODO: implement Rank into RestAPI
                return true;
            }
            return false;
        }
        else
        {
            Jedis jedis = api.getResource();

            this.value = jedis.zscore("gamestats:" + this.game + ":" + this.stat, this.playerUUID.toString());

            if (this.value == null)
            {
                jedis.close();
                return false;
            }

            this.rank = jedis.zrank("gamestats:" + this.game + ":" + this.stat, this.playerUUID.toString());

            if (this.rank != null)
                this.rank++;

            jedis.close();

            return true;
        }
    }

    public UUID getPlayerUUID()
    {
        return this.playerUUID;
    }

    public String getGame()
    {
        return this.game;
    }

    public String getStat()
    {
        return this.stat;
    }

    public long getRank()
    {
        return this.rank;
    }

    public double getValue()
    {
        return this.value;
    }
}
