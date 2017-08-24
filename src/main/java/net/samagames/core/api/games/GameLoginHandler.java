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
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

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
            if(api.getGame().isGameStarted())
            {
                if (api.isReconnectAllowed(player.getUniqueId()) && api.isWaited(player.getUniqueId()))
                    api.onPlayerReconnect(player);
            }
            else
            {
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
            }
            else
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
            List<UUID> members = SamaGamesAPI.get().getPartiesManager().getParty(party).getPlayers();
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

        if (api.isWaited(player) && api.isReconnectAllowed(player))
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
