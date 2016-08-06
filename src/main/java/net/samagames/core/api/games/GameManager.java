package net.samagames.core.api.games;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.*;
import net.samagames.api.games.themachine.ICoherenceMachine;
import net.samagames.api.parties.IParty;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.games.themachine.CoherenceMachineImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
    private final GameProperties gameProperties;
    private IGameStatisticsHelper gameStatisticsHelper;
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
        this.gameStatisticsHelper = null;

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
        APIPlugin.getInstance().getExecutor().scheduleAtFixedRate(() ->
        {
                for (Map.Entry<UUID, Long> entry: playerDisconnectedTime.entrySet())
                {
                    if (!isReconnectAllowed(entry.getKey()))
                    {
                        onPlayerReconnectTimeOut(Bukkit.getOfflinePlayer(entry.getKey()), false);
                    }
                }

        }, 1L, 30L, TimeUnit.SECONDS);

        APIPlugin.log(Level.INFO, "Registered game '" + game.getGameName() + "' successfuly!");
    }

    public void rejoinTemplateQueue(Player p)
    {
        api.getPlugin().getServer().getScheduler().runTaskAsynchronously(api.getPlugin(), () -> {
            IParty party = SamaGamesAPI.get().getPartiesManager().getPartyForPlayer(p.getUniqueId());

            if(party == null)
            {
                this.api.getHydroangeasManager().addPlayerToQueue(p.getUniqueId(), getGameProperties().getTemplateID());
            }
            else
            {
                if(!party.getLeader().equals(p.getUniqueId()))
                {
                    p.sendMessage(ChatColor.RED + "Vous n'êtes pas le leader de votre partie, vous ne pouvez donc pas l'ajouter dans une file d'attente.");
                    return;
                }

                this.api.getHydroangeasManager().addPartyToQueue(p.getUniqueId(), party.getParty(), getGameProperties().getTemplateID());
            }
        });
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

        api.getPlayerManager().connectToServer(p.getUniqueId(), "lobby");
    }

    @Override
    public void onPlayerDisconnect(Player player)
    {
        GamePlayer player1 = this.game.getPlayer(player.getUniqueId());
        if (maxReconnectTime > 0
                && player1 != null
                && !player1.isModerator()
                && !player1.isSpectator()
                && this.game.getStatus() == Status.IN_GAME)
        {
            long currentTime = System.currentTimeMillis();

            playerDisconnectedTime.put(player.getUniqueId(), currentTime);

            api.getPlugin().getExecutor().execute(() -> {
                Jedis jedis = api.getBungeeResource();
                jedis.set("rejoin:" + player.getUniqueId(), this.api.getServerName());
                jedis.expire("rejoin:" + player.getUniqueId(), (this.maxReconnectTime * 60));
                jedis.close();
            });
        }

        this.game.handleLogout(player);

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
    public GameProperties getGameProperties()
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
    public void setGameStatisticsHelper(IGameStatisticsHelper gameStatisticsHelper)
    {
        this.gameStatisticsHelper = gameStatisticsHelper;
    }

    public IGameStatisticsHelper getGameStatisticsHelper()
    {
        return this.gameStatisticsHelper;
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

        return decoTime != null && System.currentTimeMillis() < this.maxReconnectTime * 60 * 1000 + decoTime;
    }

    @Override
    public void setKeepPlayerCache(boolean keepIt)
    {
        api.setKeepCache(keepIt);
    }

    @Override
    public boolean isKeepingPlayerCache() {
        return api.isKeepCache();
    }
}
