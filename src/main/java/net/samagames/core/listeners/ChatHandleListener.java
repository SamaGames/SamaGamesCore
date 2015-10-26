package net.samagames.core.listeners;

import io.netty.util.internal.ConcurrentSet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.samagames.api.player.AbstractPlayerData;
import net.samagames.api.pubsub.IPacketsReceiver;
import net.samagames.api.pubsub.PendingMessage;
import net.samagames.core.APIPlugin;
import net.samagames.tools.Misc;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Silva on 25/10/2015.
 */
public class ChatHandleListener extends APIListener implements IPacketsReceiver {

    private Set<String> words;
    protected CopyOnWriteArraySet<String> blacklist = new CopyOnWriteArraySet<>();
    protected ConcurrentHashMap<UUID, MessageData> lastMessages = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<UUID, Date> mutedPlayers = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<UUID, String> muteReasons = new ConcurrentHashMap<>();

    public ChatHandleListener(APIPlugin plugin) {
        super(plugin);

        Jedis jedis = api.getBungeeResource();
        words = jedis.smembers("chat:blacklist");
        if (words == null)
            words = new ConcurrentSet<>();
        jedis.close();
    }

    public void reload()
    {
        Jedis jedis = api.getBungeeResource();
        words = jedis.smembers("chat:blacklist");
        if (words == null)
            words = new ConcurrentSet<>();
        jedis.close();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            AbstractPlayerData playerData = api.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
            String isMute = playerData.get("redis.isMute");
            if (isMute.equals("true")) {
                Date expiration = new Date(Long.valueOf(playerData.get("redis.muteExpiration")));
                String reason = playerData.get("redis.muteReason");
                addMute(event.getPlayer().getUniqueId(), expiration, reason);
            }
        });
    }

    @EventHandler
    public void onKick(PlayerKickEvent event)
    {
        onLogout(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        onLogout(event.getPlayer());
    }

    public void onLogout(Player p)
    {
        removeMute(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event)
    {
        if (event.getMessage().startsWith("/"))
            return;

        String message = event.getMessage();
        long time = System.currentTimeMillis();
        Player player = event.getPlayer();

        if (mutedPlayers.containsKey(player.getUniqueId()))
        {
            Date end = mutedPlayers.get(player.getUniqueId());
            if (end.before(new Date()))
            {
                mutedPlayers.remove(player.getUniqueId());
                muteReasons.remove(player.getUniqueId());
            } else
            {
                player.sendMessage(ChatColor.RED + "Vous êtes actuellement muet pour une durée de " + Misc.formatTime((end.getTime() - System.currentTimeMillis())));
                player.sendMessage(ChatColor.RED + "Raison : " + ChatColor.YELLOW + muteReasons.get(player.getUniqueId()));
                event.setCancelled(true);
                return;
            }
        }

        if (message.startsWith("*"))
        {
            UUID party = api.getPartiesManager().getPlayerParty(player.getUniqueId());
            if (party != null)
            {
                message = message.substring(1);
                message = message.trim();
                event.setCancelled(true);
                api.getPubSub().send(new PendingMessage("parties.message", party + " " + player.getName() + " " + message));
                return;
            }
        }

        if (api.getPermissionsManager().getApi().getUser(player.getUniqueId()).hasPermission("chatrestrict.ignore"))
            return;

        MessageData last = lastMessages.get(player.getUniqueId());
        if (last != null)
        {
            if (!api.getPermissionsManager().getApi().getUser(player.getUniqueId()).hasPermission("chat.bypass"))
            {
                if (last.isTooEarly(time))
                {
                    player.sendMessage(ChatColor.RED + "Merci de ne pas envoyer de messages trop souvent.");
                    event.setCancelled(true);
                    return;
                } else if (last.isSame(message, time))
                {
                    player.sendMessage(ChatColor.RED + "Merci de ne pas envoyer plusieurs fois le même message.");
                    event.setCancelled(true);
                    return;
                }
            }
        }

        MessageData current = new MessageData();
        current.message = message;
        current.time = time;
        if (last != null)
        {
            lastMessages.replace(player.getUniqueId(), current);
        } else
        {
            lastMessages.put(player.getUniqueId(), current);
        }

        if (message.matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"))
        {
            player.sendMessage(ChatColor.RED + "Pas d'adresse ip dans le chat !");
            event.setCancelled(true);
        } else if (Misc.getURLPattern().matcher(message).find())
        {
            player.sendMessage(ChatColor.RED + "Pas de lien dans le chat !");
            event.setCancelled(true);
        } else if (message.matches("[A-Z]{4,}"))
        {
            player.sendMessage(ChatColor.RED + "Pas de messages en majuscules !");
            event.setCancelled(true);
        }

        String check = message.toLowerCase();

        for (String w : words)
        {
            if (check.startsWith(w + "") || check.endsWith(" " + w) || check.contains(" " + w + " "))
            {
                char[] replacment = {'#', '!', '@', '?', '$'};
                Random random = new Random();
                StringBuffer buf = new StringBuffer();

                for (int i = 0; i < w.length(); i++)
                    buf.append(replacment[random.nextInt(replacment.length)]);

                message = message.replace(w, buf.toString());
            }
        }
    }

    @Override
    public void receive(String channel, String message)
    {
        if (channel.equals("mute.add"))
        {
            String[] parts = message.split(" ");
            UUID id = UUID.fromString(parts[0]);
            long end = Long.parseLong(parts[1]);
            String reason = StringUtils.join(Arrays.copyOfRange(parts, 2, parts.length), " ");

            if (Bukkit.getPlayer(id) != null)
            {
                mutedPlayers.put(id, new Date(end));
                muteReasons.put(id, reason);
            }
        } else if (channel.equals("mute.remove"))
        {
            UUID id = UUID.fromString(message);
            mutedPlayers.remove(id);
            muteReasons.remove(id);
        }
    }

    public String getReason(UUID id)
    {
        return muteReasons.get(id);
    }

    public Date getEnd(UUID id)
    {
        return mutedPlayers.get(id);
    }

    public boolean isMuted(UUID id)
    {
        if (mutedPlayers.containsKey(id))
        {
            Date end = mutedPlayers.get(id);
            if (end.before(new Date()))
            {
                removeMute(id);
            } else
            {
                return true;
            }
        }
        return false;
    }

    public void addMute(UUID id, Date end, String reason)
    {
        if (!mutedPlayers.containsKey(id))
            mutedPlayers.put(id, end);
        if (!muteReasons.containsKey(id))
            muteReasons.put(id, reason);
    }

    public void removeMute(UUID id)
    {
        if (mutedPlayers.containsKey(id))
            mutedPlayers.remove(id);
        if (muteReasons.containsKey(id))
            muteReasons.remove(id);
    }

    public static class MessageData
    {

        public String message = "";
        public long time = 0;

        public boolean isSame(String message, long time)
        {
            boolean eq = this.message.equals(message);
            if (!eq)
                return false;
            return (this.time + 15000 > time); // 15 secondes entre chaque message identique
        }

        public boolean isTooEarly(long time)
        {
            return this.time + 1500 > time;
        }
    }
}
