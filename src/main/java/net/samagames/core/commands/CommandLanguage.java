package net.samagames.core.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.samagames.api.gui.AbstractGui;
import net.samagames.core.APIPlugin;
import net.samagames.core.api.player.PlayerData;
import net.samagames.tools.GlowEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import redis.clients.jedis.Jedis;

public class CommandLanguage extends AbstractCommand
{
    public CommandLanguage(APIPlugin plugin)
    {
        super(plugin);
    }

    @Override
    protected boolean onCommand(CommandSender sender, String label, String[] arguments)
    {
        if (!(sender instanceof Player))
            return true;

        this.plugin.getAPI().getGuiManager().openGui((Player) sender, new GuiLanguage(this.plugin));
        return true;
    }

    private static class GuiLanguage extends AbstractGui
    {
        private final APIPlugin plugin;

        public GuiLanguage(APIPlugin plugin)
        {
            this.plugin = plugin;
        }

        @Override
        public void display(Player player)
        {
            int lines = 1;
            int slot = 0;

            for(int languageId : this.plugin.getAPI().getI18n().getLanguagesNameCache().keySet())
            {
                slot++;

                if(slot == 8)
                {
                    slot = 0;
                    lines++;
                }
            }

            this.inventory = Bukkit.createInventory(null, 9 + (lines * 9) + (9 * 2), "Langages disponibles");

            this.update(player);

            player.openInventory(this.inventory);
        }

        @Override
        public void update(Player player)
        {
            int[] baseSlots = {10, 11, 12, 13, 14, 15, 16};
            int lines = 0;
            int slot = 0;

            int playerLanguageId = this.plugin.getAPI().getPlayerManager().getPlayerData(player.getUniqueId()).getLanguageId();

            for(int languageId : plugin.getAPI().getI18n().getLanguagesNameCache().keySet())
            {
                String languageName = plugin.getAPI().getI18n().getLanguagesNameCache().get(languageId);

                this.setSlotData(makeLanguageIcon(languageName, playerLanguageId == languageId), (baseSlots[slot] + (lines * 9)), "language_" + languageId + "_" + languageName);

                slot++;

                if (slot == 7)
                {
                    slot = 0;
                    lines++;
                }
            }

            this.setSlotData(getBackIcon(), this.inventory.getSize() - 4, "back");
        }

        @Override
        public void onClick(Player player, ItemStack stack, String action, ClickType clickType)
        {
            if(action.startsWith("language_"))
            {
                int languageId = Integer.parseInt(action.split("_")[1]);

                PlayerData playerData = this.plugin.getAPI().getPlayerManager().getPlayerData(player.getUniqueId());

                if (playerData.getLanguageId() == languageId)
                {
                    player.sendMessage(ChatColor.RED + "Cette langue est déjà sélectionnée !");
                    return;
                }

                playerData.getPlayerBean().setLanguageId(languageId);
                playerData.updateData();

                this.plugin.getAPI().getI18n().updatePlayer(player.getUniqueId(), languageId);

                JsonObject jedisPacket = new JsonObject();
                jedisPacket.addProperty("uuid", player.getUniqueId().toString());
                jedisPacket.addProperty("language", languageId);

                Jedis jedis = this.plugin.getDatabaseConnector().getBungeeResource();
                jedis.publish("languageupdate", new Gson().toJson(jedisPacket));

                player.sendMessage(ChatColor.GREEN + "Votre langue a été changé pour : " + ChatColor.GOLD + action.split("_")[2]);
            }
            else if(action.equals("back"))
            {
                this.plugin.getAPI().getGuiManager().closeGui(player);
            }
        }

        private static ItemStack makeLanguageIcon(String languageName, boolean selected)
        {
            ItemStack stack = new ItemStack(Material.WRITTEN_BOOK, 1);

            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + languageName);
            stack.setItemMeta(meta);

            if (selected)
                GlowEffect.addGlow(stack);

            return stack;
        }

        private static ItemStack getBackIcon()
        {
            ItemStack stack = new ItemStack(Material.EMERALD, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "« Retour");
            stack.setItemMeta(meta);

            return stack;
        }
    }
}
