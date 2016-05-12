package net.samagames.core.api.resourcepacks;

import io.netty.channel.Channel;
import net.minecraft.server.v1_9_R1.PacketPlayInResourcePackStatus;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.resourcepacks.IResourceCallback;
import net.samagames.api.resourcepacks.IResourcePacksManager;
import net.samagames.core.APIPlugin;
import net.samagames.tools.TinyProtocol;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Silvanosky
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class ResourcePacksManagerImpl implements IResourcePacksManager, Listener
{
    private final HashSet<UUID> currentlyDownloading = new HashSet<>();
    private final SamaGamesAPI api;
    private final String resetUrl;
    private String forceUrl;
    private IResourceCallback callback;

    private final static String rejectMessage = ChatColor.RED + "Il est nécessaire d'accepter le ressource pack pour jouer.";

    public ResourcePacksManagerImpl(SamaGamesAPI api)
    {
        Bukkit.getPluginManager().registerEvents(this, APIPlugin.getInstance());

        this.api = api;

        Jedis jedis = api.getBungeeResource();
        this.resetUrl = jedis.get("resourcepacks:reseturl");
        APIPlugin.getInstance().getLogger().info("Resource packs reset URL defined to " + resetUrl);
        jedis.close();
    }

    @Override
    public void forcePack(String name)
    {
        forcePack(name, null);
    }

    @Override
    public void forcePack(String name, IResourceCallback callback)
    {
        Jedis jedis = api.getBungeeResource();
        forceUrlPack(jedis.hget("resourcepack:" + name, "url"), callback);
        jedis.close();
    }

    @Override
    public void forceUrlPack(String url, IResourceCallback callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(APIPlugin.getInstance(), () -> {
            forceUrl = url;
            APIPlugin.getInstance().getLogger().info("Defined automatic resource pack : " + forceUrl);
        });

        this.callback = callback;
    }

    private void sendPack(Player player, String url)
    {
        player.setResourcePack(url);
        APIPlugin.getInstance().getLogger().info("Sending pack to " + player.getName() + " : " + url);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        if (forceUrl != null)
        {
            Bukkit.getScheduler().runTaskLater(APIPlugin.getInstance(), () -> {
                currentlyDownloading.add(player.getUniqueId());
                sendPack(player, forceUrl);

                //Kick if still downloading after 5 minutes
                Bukkit.getScheduler().runTaskLater(SamaGamesAPI.get().getPlugin(),
                        () -> {
                            if(currentlyDownloading.contains(player.getUniqueId()))
                                player.kickPlayer(rejectMessage);
                        }, 2400L);
            }, 100);
        } else
        {
            Bukkit.getScheduler().runTaskLater(APIPlugin.getInstance(), () -> {
                Jedis jedis = api.getBungeeResource();
                Long l = jedis.srem("playersWithPack", player.getUniqueId().toString());
                jedis.close();

                if (l > 0)
                    player.setResourcePack(resetUrl);
            }, 100);
        }
    }

    @EventHandler
    public void onResourcePack(PlayerResourcePackStatusEvent event)
    {
        Player player = event.getPlayer();

        if (forceUrl == null)
            return;

        if (callback != null)
            callback.callback(player, event.getStatus());

        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)
                || event.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD))
        {
            if (callback == null || callback.automaticKick(player))
            {
                Bukkit.getScheduler().runTask(SamaGamesAPI.get().getPlugin(), () -> player.kickPlayer(rejectMessage));
            }
            currentlyDownloading.remove(player.getUniqueId());
        }else if(event.getStatus().equals(PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED))
        {
            currentlyDownloading.remove(player.getUniqueId());
            Bukkit.getScheduler().runTaskAsynchronously(APIPlugin.getInstance(), () -> {
                Jedis jedis = api.getBungeeResource();
                jedis.sadd("playersWithPack", player.getUniqueId().toString());
                jedis.close();
            });
        }
    }


    @Override
    public void kickAllUndownloaded()
    {
        for (UUID id : currentlyDownloading)
        {
            Player player = Bukkit.getPlayer(id);
            if (player != null)
                player.kickPlayer(rejectMessage);
        }

        currentlyDownloading.clear();
    }
}
