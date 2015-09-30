package net.samagames.core.api.games.themachine.messages;

import net.samagames.api.games.themachine.ICoherenceMachine;
import net.samagames.api.games.themachine.messages.IMessageManager;
import net.samagames.api.games.themachine.messages.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
    public Message writePlayerJoinToAll(Player player)
    {
        return new Message(String.valueOf(ChatColor.YELLOW) + player.getName() + " a rejoint la partie ! " + ChatColor.DARK_GRAY + "[" + ChatColor.RED + this.machine.getGame().getConnectedPlayers() + ChatColor.DARK_GRAY + "/" + ChatColor.RED + this.machine.getGameProperties().getMaxSlots() + ChatColor.DARK_GRAY + "]", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writeWelcomeInGameToPlayer(Player player)
    {
        return new Message(ChatColor.GOLD + "\nBienvenue en " + ChatColor.RED + this.machine.getGame().getGameName() + ChatColor.GOLD + " !").display(player);
    }

    @Override
    public Message writeGameStartIn(int remainingTime)
    {
        return new Message(ChatColor.YELLOW + "Début du jeu dans " + ChatColor.RED + remainingTime + " secondes" + ChatColor.YELLOW + ".", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writeNotEnougthPlayersToStart()
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
        return new Message(ChatColor.WHITE + player.getName() + " s'est déconnecté du jeu.", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writePlayerDisconnected(Player player, int remainingTime)
    {
        return new Message(ChatColor.RED + player.getName() + " s'est déconnecté ! Il a " + remainingTime + " minutes pour revenir.", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writePlayerReconnected(Player player)
    {
        return new Message(ChatColor.GREEN + player.getName() + " s'est reconnecté !", this.machine.getGameTag()).displayToAll();
    }

    @Override
    public Message writePlayerReconnectTimeOut(Player player)
    {
        return new Message(ChatColor.RED + player.getName() + " ne s'est pas reconnecté à temps !");
    }

    @Override
    public Message getArenaFull()
    {
        return new Message(ChatColor.RED + "L'arène est pleine.");
    }
}
