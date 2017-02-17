package net.samagames.core.database;

import net.samagames.core.APIPlugin;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class DatabaseConnector
{

    private final APIPlugin plugin;
    private JedisPool cachePool;
    private RedisServer bungee;
    private WhiteListRefreshTask keeper;

    public DatabaseConnector(APIPlugin plugin)
    {
        this.plugin = plugin;
    }

    public DatabaseConnector(APIPlugin plugin, RedisServer bungee)
    {
        this.plugin = plugin;
        this.bungee = bungee;

        initiateConnection();
    }

    public Jedis getBungeeResource()
    {
        return cachePool.getResource();
    }

    public void killConnection()
    {
        cachePool.close();
        cachePool.destroy();
    }

    private void initiateConnection()
    {
        // Préparation de la connexion
        connect();

        this.plugin.getExecutor().scheduleAtFixedRate(() ->
        {
            try
            {
                cachePool.getResource().close();
            } catch (Exception e)
            {
                e.printStackTrace();
                plugin.getLogger().log(Level.SEVERE, "Error redis connection, Try to reconnect!", e);
                connect();
            }
        }, 0, 10, TimeUnit.SECONDS);
        // Init du thread

        if (keeper == null)
        {
            keeper = new WhiteListRefreshTask(plugin, this);
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, keeper, 0, 30 * 20);
        }
    }

    private void connect()
    {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(-1);
        config.setJmxEnabled(false);

        try
        {
            this.cachePool = new JedisPool(config, this.bungee.getIp(), this.bungee.getPort(), 0, this.bungee.getPassword());
            this.cachePool.getResource().close();

            this.plugin.log(Level.INFO, "Connected to database.");
        }
        catch (Exception e)
        {
            plugin.getLogger().log(Level.SEVERE, "Can't connect to the database!", e);
            Bukkit.shutdown();
        }
    }

}
