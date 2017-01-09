package net.samagames.core.api.storage;

import net.samagames.core.APIPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 08/01/2017
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class StorageManager {

    private APIPlugin plugin;

    public StorageManager(APIPlugin plugin)
    {

        this.plugin = plugin;

        File file = new File(plugin.getDataFolder().getAbsoluteFile().getParentFile().getParentFile(), "spigot.yml");
        saveFile(file, "test");
    }

    public void saveGameFile(File file, String game, String player)
    {
        saveFile(file, game + "/" + player);
    }

    public void saveFile(File file, String folder)
    {
        String charset = "UTF-8";

        try {
            MultipartUtility multipart = new MultipartUtility(plugin.getDataUrl() + "/storage/index.php", charset);

            multipart.addFormField("Path" , folder);
            multipart.addFormField("Filename", file.getName());

            multipart.addFilePart("fileUpload", file);

            List<String> response = multipart.finish();

            //TODO check answer

            for (String line : response) {
                System.out.println(line);
            }
        } catch (IOException ignored) {
            plugin.getLogger().info(ignored.toString());
        }
    }


}
