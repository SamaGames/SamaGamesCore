package net.samagames.core.api.games.themachine.messages;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.themachine.ICoherenceMachine;
import net.samagames.api.games.themachine.messages.IMessageManager;
import net.samagames.api.games.themachine.messages.Message;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MessageManagerImpl implements IMessageManager
{
    private final ICoherenceMachine machine;

    public MessageManagerImpl(ICoherenceMachine machine)
    {
        this.machine = machine;
    }

    @Override
    public Message writeCustomMessage(String text, boolean gameTag)
    {
        if (gameTag)
            return new Message(text, this.machine.getGameTag()).displayToAll();
        else
            return new Message(text).displayToAll();
    }

    @Override
    public Message writePlayerJoinToAll(Player player, boolean playerCount)
    {
        return new Message(String.valueOf(ChatColor.YELLOW) + this.getName(player.getUniqueId()) + " a rejoint la partie !" + (playerCount ? " " + ChatColor.DARK_GRAY + "[" + ChatColor.RED + this.machine.getGame().getConnectedPlayers() + ChatColor.DARK_GRAY + "/" + ChatColor.RED + this.machine.getGameProperties().getMaxSlots() + ChatColor.DARK_GRAY + "]" : ""), this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writeWelcomeInGameToPlayer(Player player)
    {
        return new Message(ChatColor.GOLD + "\nBienvenue en " + ChatColor.RED + this.machine.getGame().getGameName() + ChatColor.GOLD + " !").display(player);
    }

    @Override
    public Message writeGameStartIn(int remainingTime)
    {
        return new Message(ChatColor.YELLOW + "Début du jeu dans " + ChatColor.RED + remainingTime + " seconde" + (remainingTime > 1 ? "s" : "") + ChatColor.YELLOW + ".", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writeNotEnoughPlayersToStart()
    {
        return new Message(ChatColor.RED + "Il n'y a plus assez de joueurs pour commencer.", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writeGameStart()
    {
        return new Message("La partie commence !", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writePlayerQuited(Player player)
    {
        return new Message(ChatColor.WHITE + this.getName(player.getUniqueId()) + " s'est déconnecté du jeu.", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writePlayerDisconnected(Player player, int remainingTime)
    {
        return new Message(ChatColor.RED + this.getName(player.getUniqueId()) + " s'est déconnecté ! Il a " + formatTime(remainingTime) + " pour revenir.", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writePlayerReconnected(Player player)
    {
        return new Message(ChatColor.GREEN + this.getName(player.getUniqueId()) + " s'est reconnecté !", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writePlayerReconnectTimeOut(OfflinePlayer player)
    {
        return new Message(ChatColor.RED + this.getName(player.getUniqueId()) + " ne s'est pas reconnecté à temps !", this.machine.getGameTag()).displayToAll();
    }

    private String getName(UUID player)
    {
        return SamaGamesAPI.get().getPlayerManager().getPlayerData(player).getDisplayName();
    }

    private String formatTime(long time)
    {
        long days = TimeUnit.MILLISECONDS.toDays(time);
        time -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

        String ret = "";
        if (days > 0)
            ret += days + " jour" + (days > 1 ? "s" : "") + " ";

        if (hours > 0)
            ret += hours + " heure" + (hours > 1 ? "s" : "") + " ";

        if (minutes > 0)
            ret += minutes + " minute" + (minutes > 1 ? "s" : "") + " ";

        if (seconds > 0)
            ret += seconds + " seconde" + (seconds > 1 ? "s" : "");

        if (ret.isEmpty() && minutes == 0)
            ret += "moins d'une minute";

        return ret;
    }
}
