package net.samagames.core.api.games;

import net.samagames.api.games.Game;
import net.samagames.api.games.IGameManager;
import net.samagames.api.games.Status;
import net.samagames.api.network.IJoinHandler;
import net.samagames.api.network.JoinResponse;
import net.samagames.api.network.ResponseType;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

class GameLoginHandler implements IJoinHandler
{
    private final IGameManager api;

    public GameLoginHandler(IGameManager api)
    {
        this.api = api;
    }

    @Override
    public void finishJoin(Player player)
    {
        if (api.getGame() != null)
        {
            if (!api.isWaited(player.getUniqueId()))
                api.getGame().handleLogin(player);
            else
                api.getGame().handleReconnect(player);

            api.refreshArena();
        }
    }

    @Override
    public JoinResponse requestJoin(UUID player, JoinResponse response)
    {
        if (api.getGame() != null)
        {
            Game game = api.getGame();

            Pair<Boolean, String> gameResponse = game.canJoinGame(player, false);

            if (gameResponse.getKey())
            {
                response.allow();
            } else
            {
                response.disallow(gameResponse.getValue());
                return response;
            }

            if (game.getStatus() == Status.IN_GAME)
                response.disallow(ResponseType.DENY_IN_GAME);
            else if (game.getStatus() == Status.STARTING)
                response.disallow(ResponseType.DENY_NOT_READY);
            else if (game.getConnectedPlayers() >= api.getGameProperties().getMaxSlots())
                response.disallow(ResponseType.DENY_FULL);

            if (api.isReconnectAllowed() && api.isWaited(player))
            {
                response.allow();
                return response;
            }
        }

        return response;
    }

    @Override
    public JoinResponse requestPartyJoin(UUID partyLeader, Set<UUID> partyMembers, JoinResponse response)
    {
        if (api.getGame() != null)
        {
            Game game = api.getGame();

            Pair<Boolean, String> gameResponse = game.canPartyJoinGame(partyMembers);

            if (gameResponse.getKey())
            {
                response.allow();
            } else
            {
                response.disallow(gameResponse.getValue());
                return response;
            }

            if (game.getStatus() == Status.IN_GAME)
                response.disallow(ResponseType.DENY_IN_GAME);
            else if (game.getStatus() == Status.STARTING)
                response.disallow(ResponseType.DENY_NOT_READY);
            else if (game.getConnectedPlayers() >= api.getGameProperties().getMaxSlots())
                response.disallow(ResponseType.DENY_FULL);
        }

        return response;
    }

    @Override
    public void onModerationJoin(Player player)
    {
        api.getGame().handleModeratorLogin(player);
    }

    @Override
    public void onLogout(Player player)
    {
        api.onPlayerDisconnect(player);
    }
}
