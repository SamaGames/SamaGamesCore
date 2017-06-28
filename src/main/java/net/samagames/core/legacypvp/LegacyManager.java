package net.samagames.core.legacypvp;

import net.samagames.core.APIPlugin;
import net.samagames.core.legacypvp.armors.ArmorModule;
import net.samagames.core.legacypvp.cooldown.CooldownModule;
import org.bukkit.Bukkit;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 14/05/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class LegacyManager
{
    public LegacyManager(APIPlugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(new ArmorModule(plugin.getAPI()), plugin);
        Bukkit.getPluginManager().registerEvents(new CooldownModule(plugin.getAPI()), plugin);
    }
}
