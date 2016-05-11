package net.samagames.core.commands;

import net.samagames.core.APIPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 11/05/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class CommandRejoinTemplate extends AbstractCommand {

    public CommandRejoinTemplate(APIPlugin plugin) {
        super(plugin);
    }

    @Override
    protected boolean onCommand(CommandSender sender, String label, String[] arguments) {
        if (arguments == null || arguments.length < 2)
            return true;

        try
        {
            plugin.getAPI().getGameManager().rejoinTemplateQueue((Player) sender);
        }
        catch (Exception ignored) {}

        return true;
    }
}
