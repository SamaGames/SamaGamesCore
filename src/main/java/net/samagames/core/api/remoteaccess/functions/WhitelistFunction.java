package net.samagames.core.api.remoteaccess.functions;

import net.samagames.core.api.remoteaccess.annotations.RemoteMethod;
import net.samagames.core.api.remoteaccess.annotations.RemoteObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.management.modelmbean.ModelMBeanOperationInfo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 28/07/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */

@RemoteObject(description = "Whitelist Management")
public class WhitelistFunction {

    @RemoteMethod(description = "Add a player to the whitelist", impact = ModelMBeanOperationInfo.ACTION)
    public void addPlayer(UUID player)
    {
        Bukkit.getWhitelistedPlayers().add(Bukkit.getOfflinePlayer(player));
        Bukkit.reloadWhitelist();
        Bukkit.getLogger().info("Added player " + player + " to whitelist");
    }

    @RemoteMethod(description = "Remove a player to the whitelist", impact = ModelMBeanOperationInfo.ACTION)
    public void removePlayer(UUID player)
    {
        Bukkit.getWhitelistedPlayers().add(Bukkit.getOfflinePlayer(player));
        Bukkit.reloadWhitelist();
        Bukkit.getLogger().info("Added player " + player + " to whitelist");
    }

    @RemoteMethod(description = "Get the whitelist", impact = ModelMBeanOperationInfo.ACTION)
    public ArrayList<UUID> getWhiteList()
    {
        return Bukkit.getWhitelistedPlayers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toCollection(ArrayList::new));
    }

    @RemoteMethod(description = "Know if the whitelist is actived", impact = ModelMBeanOperationInfo.INFO)
    public boolean isWhiteListActived()
    {
        return Bukkit.hasWhitelist();
    }

    @RemoteMethod(description = "Set if the whitelist is actived", impact = ModelMBeanOperationInfo.ACTION)
    public void setWhiteListActived(boolean actived)
    {
        Bukkit.setWhitelist(actived);
    }

}
