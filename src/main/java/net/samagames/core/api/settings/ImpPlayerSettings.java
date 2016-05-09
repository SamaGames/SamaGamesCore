package net.samagames.core.api.settings;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerData;
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

    public ImpPlayerSettings(PlayerData playerData) {
        super(playerData);
    }

    public ImpPlayerSettings(PlayerData playerData, PlayerSettingsBean bean) {
        super(playerData, bean);
    }

    @Override
    public void update() {
        super.update();
        Player player = Bukkit.getPlayer(playerData.getPlayerID());

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        //Comamnd type
        out.writeUTF("settingsChanges");
        //The player to refresh settings on bungee
        out.writeUTF(player.getUniqueId().toString());
        //Send data on network channel
        player.sendPluginMessage(APIPlugin.getInstance(), "Network", out.toByteArray());
    }
}
