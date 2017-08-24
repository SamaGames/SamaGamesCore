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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
public class ModerationJoinHandler implements IJoinHandler, IPacketsReceiver
{

    private final HashMap<UUID, UUID> teleportTargets = new HashMap<>();
    private final JoinManagerImplement manager;
    private final ApiImplementation api;

    public ModerationJoinHandler(ApiImplementation api)
    {
        this.api = api;
        manager = api.getJoinManager();
    }

    @Override
    public void onModerationJoin(Player player)
    {
        player.sendMessage(ChatColor.GOLD + "Vous avez rejoint cette arène en mode modération.");
        player.setGameMode(GameMode.SPECTATOR);

        List<Player> players = new ArrayList<>();
        players.addAll(Bukkit.getOnlinePlayers());
        players.stream().filter(gamePlayer -> !gamePlayer.getName().equals(player.getName())).forEach(gamePlayer -> {
            gamePlayer.hidePlayer(player);
        });

        if (teleportTargets.containsKey(player.getUniqueId()))
        {
            UUID target = teleportTargets.get(player.getUniqueId());
            Player tar = Bukkit.getPlayer(target);
            if (tar != null)
                player.teleport(tar);
            teleportTargets.remove(player.getUniqueId());
        }
    }

    @Override
    public void receive(String channel, String packet)
    {
        if(packet.startsWith("moderator"))
        {
            String[] args = StringUtils.split(packet, " ");
            String id = args[1];
            UUID uuid = UUID.fromString(id);
            manager.addModerator(uuid);
        }

        if (packet.startsWith("teleport"))
        {
            String[] args = StringUtils.split(packet, " ");
            String id = args[1];
            UUID uuid = UUID.fromString(id);

            try
            {
                UUID target = UUID.fromString(args[2]);
                teleportTargets.put(uuid, target);

            } catch (Exception ignored)
            {
            }

            //On attend un peu avant de tp
            api.getPlugin().getExecutor().schedule(() -> api.getPlayerManager().connectToServer(uuid, SamaGamesAPI.get().getServerName()), 50, TimeUnit.MILLISECONDS);
        }
    }
}
