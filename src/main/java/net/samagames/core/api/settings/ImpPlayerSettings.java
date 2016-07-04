package net.samagames.core.api.settings;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.GameServiceManager;
import net.samagames.persistanceapi.beans.players.PlayerSettingsBean;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 27/04/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
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
