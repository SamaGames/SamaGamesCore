package net.samagames.core;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.achievements.IAchievementManager;
import net.samagames.api.friends.IFriendsManager;
import net.samagames.api.games.IGameManager;
import net.samagames.api.gui.IGuiManager;
import net.samagames.api.names.IUUIDTranslator;
import net.samagames.api.network.IJoinManager;
import net.samagames.api.network.IProxyDataManager;
import net.samagames.api.parties.IPartiesManager;
import net.samagames.api.permissions.IPermissionsManager;
import net.samagames.api.player.IPlayerDataManager;
import net.samagames.api.pubsub.IPubSubAPI;
import net.samagames.api.resourcepacks.IResourcePacksManager;
import net.samagames.api.settings.ISettingsManager;
import net.samagames.api.shops.AbstractShopsManager;
import net.samagames.api.stats.AbstractStatsManager;
import net.samagames.core.api.friends.FriendsManagement;
import net.samagames.core.api.games.GameManagerImpl;
import net.samagames.core.api.gui.GuiManager;
import net.samagames.core.api.names.UUIDTranslator;
import net.samagames.core.api.network.*;
import net.samagames.core.api.parties.PartiesManager;
import net.samagames.core.api.permissions.BasicPermissionManager;
import net.samagames.core.api.permissions.PermissionsManager;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.core.api.pubsub.PubSubAPI;
import net.samagames.core.api.resourcepacks.ResourcePacksManagerImpl;
import net.samagames.core.api.settings.SettingsManager;
import net.samagames.core.api.shops.ShopsManager;
import net.samagames.core.api.stats.StatsManager;
import net.samagames.core.listeners.GlobalChannelHandler;
import net.samagames.core.rest.AchievementManagerRest;
import net.samagames.tools.BarAPI.BarAPI;
import net.samagames.tools.npc.NPCManager;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.HashMap;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class ApiImplementation extends SamaGamesAPI
{
    private final APIPlugin plugin;
    private final IGuiManager guiManager;
    private final ISettingsManager settingsManager;
    private final PlayerDataManager playerDataManager;
    private final IAchievementManager achievementManager;
    private final PubSubAPI pubSub;
    private final IUUIDTranslator uuidTranslator;
    private final IJoinManager joinManager;
    private final IProxyDataManager proxyDataManager;
    private final IPartiesManager partiesManager;
    private final IResourcePacksManager resourcePacksManager;
    private final BasicPermissionManager permissionsManager;
    private final IFriendsManager friendsManager;
    private final BarAPI barAPI;
    private IGameManager gameApi;
    private HashMap<String, StatsManager> statsManagerCache;

    public ApiImplementation(APIPlugin plugin)
    {
        super(plugin);

        this.plugin = plugin;
        this.statsManagerCache = new HashMap<>();

        JoinManagerImplement implement = new JoinManagerImplement();
        Bukkit.getServer().getPluginManager().registerEvents(implement, plugin);
        this.joinManager = implement;

        barAPI = new BarAPI(plugin);

        guiManager = new GuiManager(plugin);

        resourcePacksManager = new ResourcePacksManagerImpl(this);
        settingsManager = new SettingsManager();
        playerDataManager = new PlayerDataManager(this);
        achievementManager = new AchievementManagerRest(this);

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
        proxyDataManager = new ProxyDataManagerImpl(this);
        partiesManager = new PartiesManager(this);
        permissionsManager = new PermissionsManager(plugin);
        friendsManager = new FriendsManagement(this);

        // Init Group change listener
        pubSub.subscribe("groupchange", new GroupChangeHandler(permissionsManager));
    }

    public void onShutdown()
    {
        this.playerDataManager.onShutdown();
    }

    @Override
    public IPermissionsManager getPermissionsManager()
    {
        return permissionsManager;
    }

    @Override
    public NPCManager getNPCManager() {
        return plugin.getNPCManager();
    }

    @Override
    public IResourcePacksManager getResourcePacksManager()
    {
        return resourcePacksManager;
    }

    @Override
    public IFriendsManager getFriendsManager()
    {
        return friendsManager;
    }

    public APIPlugin getPlugin()
    {
        return plugin;
    }

    public IProxyDataManager getProxyDataManager()
    {
        return proxyDataManager;
    }

    public IGameManager getGameManager()
    {
        return (gameApi == null) ? (this.gameApi = new GameManagerImpl(this)) : this.gameApi;
    }

    @Override
    public IPartiesManager getPartiesManager()
    {
        return partiesManager;
    }

    @Override
    public BarAPI getBarAPI()
    {
        return barAPI;
    }

    @Override
    public IJoinManager getJoinManager()
    {
        return joinManager;
    }

    public AbstractStatsManager getStatsManager(String game)
    {
        if (this.statsManagerCache.containsKey(game))
            return statsManagerCache.get(game);

        StatsManager statsManager = new StatsManager(game, this);
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
    public ISettingsManager getSettingsManager()
    {
        return settingsManager;
    }

    @Override
    public IPlayerDataManager getPlayerManager()
    {
        return playerDataManager;
    }

    @Override
    public IAchievementManager getAchievementManager()
    {
        return achievementManager;
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

    @Override
    public String getServerName()
    {
        return plugin.getServerName();
    }

}
