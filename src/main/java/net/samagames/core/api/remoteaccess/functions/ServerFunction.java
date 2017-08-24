package net.samagames.core.api.remoteaccess.functions;

import net.samagames.core.api.remoteaccess.annotations.RemoteMethod;
import net.samagames.core.api.remoteaccess.annotations.RemoteObject;
import net.samagames.tools.Reflection;
import org.bukkit.Bukkit;

import javax.management.modelmbean.ModelMBeanOperationInfo;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
