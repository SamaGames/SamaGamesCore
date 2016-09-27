package net.samagames.core.utils;

import net.samagames.tools.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;

import java.util.Map;

public class CommandBlocker
{
    private static final String MINECRAFT_PREFIX = "minecraft";
    private static final String BUKKIT_PREFIX = "bukkit";
    private static final String PROTOCOLLIB_PREFIX = "protocollib";
    private static final String SONARPET_PREFIX = "sonarpet";
    private static final String LIBSDISGUISES_PREFIX = "libsdisguises";

    public static void removeCommands()
    {
        try
        {
            // Minecraft
            removeCommand(MINECRAFT_PREFIX, "help");
            removeCommand(MINECRAFT_PREFIX, "tell");
            removeCommand(MINECRAFT_PREFIX, "me");
            removeCommand(MINECRAFT_PREFIX, "trigger");

            // Bukkit
            removeCommand(BUKKIT_PREFIX, "about", "version", "ver");
            removeCommand(BUKKIT_PREFIX, "plugins", "pl");
            removeCommand(BUKKIT_PREFIX, "help", "?");
            removeCommand(BUKKIT_PREFIX, "me");
            removeCommand(BUKKIT_PREFIX, "save-all", "save-off", "save-on");
            removeCommand(BUKKIT_PREFIX, "trigger");

            // ProtocolLib
            removeCommand(PROTOCOLLIB_PREFIX, "protocol");
            removeCommand(PROTOCOLLIB_PREFIX, "packet");
            removeCommand(PROTOCOLLIB_PREFIX, "filter");

            // SonarPet
            removeCommand(SONARPET_PREFIX, "pet");
            removeCommand(SONARPET_PREFIX, "petadmin");
            removeCommand(SONARPET_PREFIX, "ecupdate");
            removeCommand(SONARPET_PREFIX, "echopet");

            // LibsDisguises
            removeCommand(LIBSDISGUISES_PREFIX, "libsdisguises");
            removeCommand(LIBSDISGUISES_PREFIX, "disguise", "d", "dis");
            removeCommand(LIBSDISGUISES_PREFIX, "disguiseentity", "dentity", "disentity");
            removeCommand(LIBSDISGUISES_PREFIX, "disguisehelp", "dhelp", "dishelp");
            removeCommand(LIBSDISGUISES_PREFIX, "disguiseplayer", "dplayer", "displayer");
            removeCommand(LIBSDISGUISES_PREFIX, "disguiseradius", "disradius", "dradius");
            removeCommand(LIBSDISGUISES_PREFIX, "undisguise", "u", "und", "undis");
            removeCommand(LIBSDISGUISES_PREFIX, "undisguiseplayer", "undisplayer", "undplayer");
            removeCommand(LIBSDISGUISES_PREFIX, "undisguiseentity", "undisentity", "undentity");
            removeCommand(LIBSDISGUISES_PREFIX, "undisguiseradius", "undisradius", "undradius");
            removeCommand(LIBSDISGUISES_PREFIX, "disguiseclone", "disguisec", "disc", "disclone", "dclone", "clonedisguise", "clonedis", "cdisguise", "cdis");
            removeCommand(LIBSDISGUISES_PREFIX, "disguiseviewself", "dviewself", "dvs", "disguisevs", "disvs", "vsd", "viewselfdisguise", "viewselfd");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private static void removeCommand(String prefix, String... str) throws NoSuchFieldException, IllegalAccessException
    {
        SimpleCommandMap scm = ((CraftServer) Bukkit.getServer()).getCommandMap();
        Map knownCommands = (Map) Reflection.getValue(scm, true, "knownCommands");

        for (String cmd : str)
        {
            if (knownCommands.containsKey(cmd))
                knownCommands.remove(cmd);

            if (knownCommands.containsKey(prefix + ":" + cmd))
                knownCommands.remove(prefix + ":" + cmd);
        }
    }
}
