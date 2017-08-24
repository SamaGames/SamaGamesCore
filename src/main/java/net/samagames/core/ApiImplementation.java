package net.samagames.core;

import in.ashwanthkumar.slack.webhook.Slack;
import net.samagames.api.SamaGamesAPI;
import net.samagames.core.api.achievements.AchievementManager;
import net.samagames.core.api.friends.FriendsManager;
import net.samagames.core.api.games.GameManager;
import net.samagames.core.api.gui.GuiManager;
import net.samagames.core.api.hydroangeas.HydroangeasManager;
import net.samagames.core.api.names.UUIDTranslator;
import net.samagames.core.api.network.JoinManagerImplement;
import net.samagames.core.api.network.ModerationJoinHandler;
import net.samagames.core.api.network.PartiesPubSub;
import net.samagames.core.api.network.RegularJoinHandler;
import net.samagames.core.api.options.ServerOptions;
import net.samagames.core.api.parties.PartiesManager;
import net.samagames.core.api.parties.PartyListener;
import net.samagames.core.api.permissions.PermissionManager;
import net.samagames.core.api.player.PlayerDataManager;
import net.samagames.core.api.pubsub.PubSubAPI;
import net.samagames.core.api.remoteaccess.RemoteAccessManager;
import net.samagames.core.api.remoteaccess.functions.ServerFunction;
import net.samagames.core.api.remoteaccess.functions.StopFunction;
import net.samagames.core.api.remoteaccess.functions.WhitelistFunction;
import net.samagames.core.api.resourcepacks.ResourcePacksManagerImpl;
import net.samagames.core.api.settings.SettingsManager;
import net.samagames.core.api.shops.ShopsManager;
import net.samagames.core.api.stats.StatsManager;
import net.samagames.core.api.storage.StorageManager;
import net.samagames.core.listeners.pubsub.GlobalUpdateListener;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.tools.SkyFactory;
import net.samagames.tools.cameras.CameraManager;
import net.samagames.tools.npc.NPCManager;
import redis.clients.jedis.Jedis;

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
public class ApiImplementation extends SamaGamesAPI
{
    private static final String SLACK_LOGS_WEBHOOK_URL = "https://hooks.slack.com/services/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";

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
    private final ShopsManager shopsManager;
    private final NPCManager npcManager;
    private final CameraManager cameraManager;
    private final AchievementManager achievementManager;
    private GameManager gameManager;

    private StorageManager storageManager;

    private RemoteAccessManager remoteAccessManager;

    private final ServerOptions serverOptions;

    private boolean keepCache = false;

    public ApiImplementation(APIPlugin plugin)
    {
        super(plugin);

        this.plugin = plugin;

        this.pubSub = new PubSubAPI(this);
        //TODO redo
        GlobalUpdateListener listener = new GlobalUpdateListener(plugin);
        this.pubSub.subscribe("groupchange", listener);
        this.pubSub.subscribe("global", listener);
        this.pubSub.subscribe("networkEvent_WillQuit", listener);
        this.pubSub.subscribe(plugin.getServerName(), listener);
        this.pubSub.subscribe("commands.servers." + getServerName(), new RemoteCommandsHandler(plugin));
        this.pubSub.subscribe("commands.servers.all", new RemoteCommandsHandler(plugin));

        this.serverOptions = new ServerOptions();

        storageManager = new StorageManager(plugin);

        npcManager = new NPCManager(this);
        achievementManager = new AchievementManager(this);

        this.statsManager = new StatsManager(this);

        JoinManagerImplement implement = new JoinManagerImplement(this);
        this.joinManager = implement;

        skyFactory = new SkyFactory(plugin);
        cameraManager = new CameraManager(this);

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
        permissionsManager = new PermissionManager(this);
        friendsManager = new FriendsManager(this);
        this.shopsManager = new ShopsManager(this);

        this.remoteAccessManager = new RemoteAccessManager();
        try {
            remoteAccessManager.registerMBean(new WhitelistFunction());
            remoteAccessManager.registerMBean(new StopFunction());
            remoteAccessManager.registerMBean(new ServerFunction());
        } catch (Exception e) {
            e.printStackTrace();
        }

        PartyListener partyListener = new PartyListener(plugin, getPartiesManager());
        this.pubSub.subscribe("parties.disband", partyListener);
        this.pubSub.subscribe("parties.leave", partyListener);
        this.pubSub.subscribe("parties.kick", partyListener);
        this.pubSub.subscribe("parties.join", partyListener);
        this.pubSub.subscribe("parties.lead", partyListener);
    }

    public void onShutdown()
    {
        this.playerDataManager.onShutdown();

        this.pubSub.disable();
    }

    @Override
    public PermissionManager getPermissionsManager()
    {
        return permissionsManager;
    }

    @Override
    public NPCManager getNPCManager()
    {
        return npcManager;
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
    public CameraManager getCameraManager()
    {
        return cameraManager;
    }

    @Override
    public Slack getSlackLogsPublisher()
    {
        return getSlackPublisher(SLACK_LOGS_WEBHOOK_URL, "_developpement-logs");
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
    public ShopsManager getShopsManager()
    {
        return this.shopsManager;
    }

    @Override
    public GuiManager getGuiManager()
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
    public AchievementManager getAchievementManager()
    {
        return achievementManager;
    }

    public PubSubAPI getPubSub()
    {
        return pubSub;
    }

    @Override
    public UUIDTranslator getUUIDTranslator()
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

    public HydroangeasManager getHydroangeasManager()
    {
        return plugin.getHydroangeasManager();
    }

    @Override
    public String getServerName()
    {
        return plugin.getServerName();
    }

    public boolean isKeepCache() {
        return keepCache;
    }

    public void setKeepCache(boolean keepCache) {
        this.keepCache = keepCache;
    }

    public RemoteAccessManager getRemoteAccessManager() {
        return remoteAccessManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    private static Slack getSlackPublisher(String webhookUrl, String targetChannel)
    {
        return new Slack(webhookUrl).icon(":smiley_cat:").sendToChannel(targetChannel).displayName("Meow");
    }
}
