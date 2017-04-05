package net.samagames.core.api.remoteaccess.functions;

import net.samagames.core.api.remoteaccess.annotations.RemoteMethod;
import net.samagames.core.api.remoteaccess.annotations.RemoteObject;
import net.samagames.tools.Reflection;
import org.bukkit.Bukkit;

import javax.management.modelmbean.ModelMBeanOperationInfo;
import java.lang.reflect.Field;

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
    private static Class<?> craftServerClass;
    private static Field recentTpsField;

    @RemoteMethod(description = "Get current server tps", impact = ModelMBeanOperationInfo.ACTION)
    public double tps()
    {
        try
        {
            double result = 0;
            double[] recentTps = (double[]) recentTpsField.get(craftServerClass.cast(Bukkit.getServer()));

            for(double one : recentTps)
                result += one;

            return result / recentTps.length;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    static
    {
        try
        {
            craftServerClass = Reflection.getOBCClass("CraftServer");
            recentTpsField = craftServerClass.getField("recentTps");
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
}
