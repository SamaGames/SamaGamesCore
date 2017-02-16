package net.samagames.core.listeners.general;

import net.md_5.bungee.api.ChatColor;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamePlayer;
import net.samagames.api.games.Status;
import net.samagames.api.pubsub.IPacketsReceiver;
import net.samagames.api.pubsub.PendingMessage;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.parties.Party;
import net.samagames.core.api.permissions.PermissionEntity;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.players.SanctionBean;
import net.samagames.tools.Misc;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Silva on 25/10/2015.
 */
public class ChatHandleListener extends APIListener implements IPacketsReceiver {

    private ConcurrentHashMap<String, String> blacklist = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, MessageData> lastMessages = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, Date> mutedPlayers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, String> muteReasons = new ConcurrentHashMap<>();

    public ChatHandleListener(APIPlugin plugin)
    {
        super(plugin);

        Jedis jedis = api.getBungeeResource();

        for (String blacklisted : jedis.smembers("chat:blacklist"))
        {
            if (blacklisted.contains("="))
                blacklist.put(blacklisted.split("=")[0], blacklisted.split("=")[1]);
            else
                blacklist.put(blacklisted, null);
        }

        jedis.close();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerData playerData = api.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
            SanctionBean mute = playerData.getMuteSanction();
            if (mute != null && !mute.isDeleted()) {
                addMute(event.getPlayer().getUniqueId(), mute.getExpirationTime(), mute.getReason());
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

    private String replaceColors(String message)
    {
        String s = message;
        for (org.bukkit.ChatColor color : org.bukkit.ChatColor.values())
        {
            s = s.replaceAll("(?i)&" + color.getChar(), "" + color);
        }
        return s;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatFormat(AsyncPlayerChatEvent event)
    {
        Player p = event.getPlayer();
        PermissionEntity user = api.getPermissionsManager().getPlayer(p.getUniqueId());
        PlayerData playerData = api.getPlayerManager().getPlayerData(p.getUniqueId());
        String format = "<display><prefix><name><suffix>: ";

        String display = replaceColors(user.getDisplayTag());
        String prefix = replaceColors(user.getDisplayPrefix());
        String suffix = replaceColors(user.getDisplaySuffix());

        String tmp = format;
        tmp = tmp.replaceAll("<display>", "" + display + org.bukkit.ChatColor.WHITE);
        tmp = tmp.replaceAll("<prefix>", "" + prefix);
        tmp = tmp.replaceAll("<name>", "" + playerData.getDisplayName());
        tmp = tmp.replaceAll("<suffix>", "" + suffix);

        if (p.hasPermission("tracker.famous") || p.hasPermission("network.admin"))
        {
            tmp += replaceColors(event.getMessage());
        } else
        {
            tmp += event.getMessage().replaceAll("&r", "");
        }

        event.setFormat(tmp.replace("%", "%%"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event)
    {
        if (event.getMessage().startsWith("/"))
            return;

        String message = event.getMessage();
        long time = System.currentTimeMillis();
        Player player = event.getPlayer();

        //TODO rewrite mute system
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
            Party party = api.getPartiesManager().getPartyForPlayer(player.getUniqueId());
            if (party != null)
            {
                message = message.substring(1);
                message = message.trim();
                event.setCancelled(true);
                api.getPubSub().send(new PendingMessage("parties.message", party.getParty() + " " + player.getName() + " " + message));
                return;
            }
        }

        message = message.replaceAll("<3", "\u2764");

        event.setMessage(message);

        if (SamaGamesAPI.get().getGameManager().getGame() != null && SamaGamesAPI.get().getGameManager().getGame().getStatus() == Status.FINISHED && event.getMessage().equalsIgnoreCase("gg"))
            Bukkit.getScheduler().runTask(this.plugin, () -> SamaGamesAPI.get().getAchievementManager().getAchievementByID(21).unlock(player.getUniqueId()));

        PermissionEntity user = api.getPermissionsManager().getPlayer(player.getUniqueId());

        if (user.hasPermission("api.chat.bypass"))
            return;

        MessageData last = lastMessages.get(player.getUniqueId());
        if (last != null)
        {
            if (last.isTooEarly(time))
            {
                player.sendMessage(ChatColor.RED + "Merci de ne pas envoyer de messages trop souvent.");
                event.setCancelled(true);
                return;
            }
            else if (last.isSame(message, time))
            {
                player.sendMessage(ChatColor.RED + "Merci de ne pas envoyer plusieurs fois le même message.");
                event.setCancelled(true);
                return;
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
        }
        else if (Misc.getURLPattern().matcher(message).find())
        {
            player.sendMessage(ChatColor.RED + "Pas de lien dans le chat !");
            event.setCancelled(true);
        }
        else if (message.matches("[A-Z]{4,}"))
        {
            player.sendMessage(ChatColor.RED + "Pas de messages en majuscules !");
            event.setCancelled(true);
        }

        String checkBlacklisted = message.toLowerCase();
        char[] endings = {'.', ',', ';', ':', '?', '!'};

        for (String blacklistedWord : blacklist.keySet())
        {
            boolean containsWithSpecial = false;

            for (char ending : endings)
            {
                if (checkBlacklisted.contains(blacklistedWord + ending))
                {
                    containsWithSpecial = true;
                    break;
                }
            }

            if (checkBlacklisted.equals(blacklistedWord) || checkBlacklisted.startsWith(blacklistedWord + " ") || checkBlacklisted.endsWith(" " + blacklistedWord) || checkBlacklisted.contains(" " + blacklistedWord + " ") || containsWithSpecial)
            {
                if (blacklist.get(blacklistedWord) == null)
                {
                    char[] replaceChars = {'#', '!', '@', '?', '$'};
                    Random random = new Random();
                    StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < blacklistedWord.length(); i++)
                        builder.append(replaceChars[random.nextInt(replaceChars.length)]);

                    message = message.replaceAll("(?i)" + blacklistedWord, builder.toString());
                }
                else
                {
                    message = message.replaceAll("(?i)" + blacklistedWord, blacklist.get(blacklistedWord));
                }
            }
        }

        event.setMessage(message);
    }

    /**
     * Spectator's chat
     *
     * @param event Event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        if (SamaGamesAPI.get().getGameManager().getGame() == null)
            return;
        else if (!SamaGamesAPI.get().getGameManager().getGame().getStatus().equals(Status.IN_GAME))
            return;
        else if (!SamaGamesAPI.get().getGameManager().getGame().isSpectator(event.getPlayer()))
            return;
        else if (SamaGamesAPI.get().getGameManager().getGame().isModerator(event.getPlayer()))
            return;

        event.setCancelled(true);

        String finalMessage = ChatColor.GRAY + "[Spectateur] " + event.getPlayer().getName() + ": " + event.getMessage();

        SamaGamesAPI.get().getGameManager().getGame().getSpectatorPlayers().values().stream().filter(spectator -> ((GamePlayer) spectator).getPlayerIfOnline() != null).forEach(spectator -> ((GamePlayer) spectator).getPlayerIfOnline().sendMessage(finalMessage));
        Bukkit.getOnlinePlayers().stream().filter(player -> !SamaGamesAPI.get().getGameManager().getGame().hasPlayer(player)).forEach(player -> player.sendMessage(finalMessage));
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
