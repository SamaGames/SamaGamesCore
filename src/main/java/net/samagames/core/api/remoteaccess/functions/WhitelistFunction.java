package net.samagames.core.api.remoteaccess.functions;

import net.samagames.core.api.remoteaccess.annotations.RemoteMethod;
import net.samagames.core.api.remoteaccess.annotations.RemoteObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.management.modelmbean.ModelMBeanOperationInfo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

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
