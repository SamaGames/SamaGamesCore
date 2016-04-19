package net.samagames.core;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.achievements.IAchievementManager;
import net.samagames.api.gui.IGuiManager;
import net.samagames.api.names.IUUIDTranslator;
import net.samagames.api.pubsub.IPubSubAPI;
import net.samagames.api.shops.AbstractShopsManager;
import net.samagames.core.api.friends.FriendsManager;
import net.samagames.core.api.games.GameManager;
import net.samagames.core.api.gui.GuiManager;
import net.samagames.core.api.names.UUIDTranslator;
import net.samagames.core.api.network.JoinManagerImplement;
import net.samagames.core.api.network.ModerationJoinHandler;
import net.samagames.core.api.network.PartiesPubSub;
import net.samagames.core.api.network.RegularJoinHandler;
import net.samagames.core.api.options.ServerOptions;
import net.samagames.core.api.parties.PartiesManager;
import net.samagames.core.api.permissions.PermissionManager;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.core.api.pubsub.PubSubAPI;
import net.samagames.core.api.resourcepacks.ResourcePacksManagerImpl;
import net.samagames.core.api.settings.SettingsManager;
import net.samagames.core.api.stats.StatsManager;
import net.samagames.core.listeners.pubsub.GlobalUpdateListener;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.tools.SkyFactory;
import net.samagames.tools.npc.NPCManager;
import redis.clients.jedis.Jedis;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    private final SkyFactory skyFactory;
    private final StatsManager statsManager;
    private GameManager gameManager;

    private final ServerOptions serverOptions;

    public ApiImplementation(APIPlugin plugin)
    {
        super(plugin);

        this.plugin = plugin;

        this.pubSub = new PubSubAPI();
        this.pubSub.init(this);
        //TODO redo
        GlobalUpdateListener listener = new GlobalUpdateListener(plugin);
        this.pubSub.subscribe("groupchange", listener);
        this.pubSub.subscribe("global", listener);
        this.pubSub.subscribe(plugin.getServerName(), listener);
        this.pubSub.subscribe("commands.servers." + getServerName(), new RemoteCommandsHandler());
        this.pubSub.subscribe("commands.servers.all", new RemoteCommandsHandler());

        this.serverOptions = new ServerOptions();

        this.statsManager = new StatsManager(this);

        JoinManagerImplement implement = new JoinManagerImplement(this);
        this.joinManager = implement;

        skyFactory = new SkyFactory(plugin);

        guiManager = new GuiManager(plugin);

        resourcePacksManager = new ResourcePacksManagerImpl(this);
        settingsManager = new SettingsManager(this);
        playerDataManager = new PlayerDataManager(this);

        ModerationJoinHandler moderationJoinHandler = new ModerationJoinHandler(this);
        implement.registerHandler(moderationJoinHandler, -1);

        pubSub.subscribe(plugin.getServerName(), moderationJoinHandler);
        pubSub.subscribe("partyjoin." + getServerName(), new PartiesPubSub(this, implement));
        pubSub.subscribe("join." + getServerName(), new RegularJoinHandler(implement));

        uuidTranslator = new UUIDTranslator(plugin, this);
        partiesManager = new PartiesManager(this);
        permissionsManager = new PermissionManager(plugin);
        friendsManager = new FriendsManager(this);
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
    public SkyFactory getSkyFactory()
    {
        return skyFactory;
    }

    @Override
    public JoinManagerImplement getJoinManager()
    {
        return joinManager;
    }

    @Override
    public StatsManager getStatsManager()
    {
        return statsManager;
    }

    @Override
    public AbstractShopsManager getShopsManager()
    {
        throw new NotImplementedException();
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
        throw new NotImplementedException();
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
