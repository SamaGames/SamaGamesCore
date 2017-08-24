package net.samagames.core.api.storage;

import net.samagames.core.APIPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        String target = plugin.getDataUrl() + "/storage/index.php?path=" + folder;
        try {
            MultipartUtility multipart = new MultipartUtility(target, charset);

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
