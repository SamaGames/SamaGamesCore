package net.samagames.core.api.remoteaccess.functions;

import net.samagames.core.api.remoteaccess.annotations.RemoteMethod;
import net.samagames.core.api.remoteaccess.annotations.RemoteObject;
import net.samagames.tools.Reflection;
import org.bukkit.Bukkit;

import javax.management.modelmbean.ModelMBeanOperationInfo;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 02/01/2017
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
@RemoteObject(description = "Internal server Management")
public class ServerFunction
{
    private static Method getServerMethod;
    private static Field recentTpsField;

    @RemoteMethod(description = "Get current server tps", impact = ModelMBeanOperationInfo.ACTION)
    public double tps()
    {
        try
        {
            double result = 0;
            double[] recentTps = (double[]) recentTpsField.get(getServerMethod.invoke(null));

            for(double one : recentTps)
                result += one;

            return result / recentTps.length;
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    static
    {
        try
        {
            Class<?> minecraftServerClass = Reflection.getNMSClass("MinecraftServer");
            getServerMethod = minecraftServerClass.getMethod("getServer");
            recentTpsField = minecraftServerClass.getField("recentTps");
        }
        catch (NoSuchFieldException | NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }
}
