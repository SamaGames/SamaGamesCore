package net.samagames.core.api.games.themachine;

import net.samagames.api.games.Game;
import net.samagames.api.games.IGameProperties;
import net.samagames.api.games.themachine.ICoherenceMachine;
import net.samagames.api.games.themachine.messages.IMessageManager;
import net.samagames.api.games.themachine.messages.ITemplateManager;
import net.samagames.core.api.games.themachine.messages.MessageManagerImpl;
import net.samagames.core.api.games.themachine.messages.TemplateManagerImpl;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CoherenceMachineImpl implements ICoherenceMachine
{
    private final Game game;
    private final IGameProperties gameProperties;
    private final IMessageManager messageManager;
    private final ITemplateManager templateManager;
    private String startCountdownCatchPhrase;
    private String nameShortcut;

    public CoherenceMachineImpl(Game game, IGameProperties gameProperties)
    {
        this.game = game;
        this.gameProperties = gameProperties;

        this.messageManager = new MessageManagerImpl(this);
        this.templateManager = new TemplateManagerImpl();

        this.startCountdownCatchPhrase = "Préparez-vous à jouer !";
        this.nameShortcut = null;
    }

    @Override
    public void setStartCountdownCatchPhrase(String phrase)
    {
        this.startCountdownCatchPhrase = phrase;
        this.game.getBeginTimer().setCatchPhrase(phrase);
    }

    @Override
    public void setNameShortcut(String shortcut)
    {
        this.nameShortcut = shortcut;
    }

    @Override
    public String getGameTag()
    {
        return ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + (this.nameShortcut != null ? this.nameShortcut : this.game.getGameName()) + ChatColor.DARK_AQUA + "]" + ChatColor.RESET;
    }

    @Override
    public IMessageManager getMessageManager()
    {
        return this.messageManager;
    }

    @Override
    public ITemplateManager getTemplateManager()
    {
        return this.templateManager;
    }

    @Override
    public Game getGame()
    {
        return this.game;
    }

    @Override
    public IGameProperties getGameProperties()
    {
        return this.gameProperties;
    }

    @Override
    public ItemStack getLeaveItem()
    {
        ItemStack door = new ItemStack(Material.WOOD_DOOR, 1);
        ItemMeta meta = door.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Quitter le jeu");
        door.setItemMeta(meta);

        return door;
    }

    @Override
    public String getStartCountdownCatchPhrase()
    {
        return this.startCountdownCatchPhrase;
    }

    @Override
    public String getNameShortcut()
    {
        return this.nameShortcut;
    }
}
