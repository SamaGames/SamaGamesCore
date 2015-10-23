package net.samagames.core.api.network;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.network.IJoinHandler;
import net.samagames.api.pubsub.IPacketsReceiver;
import net.samagames.core.ApiImplementation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ModerationJoinHandler implements IJoinHandler, IPacketsReceiver
{

    private final HashMap<UUID, UUID> teleportTargets = new HashMap<>();
    private final JoinManagerImplement manager;
    private final ApiImplementation api;

    public ModerationJoinHandler(ApiImplementation api)
    {
        this.api = api;
        manager = (JoinManagerImplement) api.getJoinManager();
    }

    @Override
    public void onModerationJoin(Player player)
    {
        player.sendMessage(ChatColor.GOLD + "Vous avez rejoint cette arène en mode modération.");
        player.setGameMode(GameMode.SPECTATOR);

        if (teleportTargets.containsKey(player.getUniqueId()))
        {
            UUID target = teleportTargets.get(player.getUniqueId());
            Player tar = Bukkit.getPlayer(target);
            if (tar != null)
                player.teleport(tar);
            teleportTargets.remove(player.getUniqueId());
        }

        for(Player gamePlayer : Bukkit.getOnlinePlayers())
            gamePlayer.hidePlayer(player);
    }

    @Override
    public void receive(String channel, String packet)
    {
        String[] args = StringUtils.split(packet, " ");
        String id = args[1];
        UUID uuid = UUID.fromString(id);
        boolean isModo = SamaGamesAPI.get().getPermissionsManager().hasPermission(uuid, "mod.tp");

        if (isModo)
            manager.addModerator(uuid);

        if (packet.startsWith("teleport"))
        {
            try
            {
                UUID target = UUID.fromString(args[2]);
                if (isModo)
                {
                    teleportTargets.put(uuid, target);
                }
            } catch (Exception ignored)
            {
            }
        }

        //On attend un peu avant de tp
        api.getPlugin().getExecutor().schedule(() -> api.getProxyDataManager().getProxiedPlayer(uuid).connect(SamaGamesAPI.get().getServerName()), 100, TimeUnit.MILLISECONDS);
    }
}
