package net.samagames.core;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.achievements.IAchievementManager;
import net.samagames.api.channels.IPubSubAPI;
import net.samagames.api.friends.IFriendsManager;
import net.samagames.api.games.IGameManager;
import net.samagames.api.names.IUUIDTranslator;
import net.samagames.api.network.IJoinManager;
import net.samagames.api.network.IProxyDataManager;
import net.samagames.api.parties.IPartiesManager;
import net.samagames.api.permissions.IPermissionsManager;
import net.samagames.api.player.IPlayerDataManager;
import net.samagames.api.resourcepacks.IResourcePacksManager;
import net.samagames.api.settings.ISettingsManager;
import net.samagames.api.shops.AbstractShopsManager;
import net.samagames.api.stats.AbstractStatsManager;
import net.samagames.core.api.achievements.AchievementManagerImpl;
import net.samagames.core.api.friends.FriendsManagement;
import net.samagames.core.api.games.GameManagerImpl;
import net.samagames.core.api.names.UUIDTranslator;
import net.samagames.core.api.network.*;
import net.samagames.core.api.parties.PartiesManager;
import net.samagames.core.api.permissions.BasicPermissionManager;
import net.samagames.core.api.permissions.PermissionsManager;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.core.api.resourcepacks.ResourcePacksManagerImpl;
import net.samagames.core.api.settings.SettingsManager;
import net.samagames.core.api.shops.ShopsManager;
import net.samagames.core.api.stats.StatsManager;
import net.samagames.core.database.DatabaseConnector;
import net.samagames.core.listeners.GlobalChannelHandler;
import net.samagames.tools.BarAPI.BarAPI;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class ApiImplementation extends SamaGamesAPI
{

	protected APIPlugin plugin;
	protected ISettingsManager settingsManager;
	protected IPlayerDataManager playerDataManager;
    protected IAchievementManager achievementManager;
	protected IPubSubAPI pubSub;
	protected IUUIDTranslator uuidTranslator;
	protected IJoinManager joinManager;
	protected IGameManager gameApi;
	protected IProxyDataManager IProxyDataManager;
	protected IPartiesManager partiesManager;
	protected IResourcePacksManager resourcePacksManager;
	protected BasicPermissionManager permissionsManager;
    protected IFriendsManager friendsManager;

	protected BarAPI barAPI;

	public ApiImplementation(APIPlugin plugin) {
        super(plugin);

		this.plugin = plugin;

		IJoinManagerImplement implement = new IJoinManagerImplement();
		Bukkit.getServer().getPluginManager().registerEvents(implement, plugin);
		this.joinManager = implement;

		barAPI = new BarAPI(plugin);

        resourcePacksManager = new ResourcePacksManagerImpl(this);
        settingsManager = new SettingsManager(this);
        playerDataManager = new PlayerDataManager(this);
        achievementManager = new AchievementManagerImpl(this);
        pubSub = new net.samagames.core.api.pubsub.IPubSubAPI(this);
        pubSub.subscribe("global", new GlobalChannelHandler(plugin));
        pubSub.subscribe(plugin.getServerName(), new GlobalChannelHandler(plugin));

        pubSub.subscribe("commands.servers." + getServerName(), new RemoteCommandsHandler());
        pubSub.subscribe("commands.servers.all", new RemoteCommandsHandler());

        ModerationIJoinHandler moderationJoinHandler = new ModerationIJoinHandler(implement);
        implement.registerHandler(moderationJoinHandler, - 1);
        pubSub.subscribe(plugin.getServerName(), moderationJoinHandler);
        pubSub.subscribe("partyjoin." + getServerName(), new PartiesPubSub(implement));
        pubSub.subscribe("join." + getServerName(), new RegularJoinHandler(implement));

        uuidTranslator = new UUIDTranslator(plugin, this);
        IProxyDataManager = new ProxyDataManagerImpl(this);
        partiesManager = new PartiesManager(this);
        permissionsManager = new PermissionsManager();
        friendsManager = new FriendsManagement(this);
	}

	@Override
	public IPermissionsManager getPermissionsManager() {
		return permissionsManager;
	}

	@Override
	public IResourcePacksManager getResourcePacksManager() {
		return resourcePacksManager;
	}

    @Override
    public IFriendsManager getFriendsManager() {
        return friendsManager;
    }

	public APIPlugin getPlugin() {
		return plugin;
	}

	public IProxyDataManager getIProxyDataManager() {
		return IProxyDataManager;
	}

	public IGameManager getGameManager() {
		return (gameApi == null) ? (this.gameApi = new GameManagerImpl(this)) : this.gameApi;
	}

	public void replaceJoinManager(IJoinManager manager) {
		this.joinManager = manager;
	}

	@Override
	public IPartiesManager getPartiesManager() {
		return partiesManager;
	}

	@Override
	public BarAPI getBarAPI() {
		return barAPI;
	}

	public IJoinManager getJoinManager() {
		return joinManager;
	}

	public Jedis getResource() {
		return plugin.databaseConnector.getResource();
	}

	public AbstractStatsManager getStatsManager(String game) {
		return new StatsManager(game);
	}

	@Override
	public AbstractShopsManager getShopsManager(String game) {
		return new ShopsManager(game, this);
	}

	@Override
	public ISettingsManager getSettingsManager() {
		return settingsManager;
	}

	@Override
	public IPlayerDataManager getPlayerManager() {
		return playerDataManager;
	}

    @Override
    public IAchievementManager getAchievementManager()
    {
        return achievementManager;
    }

	public IPubSubAPI getPubSub() {
		return pubSub;
	}

	@Override
	public IUUIDTranslator getUUIDTranslator() {
		return uuidTranslator;
	}

	public Jedis getBungeeResource() {
		return plugin.databaseConnector.getBungeeResource();
	}

	@Override
	public String getServerName() {
		return plugin.getServerName();
	}

	public DatabaseConnector getDatabase() {
		return plugin.databaseConnector;
	}
}
