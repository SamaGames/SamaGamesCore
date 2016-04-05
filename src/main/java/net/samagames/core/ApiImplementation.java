package net.samagames.core;

import net.samagames.core.api.options.ServerOptions;
import net.samagames.core.api.permissions.GroupChangeHandler;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.achievements.IAchievementManager;
import net.samagames.api.gui.IGuiManager;
import net.samagames.api.names.IUUIDTranslator;
import net.samagames.api.network.IJoinManager;
import net.samagames.api.pubsub.IPubSubAPI;
import net.samagames.api.shops.AbstractShopsManager;
import net.samagames.api.stats.IStatsManager;
import net.samagames.core.api.friends.FriendsManager;
import net.samagames.core.api.games.GameManager;
import net.samagames.core.api.gui.GuiManager;
import net.samagames.core.api.names.UUIDTranslator;
import net.samagames.core.api.network.*;
import net.samagames.core.api.parties.PartiesManager;
import net.samagames.core.api.permissions.PermissionManager;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.core.api.pubsub.PubSubAPI;
import net.samagames.core.api.resourcepacks.ResourcePacksManagerImpl;
import net.samagames.core.api.settings.SettingsManager;
import net.samagames.core.api.shops.ShopsManager;
import net.samagames.core.api.stats.StatsManager;
import net.samagames.core.listeners.pubsub.GlobalChannelHandler;
import net.samagames.tools.BarAPI.BarAPI;
import net.samagames.tools.SkyFactory;
import net.samagames.tools.npc.NPCManager;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.HashMap;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class ApiImplementation extends SamaGamesAPI
{
    private final APIPlugin plugin;
    private final GuiManager guiManager;
    private final SettingsManager settingsManager;
    private final PlayerDataManager playerDataManager;
    private final PubSubAPI pubSub;
    private final UUIDTranslator uuidTranslator;
    private final JoinManagerImplement joinManager;
    private final PartiesManager partiesManager;
    private final ResourcePacksManagerImpl resourcePacksManager;
    private final PermissionManager permissionsManager;
    private final FriendsManager friendsManager;
    private final BarAPI barAPI;
    private final SkyFactory skyFactory;
    private final HashMap<String, StatsManager> statsManagerCache;
    private GameManager gameManager;

    private final ServerOptions serverOptions;

    public ApiImplementation(APIPlugin plugin)
    {
        super(plugin);

        this.plugin = plugin;

        serverOptions = new ServerOptions();

        this.statsManagerCache = new HashMap<>();

        JoinManagerImplement implement = new JoinManagerImplement();
        Bukkit.getServer().getPluginManager().registerEvents(implement, plugin);
        this.joinManager = implement;

        barAPI = new BarAPI(plugin);
        skyFactory = new SkyFactory(plugin);

        guiManager = new GuiManager(plugin);

        resourcePacksManager = new ResourcePacksManagerImpl(this);
        settingsManager = new SettingsManager(this);
        playerDataManager = new PlayerDataManager(this);

        pubSub = new PubSubAPI();
        pubSub.init(this);
        pubSub.subscribe("global", new GlobalChannelHandler(plugin));
        pubSub.subscribe(plugin.getServerName(), new GlobalChannelHandler(plugin));
        pubSub.subscribe("commands.servers." + getServerName(), new RemoteCommandsHandler());
        pubSub.subscribe("commands.servers.all", new RemoteCommandsHandler());

        ModerationJoinHandler moderationJoinHandler = new ModerationJoinHandler(this);
        implement.registerHandler(moderationJoinHandler, -1);

        pubSub.subscribe(plugin.getServerName(), moderationJoinHandler);
        pubSub.subscribe("partyjoin." + getServerName(), new PartiesPubSub(implement));
        pubSub.subscribe("join." + getServerName(), new RegularJoinHandler(implement));

        uuidTranslator = new UUIDTranslator(plugin, this);
        partiesManager = new PartiesManager(this);
        permissionsManager = new PermissionManager(plugin);
        friendsManager = new FriendsManager(this);


        // Init Group change listener
        pubSub.subscribe("groupchange", new GroupChangeHandler(permissionsManager));
    }

    public void onShutdown()
    {
        this.playerDataManager.onShutdown();
    }

    @Override
    public PermissionManager getPermissionsManager()
    {
        return permissionsManager;
    }

    @Override
    public NPCManager getNPCManager() {
        return plugin.getNPCManager();
    }

    @Override
    public ServerOptions getServerOptions() {
        return serverOptions;
    }

    @Override
    public ResourcePacksManagerImpl getResourcePacksManager()
    {
        return resourcePacksManager;
    }

    @Override
    public FriendsManager getFriendsManager()
    {
        return friendsManager;
    }

    @Override
    public APIPlugin getPlugin()
    {
        return plugin;
    }

    @Override
    public GameManager getGameManager()
    {
        return (gameManager == null) ? (this.gameManager = new GameManager(this)) : this.gameManager;
    }

    @Override
    public PartiesManager getPartiesManager()
    {
        return partiesManager;
    }

    @Override
    public BarAPI getBarAPI()
    {
        return barAPI;
    }

    @Override
    public SkyFactory getSkyFactory()
    {
        return skyFactory;
    }

    @Override
    public IJoinManager getJoinManager()
    {
        return joinManager;
    }

    public IStatsManager getStatsManager(String game)
    {
        if (this.statsManagerCache.containsKey(game))
            return statsManagerCache.get(game);

        StatsManager statsManager = new StatsManager(this);
        statsManagerCache.put(game, statsManager);
        return statsManager;
    }

    @Override
    public AbstractShopsManager getShopsManager(String game)
    {
        return new ShopsManager(game, this);
    }

    @Override
    public IGuiManager getGuiManager()
    {
        return guiManager;
    }

    @Override
    public SettingsManager getSettingsManager()
    {
        return settingsManager;
    }

    @Override
    public PlayerDataManager getPlayerManager()
    {
        return playerDataManager;
    }

    @Override
    public IAchievementManager getAchievementManager()
    {
        throw new RuntimeException("Not implemented yet");
    }

    public IPubSubAPI getPubSub()
    {
        return pubSub;
    }

    @Override
    public IUUIDTranslator getUUIDTranslator()
    {
        return uuidTranslator;
    }

    public Jedis getBungeeResource()
    {
        return plugin.getDatabaseConnector().getBungeeResource();
    }

    public GameServiceManager getGameServiceManager()
    {
        return plugin.getGameServiceManager();
    }

    @Override
    public String getServerName()
    {
        return plugin.getServerName();
    }

}
