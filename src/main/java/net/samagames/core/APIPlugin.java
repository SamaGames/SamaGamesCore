package net.samagames.core;

import net.samagames.core.database.DatabaseConnector;
import net.samagames.core.database.RedisServer;
import net.samagames.core.listeners.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class APIPlugin extends JavaPlugin implements Listener
{

    private static APIPlugin instance;
    private final CopyOnWriteArraySet<String> ipWhiteList = new CopyOnWriteArraySet<>();
    private ApiImplementation api;
    private DatabaseConnector databaseConnector;
    private String serverName;
    private FileConfiguration configuration;
    private boolean allowJoin;
    private final String denyJoinReason = ChatColor.RED + "Serveur non initialisé.";
    private boolean serverRegistered;
    private boolean gameServer;
    private String joinPermission = null;
    private ScheduledExecutorService executor;
    private DebugListener debugListener;

    private NicknamePacketListener nicknamePacketListener;


    public static APIPlugin getInstance()
    {
        return instance;
    }

    public static void log(String message)
    {
        instance.getLogger().info(message);
    }

    public static void log(Level level, String message)
    {
        instance.getLogger().log(level, message);
    }

    public ApiImplementation getAPI()
    {
        return api;
    }

    public void onEnable()
    {
        instance = this;

        log("#==========[WELCOME TO SAMAGAMES API]==========#");
        log("# SamaGamesAPI is now loading. Please read     #");
        log("# carefully all outputs coming from it.        #");
        log("#==============================================#");

        executor = Executors.newScheduledThreadPool(16);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        log("Loading main configuration...");
        this.saveDefaultConfig();
        configuration = this.getConfig();
        gameServer = configuration.getBoolean("game-server", true);

        // Chargement de l'IPWhitelist le plus tot possible
        Bukkit.getPluginManager().registerEvents(this, this);

        serverName = configuration.getString("bungeename");
        if (serverName == null)
        {
            log(Level.SEVERE, "Plugin cannot load : ServerName is empty.");
            Bukkit.getServer().shutdown();
            return;
        }

        joinPermission = getConfig().getString("join-permission");

        File conf = new File(getDataFolder().getAbsoluteFile().getParentFile().getParentFile(), "data.yml");
        this.getLogger().info("Searching data.yml in " + conf.getAbsolutePath());
        if (!conf.exists())
        {
            log(Level.SEVERE, "Cannot find database configuration. Disabling database mode.");
            log(Level.WARNING, "Database is disabled for this session. API will work perfectly, but some plugins might have issues during run.");
            databaseConnector = new DatabaseConnector(this);
        } else
        {
            YamlConfiguration dataYML = YamlConfiguration.loadConfiguration(conf);

            String mainIp = dataYML.getString("redis-ip", "127.0.0.1");
            int mainPort = dataYML.getInt("redis-port", 6379);
            String mainPassword = dataYML.getString("redis-password", "passw0rd");
            RedisServer main = new RedisServer(mainIp, mainPort, mainPassword);

            String bungeeIp = dataYML.getString("redis-bungee-ip", "127.0.0.1");
            int bungeePort = dataYML.getInt("redis-bungee-port", 4242);
            String bungeePassword = dataYML.getString("redis-bungee-password", "passw0rd");
            RedisServer bungee = new RedisServer(bungeeIp, bungeePort, bungeePassword);

            databaseConnector = new DatabaseConnector(this, main, bungee);

        }

        api = new ApiImplementation(this);

		/*
        Loading listeners
		 */

        debugListener = new DebugListener();
        api.getJoinManager().registerHandler(debugListener, 0);
        api.getPubSub().subscribe("*", debugListener);

        //Nickname

        nicknamePacketListener = new NicknamePacketListener(this);

        Bukkit.getPluginManager().registerEvents(new PlayerDataListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatFormatter(this), this);
        if (configuration.getBoolean("disable-nature", false))
            Bukkit.getPluginManager().registerEvents(new NaturalListener(), this);
        if (configuration.getBoolean("tab-colors", true))
            Bukkit.getPluginManager().registerEvents(new TabsColorsListener(this), this);

		/*
		Loading commands
		 */

        for (String command : this.getDescription().getCommands().keySet())
        {
            try
            {
                Class clazz = Class.forName("net.samagames.core.commands.Command" + StringUtils.capitalize(command));
                Constructor<APIPlugin> ctor = clazz.getConstructor(APIPlugin.class);
                getCommand(command).setExecutor(ctor.newInstance(this));
                log("Loaded command " + command + " successfully. ");
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            if (calendar.get(Calendar.HOUR_OF_DAY) > 3 || (calendar.get(Calendar.HOUR_OF_DAY) == 3 && calendar.get(Calendar.MINUTE) >= 45))
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
            calendar.set(Calendar.HOUR_OF_DAY, 3);
            calendar.set(Calendar.MINUTE, 45);
            Date sched = calendar.getTime();

            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    Bukkit.getScheduler().runTaskTimer(instance, new Runnable()
                    {
                        int minutes = 15;
                        int seconds = 1;

                        @Override
                        public void run()
                        {
                            seconds--;
                            if (seconds < 0)
                            {
                                seconds = 59;
                                minutes--;
                            }

                            if (minutes < 0)
                            {
                                Bukkit.getServer().shutdown();

                                return;
                            }

                            if ((seconds == 0 && (minutes % 5 == 0 || minutes >= 3)) || (minutes == 0 && seconds % 10 == 0))
                                Bukkit.broadcastMessage(ChatColor.RED + "[REBOOT] Le serveur redémarre dans " + ((minutes > 0) ? minutes + "minute" + ((minutes > 1) ? "s " : " ") : "") + ((seconds > 0) ? seconds + "seconde" + ((seconds > 1) ? "s " : " ") : ""));
                        }
                    }, 20L, 20L);
                }
            }, sched);
            this.getLogger().info("Scheduled automatic reboot at : " + calendar.toString());
        } catch (Exception e)
        {
            this.getLogger().severe("CANNOT SCHEDULE AUTOMATIC SHUTDOWN.");
            e.printStackTrace();
        }

        registerServer();
        allowJoin();
    }

    public DebugListener getDebugListener()
    {
        return debugListener;
    }

    public ScheduledExecutorService getExecutor()
    {
        return executor;
    }

    public void onDisable()
    {
        String bungeename = getServerName();
        Jedis rb_jedis = databaseConnector.getBungeeResource();
        rb_jedis.hdel("servers", bungeename);
        api.getPubSub().send("servers", "stop " + bungeename);
        rb_jedis.close();
        nicknamePacketListener.close();
        databaseConnector.killConnections();
        executor.shutdownNow();
        Bukkit.getServer().shutdown();
    }

    public boolean canConnect(String ip)
    {
        return containsIp(ip);
    }

    public void refreshIps(Set<String> ips)
    {
        ipWhiteList.stream().filter(ip -> !ips.contains(ip)).forEach(ipWhiteList::remove);

        ips.stream().filter(ip -> !ipWhiteList.contains(ip)).forEach(ipWhiteList::add);
    }

    public boolean containsIp(String ip)
    {
        return ipWhiteList.contains(ip);
    }

    public void allowJoin()
    {
        allowJoin = true;
    }

    public String getServerName()
    {
        return serverName;
    }

    public void registerServer()
    {
        if (serverRegistered)
            return;

        log("Trying to register server to the proxy");
        try
        {
            String bungeename = getServerName();
            Jedis rb_jedis = databaseConnector.getBungeeResource();
            rb_jedis.hset("servers", bungeename, this.getServer().getIp() + ":" + this.getServer().getPort());
            rb_jedis.close();


            api.getPubSub().send("servers", "heartbeat " + bungeename + " " + this.getServer().getIp() + " " + this.getServer().getPort());


            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                Jedis jedis = databaseConnector.getBungeeResource();
                jedis.hset("servers", bungeename, this.getServer().getIp() + ":" + this.getServer().getPort());
                jedis.close();

                try
                {
                    for (Player player : Bukkit.getOnlinePlayers())
                    {
                        jedis.sadd("connectedonserv:" + bungeename, player.getUniqueId().toString());
                    }
                } catch (Exception ignored)
                {
                }

                api.getPubSub().send("servers", "heartbeat " + bungeename + " " + this.getServer().getIp() + " " + this.getServer().getPort());

            }, 30 * 20, 30 * 20);
        } catch (Exception ignore)
        {
            ignore.printStackTrace();
            return;
        }

        serverRegistered = true;
    }


	/*
	Listen for join
	 */

    @EventHandler
    public void onLogin(PlayerLoginEvent event)
    {
        if (!allowJoin)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + denyJoinReason);
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            event.setKickMessage(ChatColor.RED + denyJoinReason);

            return;
        }

        if (joinPermission != null && !api.getPermissionsManager().hasPermission(event.getPlayer(), joinPermission))
        {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Vous n'avez pas la permission de rejoindre ce serveur.");
        }

        if (!ipWhiteList.contains(event.getRealAddress().getHostAddress()))
        {
            event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
            event.setKickMessage(ChatColor.RED + "Erreur de connexion vers le serveur... Merci de bien vouloir ré-essayer plus tard.");
            Bukkit.getLogger().log(Level.WARNING, "An user tried to connect from IP " + event.getRealAddress().getHostAddress());
        }
    }

    public DatabaseConnector getDatabaseConnector()
    {
        return databaseConnector;
    }
}
