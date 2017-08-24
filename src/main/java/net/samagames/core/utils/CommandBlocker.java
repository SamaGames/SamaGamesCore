package net.samagames.core.utils;

import net.samagames.tools.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class CommandBlocker
{
    private static final String MINECRAFT_PREFIX = "minecraft";
    private static final String BUKKIT_PREFIX = "bukkit";
    private static final String SPIGOT_PREFIX = "spigot";
    private static final String PROTOCOLLIB_PREFIX = "protocollib";
    private static final String SONARPET_PREFIX = "sonarpet";
    private static final String LIBSDISGUISES_PREFIX = "libsdisguises";
    private static final String PROTOCOLSUPPORT_PREFIX = "protocolsupport";
    private static final String VIAVERSION_PREFIX = "viaversion";
    private static final String WORLDEDIT_PREFIX = "worldedit";

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
            removeCommand(BUKKIT_PREFIX, "about", "version", "ver", "icanhasbukkit");
            removeCommand(BUKKIT_PREFIX, "plugins", "pl");
            removeCommand(BUKKIT_PREFIX, "help", "?");
            removeCommand(BUKKIT_PREFIX, "me");
            removeCommand(BUKKIT_PREFIX, "save-all", "save-off", "save-on");
            removeCommand(BUKKIT_PREFIX, "trigger");

            // Spigot
            removeCommand(SPIGOT_PREFIX, "restart");

            // ProtocolLib
            removeCommand(PROTOCOLLIB_PREFIX, "*");

            // SonarPet
            removeCommand(SONARPET_PREFIX, "*");

            // LibsDisguises
            removeCommand(LIBSDISGUISES_PREFIX, "*");
            // ProtocolSupport
            removeCommand(PROTOCOLSUPPORT_PREFIX, "*");

            // ViaVersion
            removeCommand(VIAVERSION_PREFIX, "*");

            // WorldEdit
            removeCommand(WORLDEDIT_PREFIX, "*");
        }
        catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }

    private static void removeCommand(String prefix, String... str) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        SimpleCommandMap scm = getCommandMap();
        Map knownCommands = (Map) Reflection.getValue(scm, true, "knownCommands");

        for (String cmd : str)
        {
            if (cmd.equals("*"))
            {
                for (String knownCommand : new HashSet<String>(knownCommands.keySet()))
                {
                    if (knownCommand.startsWith(prefix))
                    {
                        knownCommands.remove(knownCommand);

                        if (knownCommands.containsKey(":") && knownCommands.containsKey(knownCommand.split(":")[1]))
                            knownCommands.remove(knownCommand.split(":")[1]);
                    }
                }
            }
            else
            {
                if (knownCommands.containsKey(cmd))
                    knownCommands.remove(cmd);

                if (knownCommands.containsKey(prefix + ":" + cmd))
                    knownCommands.remove(prefix + ":" + cmd);
            }
        }
    }

    private static SimpleCommandMap getCommandMap() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Class<?> craftServerClass = Reflection.getOBCClass("CraftServer");
        Method getCommandMapMethod = craftServerClass.getMethod("getCommandMap");

        return (SimpleCommandMap) getCommandMapMethod.invoke(craftServerClass.cast(Bukkit.getServer()));
    }
}
