package net.samagames.core.api.games;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.IGameManager;
import net.samagames.api.games.Status;
import net.samagames.api.network.IJoinHandler;
import net.samagames.api.network.JoinResponse;
import net.samagames.api.network.ResponseType;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.network.JoinManagerImplement;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

class GameLoginHandler implements IJoinHandler
{
    private final IGameManager api;

    private JoinManagerImplement joinManager;

    public GameLoginHandler(IGameManager api)
    {
        this.api = api;
        this.joinManager = (JoinManagerImplement) ApiImplementation.get().getJoinManager();
    }

    @Override
    public void finishJoin(Player player)
    {
        if (api.getGame() != null)
        {
            if (api.isLegacyPvP())
            {
                AttributeInstance genericAttackSpeedAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);

                if (genericAttackSpeedAttribute != null)
                    genericAttackSpeedAttribute.setBaseValue(16.0D);
            }

            if(api.getGame().isGameStarted())
            {
                if (api.isReconnectAllowed(player.getUniqueId())
                        && api.isWaited(player.getUniqueId()))
                    api.onPlayerReconnect(player);
            }else{
                api.getGame().handleLogin(player);
            }

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

            response = checkState(game, response, player);
        }

        return response;
    }

    @Override
    public JoinResponse requestPartyJoin(UUID party, UUID player, JoinResponse response)
    {
        if (api.getGame() != null)
        {
            Game game = api.getGame();
            //Hope for cache
            Set<UUID> members = SamaGamesAPI.get().getPartiesManager().getPlayersInParty(party).keySet();
            Pair<Boolean, String> gameResponse = game.canPartyJoinGame(members);

            if (gameResponse.getKey())
            {
                response.allow();
            } else
            {
                response.disallow(gameResponse.getValue());
                return response;
            }

            response = checkState(game, response, player);
        }

        return response;
    }

    public JoinResponse checkState(Game game, JoinResponse response, UUID player)
    {
        if (game.getStatus() == Status.IN_GAME || game.getStatus() == Status.FINISHED)
            response.disallow(ResponseType.DENY_IN_GAME);
        else if (game.getStatus() == Status.STARTING)
            response.disallow(ResponseType.DENY_NOT_READY);
        else if (joinManager.countExpectedPlayers() + game.getConnectedPlayers() >= api.getGameProperties().getMaxSlots())
            response.disallow(ResponseType.DENY_FULL);

        if (api.isReconnectAllowed(player) && api.isWaited(player))
        {
            response.allow();
            return response;
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
        api.refreshArena();
    }
}
