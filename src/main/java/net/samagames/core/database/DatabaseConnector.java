package net.samagames.core.database;

import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class DatabaseConnector
{

    private final APIPlugin plugin;
    private JedisPool mainPool;
    private JedisPool cachePool;
    private RedisServer main;
    private RedisServer bungee;
    private WhiteListRefreshTask keeper;

    public DatabaseConnector(APIPlugin plugin)
    {
        mainPool = null;
        this.plugin = plugin;
    }

    public DatabaseConnector(APIPlugin plugin, RedisServer main, RedisServer bungee)
    {
        this.plugin = plugin;
        this.main = main;
        this.bungee = bungee;

        initiateConnections();
    }

    public Jedis getBungeeResource()
    {
        return cachePool.getResource();
    }

    public void killConnections()
    {
        cachePool.destroy();
        mainPool.destroy();
    }

    private void initiateConnections()
    {
        // Préparation de la connexion
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1024);
        config.setMaxWaitMillis(5000);

        this.mainPool = new JedisPool(config, this.main.getIp(), this.main.getPort(), 5000, this.main.getPassword());
        this.cachePool = new JedisPool(config, this.bungee.getIp(), this.bungee.getPort(), 5000, this.bungee.getPassword());

        // Init du thread

        if (keeper == null)
        {
            keeper = new WhiteListRefreshTask(plugin, this);
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, keeper, 0, 30 * 20);
        }
    }

}
