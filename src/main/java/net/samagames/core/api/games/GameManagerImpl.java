package net.samagames.core.api.games;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.*;
import net.samagames.api.games.themachine.ICoherenceMachine;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.games.themachine.CoherenceMachineImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class GameManagerImpl implements IGameManager
{
    private final ApiImplementation api;

    private final ArrayList<UUID> playersDisconnected;
    private final HashMap<UUID, Integer> playerDisconnectTime;
    private final HashMap<UUID, BukkitTask> playerReconnectedTimers;
    private final IGameProperties gameProperties;
    private Game game;
    private int maxReconnectTime;

    public GameManagerImpl(ApiImplementation api)
    {
        this.api = api;
        this.game = null;

        this.playersDisconnected = new ArrayList<>();
        this.playerDisconnectTime = new HashMap<>();
        this.playerReconnectedTimers = new HashMap<>();

        this.maxReconnectTime = -1;
        this.gameProperties = new GameProperties();
    }

    @Override
    public void registerGame(Game game)
    {
        if (this.game != null)
            throw new IllegalStateException("A game is already registered!");

        this.game = game;

        api.getJoinManager().registerHandler(new GameLoginHandler(this), 100);

        APIPlugin.getInstance().getExecutor().scheduleAtFixedRate(() ->
        {
            if (game != null) this.refreshArena();
        }, 1L, 3 * 30L, TimeUnit.SECONDS);

        game.handlePostRegistration();

        Bukkit.getPluginManager().registerEvents(new SpectatorListener(game), this.api.getPlugin());

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

        api.getProxyDataManager().apiexec("connect", p.getUniqueId().toString(), "lobby");

    }

    @Override
    public void onPlayerDisconnect(Player player)
    {
        game.handleLogout(player);

        if (!isReconnectAllowed())
            return;

        if (game.getStatus() != Status.IN_GAME)
            return;

        playersDisconnected.add(player.getUniqueId());

        api.getBungeeResource().set("rejoin:" + player.getUniqueId(), api.getServerName());
        api.getBungeeResource().expire("rejoin:" + player.getUniqueId(), maxReconnectTime * 60);

        playerReconnectedTimers.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimerAsynchronously(APIPlugin.getInstance(), new Runnable()
        {
            int before;
            int now;
            boolean bool;

            @Override
            public void run()
            {
                if (!bool)
                {
                    if (GameManagerImpl.this.playerDisconnectTime.containsKey(player.getUniqueId()))
                        before = GameManagerImpl.this.playerDisconnectTime.get(player.getUniqueId());

                    bool = true;
                }

                if (before == GameManagerImpl.this.maxReconnectTime * 60 * 2 || now == GameManagerImpl.this.maxReconnectTime * 60)
                    GameManagerImpl.this.onPlayerReconnectTimeOut(player);

                before++;
                now++;

                GameManagerImpl.this.playerDisconnectTime.put(player.getUniqueId(), this.before);
            }
        }, 20L, 20L));

        refreshArena();
    }

    @Override
    public void onPlayerReconnect(Player player)
    {
        if (playerReconnectedTimers.containsKey(player.getUniqueId()))
        {
            BukkitTask task = playerReconnectedTimers.get(player.getUniqueId());

            if (task != null)
                task.cancel();

            playerReconnectedTimers.remove(player.getUniqueId());
        }

        game.handleReconnect(player);
        refreshArena();
    }

    @Override
    public void onPlayerReconnectTimeOut(Player player)
    {
        if (playerReconnectedTimers.containsKey(player.getUniqueId()))
        {
            BukkitTask task = playerReconnectedTimers.get(player.getUniqueId());

            if (task != null)
                task.cancel();

            playerReconnectedTimers.remove(player.getUniqueId());
        }

        game.handleReconnectTimeOut(player);
    }

    public void refreshArena()
    {
        if (game == null)
            throw new IllegalStateException("Can't refresh arena because the arena is null!");

        new ServerStatus(SamaGamesAPI.get().getServerName(), game.getGameName(), gameProperties.getMapName(), game.getStatus(), game.getConnectedPlayers(), gameProperties.getMaxSlots()).sendToHubs();
    }

    @Override
    public Game getGame()
    {
        return game;
    }

    @Override
    public Status getGameStatus()
    {
        if (game == null)
            return null;

        return getGame().getStatus();
    }

    @Override
    public ICoherenceMachine getCoherenceMachine()
    {
        if (this.game == null)
            throw new NullPointerException("Can't get CoherenceMachine because game is null!");

        if (this.coherenceMachine == null)
            return new CoherenceMachineImpl(this.game, this.gameProperties);

        return this.game.getCoherenceMachine();
    }

    @Override
    public IGameProperties getGameProperties()
    {
        return gameProperties;
    }

    @Override
    public GameGuiManager getGameGuiManager()
    {
        return new GameGuiManager();
    }

    @Override
    public int getMaxReconnectTime()
    {
        return maxReconnectTime;
    }

    @Override
    public void setMaxReconnectTime(int minutes)
    {
        maxReconnectTime = minutes;
    }

    @Override
    public boolean isWaited(UUID uuid)
    {
        return playersDisconnected.contains(uuid);
    }

    @Override
    public boolean isReconnectAllowed()
    {
        return maxReconnectTime != -1;
    }
}
