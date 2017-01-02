package net.samagames.core.api.remoteaccess.functions;

import net.samagames.core.api.remoteaccess.annotations.RemoteMethod;
import net.samagames.core.api.remoteaccess.annotations.RemoteObject;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;

import javax.management.modelmbean.ModelMBeanOperationInfo;

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
public class ServerFunction {

    @RemoteMethod(description = "Get current server tps", impact = ModelMBeanOperationInfo.ACTION)
    public double tps()
    {
        double result = 0;
        double[] recentTps = ((CraftServer) Bukkit.getServer()).getServer().recentTps;
        for(double one : recentTps)
            result += one;

        return result / recentTps.length;
    }

}
