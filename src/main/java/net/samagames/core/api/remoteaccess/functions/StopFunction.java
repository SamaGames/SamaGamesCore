package net.samagames.core.api.remoteaccess.functions;

import net.samagames.core.api.remoteaccess.annotations.RemoteMethod;
import net.samagames.core.api.remoteaccess.annotations.RemoteObject;
import org.bukkit.Bukkit;

import javax.management.modelmbean.ModelMBeanOperationInfo;

/**
 * /`\  ___  /`\
 * \d `"\:/"` b/
 * /`.--. ` .--.`\
 * |/ __ \ / __ \|
 * ( ((o) V (o)) )
 * |\`""`/^\`""`/|
 * \ `--'\ /'--` /
 * /`-._  `  _.-`\
 * / /.:.:.:.:.:.\ \
 * ; |.:.:.:.:.:.:.| ;
 * | |:.:.:.:.:.:.:| |
 * | |.:.:.:.:.:.:.| |
 * | |:.:.:.:.:.:.:| |
 * \/\.:.:.:.:.:.:./\/
 * _`).-.-:-.-.(`_
 * ,=^` |=  =| |=  =| `^=,
 * /     \=/\=/ \=/\=/     \
 * `  `   `  `
 * Created by Silvanosky on 31/12/2016
 */

@RemoteObject(description = "Stop Management")
public class StopFunction {

    @RemoteMethod(description = "Shutdown a server", impact = ModelMBeanOperationInfo.ACTION)
    public void stop()
    {
        Bukkit.getServer().shutdown();
    }

}
