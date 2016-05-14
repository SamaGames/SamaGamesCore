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
public class LegacyManager {

    private ArmorModule armorModule;
    private CooldownModule cooldownModule;

    public LegacyManager(APIPlugin plugin)
    {
        armorModule = new ArmorModule(plugin.getAPI());
        Bukkit.getPluginManager().registerEvents(armorModule, plugin);
        cooldownModule = new CooldownModule(plugin.getAPI());
        Bukkit.getPluginManager().registerEvents(cooldownModule, plugin);
    }

    public ArmorModule getArmorModule() {
        return armorModule;
    }

    public CooldownModule getCooldownModule() {
        return cooldownModule;
    }
}
