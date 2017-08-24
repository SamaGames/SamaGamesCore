package net.samagames.core.api.settings;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.persistanceapi.beans.players.PlayerSettingsBean;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
public class ImpPlayerSettings extends PlayerSettings {

    private GameServiceManager gameServiceManager;
    public ImpPlayerSettings(GameServiceManager gameServiceManager, PlayerData playerData) {
        super(playerData);
        this.gameServiceManager = gameServiceManager;
    }

    public ImpPlayerSettings(GameServiceManager gameServiceManager, PlayerData playerData, PlayerSettingsBean bean) {
        super(playerData, bean);
        this.gameServiceManager = gameServiceManager;
    }

    @Override
    public void update() {
        Player player = Bukkit.getPlayer(playerData.getPlayerID());

        try {
            //Update SQL
            gameServiceManager.setPlayerSettings(playerData.getPlayerBean(), this);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            //Comamnd type
            out.writeUTF("settingsChanges");
            //The player to refresh settings on bungee
            out.writeUTF(player.getUniqueId().toString());
            //Send data on network channel
            player.sendPluginMessage(APIPlugin.getInstance(), "Network", out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh()
    {
        try {
            //LOAD SQL
            PlayerSettingsBean playerSettings1 = gameServiceManager.getPlayerSettings(playerData.getPlayerBean());
            copy(playerSettings1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
