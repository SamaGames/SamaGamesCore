package net.samagames.core.api.games;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.*;
import net.samagames.api.games.themachine.ICoherenceMachine;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.games.themachine.CoherenceMachineImpl;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class GameManager implements IGameManager
{
    private final ApiImplementation api;

    private final ConcurrentHashMap<UUID, Long> playerDisconnectedTime;
    private final IGameProperties gameProperties;
    private Game game;
    private int maxReconnectTime;
    private boolean freeMode;
    private boolean legacyPvP;

    //Maybe useful some day
    private BukkitTask checkerThread;

    public GameManager(ApiImplementation api)
    {
        this.api = api;
        this.game = null;

        this.playerDisconnectedTime = new ConcurrentHashMap<>();

        this.maxReconnectTime = -1;
        this.freeMode = false;
        this.legacyPvP = false;

        this.gameProperties = new GameProperties();

    }

    @Override
    public void registerGame(Game game)
    {
        if (this.game != null)
            throw new IllegalStateException("A game is already registered!");

        this.game = game;

        this.api.getJoinManager().registerHandler(new GameLoginHandler(this), 100);

        APIPlugin.getInstance().getExecutor().scheduleAtFixedRate(() ->
        {
            if (game != null)
                this.refreshArena();
        }, 1L, 3 * 30L, TimeUnit.SECONDS);

        game.handlePostRegistration();

        //Check for reconnection can be started when we change the mas reconnection time but fuck it
        checkerThread = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                long currentTimeMillis = System.currentTimeMillis();
                for (Map.Entry<UUID, Long> data : playerDisconnectedTime.entrySet())
                {
                    long delta = currentTimeMillis - data.getValue();

                    if (delta >= maxReconnectTime * 60)
                    {
                        OfflinePlayer playerReconnected = Bukkit.getOfflinePlayer(data.getKey());

                        if (!playerReconnected.isOnline())
                            onPlayerReconnectTimeOut(playerReconnected, false);

                        //Useless because called in onPlayerReconnectTimeOut
                        //playerDisconnectedTime.remove(data.getKey());
                    }
                }
            }
        }.runTaskTimerAsynchronously(api.getPlugin(), 20, 20);

        APIPlugin.log(Level.INFO, "Registered game '" + game.getGameName() + "' successfuly!");
    }

    @Override
    public void kickPlayer(Player p, String msg)
    {
        if (!this.api.getPlugin().isEnabled())
        {
            p.kickPlayer(msg);
            return;
        }

        if (!p.isOnline())
            return;

        //TODO kick offline
        /*this.api.getPlayerManager()
        this.api.getProxyDataManager().apiexec("connect", p.getUniqueId().toString(), "lobby");*/
    }

    @Override
    public void onPlayerDisconnect(Player player)
    {
        this.game.handleLogout(player);

        if (!this.isReconnectAllowed(player))
            return;

        if (this.game.getStatus() != Status.IN_GAME)
            return;

        long currentTime = System.currentTimeMillis();
        Long decoTime = this.playerDisconnectedTime.get(player.getUniqueId());

        if (decoTime != null && currentTime - decoTime >= this.maxReconnectTime * 60 * 2)
        {
            this.game.handleReconnectTimeOut(player, true);
            return;
        }

        api.getPlugin().getExecutor().execute(() -> {
            Jedis jedis = api.getBungeeResource();
            jedis.set("rejoin:" + player.getUniqueId(), this.api.getServerName());
            jedis.expire("rejoin:" + player.getUniqueId(), (this.maxReconnectTime * 60));
            jedis.close();
        });

        refreshArena();
    }

    @Override
    public void onPlayerReconnect(Player player)
    {
        this.game.handleReconnect(player);

        Long decoTime = this.playerDisconnectedTime.get(player.getUniqueId());

        if (decoTime != null)
        {
            this.playerDisconnectedTime.remove(player.getUniqueId());
        }

        refreshArena();
    }

    @Override
    public void onPlayerReconnectTimeOut(OfflinePlayer player, boolean silent)
    {
        this.playerDisconnectedTime.remove(player.getUniqueId());
        this.game.handleReconnectTimeOut(player, silent);
    }

    public void refreshArena()
    {
        if (this.game == null)
            throw new IllegalStateException("Can't refresh arena because the arena is null!");

        new ServerStatus(SamaGamesAPI.get().getServerName(), this.game.getGameName(), this.gameProperties.getMapName(), this.game.getStatus(), this.game.getConnectedPlayers() + api.getJoinManager().countExpectedPlayers(), this.gameProperties.getMaxSlots()).sendToHubs();
    }

    @Override
    public Game getGame()
    {
        return this.game;
    }

    @Override
    public Status getGameStatus()
    {
        if (game == null)
            return null;

        return this.game.getStatus();
    }

    @Override
    public ICoherenceMachine getCoherenceMachine()
    {
        if (this.game == null)
            throw new NullPointerException("Can't get CoherenceMachine because game is null!");

        if (this.game.getCoherenceMachine() == null)
            return new CoherenceMachineImpl(this.game, this.gameProperties);

        return this.game.getCoherenceMachine();
    }

    @Override
    public IGameProperties getGameProperties()
    {
        return this.gameProperties;
    }

    @Override
    public GameGuiManager getGameGuiManager()
    {
        //TODO gui manager ?
        return new GameGuiManager();
    }

    @Override
    public int getMaxReconnectTime()
    {
        return this.maxReconnectTime;
    }

    @Override
    public void setMaxReconnectTime(int minutes)
    {
        this.maxReconnectTime = minutes;
    }

    @Override
    public void setFreeMode(boolean freeMode)
    {
        this.freeMode = freeMode;
    }

    @Override
    public void setLegacyPvP(boolean legacyPvP)
    {
        this.legacyPvP = legacyPvP;
    }

    @Override
    public boolean isWaited(UUID uuid)
    {
        return this.playerDisconnectedTime.containsKey(uuid);
    }

    @Override
    public boolean isFreeMode()
    {
        return this.freeMode;
    }

    @Override
    public boolean isLegacyPvP()
    {
        return this.legacyPvP;
    }

    @Override
    public boolean isReconnectAllowed(Player player)
    {
        return this.isReconnectAllowed(player.getUniqueId());
    }

    @Override
    public boolean isReconnectAllowed(UUID player)
    {
        if (this.maxReconnectTime <= 0)
            return false;

        Long decoTime = this.playerDisconnectedTime.get(player);

        return decoTime == null || System.currentTimeMillis() - decoTime < this.maxReconnectTime * 60;
    }
}
