package net.samagames.core.commands;

import net.samagames.api.SamaGamesAPI;
import net.samagames.core.APIPlugin;
import net.samagames.tools.JsonModMessage;
import net.samagames.tools.ModChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * )\._.,--....,'``.
 * .b--.        /;   _.. \   _\  (`._ ,.
 * `=,-,-'~~~   `----(,_..'--(,_..'`-.;.'
 * <p>
 * Created by Jérémy L. (BlueSlime) on 09/02/2017
 */
public class CommandAnticheatreport extends AbstractCommand
{
    public CommandAnticheatreport(APIPlugin plugin)
    {
        super(plugin);
    }

    @Override
    protected boolean onCommand(CommandSender sender, String label, String[] arguments)
    {
        if (!(sender instanceof ConsoleCommandSender) && !sender.isOp())
            return true;

        if (arguments.length != 2)
            return true;

        String cheat = arguments[0];
        String player = arguments[1];
        String server = SamaGamesAPI.get().getServerName();

        new JsonModMessage("AntiCheat", ModChannel.REPORT, ChatColor.DARK_RED, player + "#####" + server + "#####Possible usage de la triche : " + cheat).send();

        return true;
    }
}
