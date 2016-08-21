package net.samagames.core.utils;

import net.samagames.api.SamaGamesAPI;
import net.samagames.tools.GlowEffect;
import net.samagames.tools.MojangShitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PersistanceUtils
{
    private PersistanceUtils()
    {
    }

    /**
     * Get an item from a string
     *
     * @param item Item
     * @return Bukkit item
     */
    public static ItemStack strToItem(String item, String name)
    {
        String[] itemData = item.split(":");
        ItemStack stack;

        if (itemData[0].equalsIgnoreCase("B"))
        {
            Material material = Material.valueOf(itemData[1].toUpperCase());
            int size = Integer.parseInt(itemData[2]);
            byte durability = Byte.parseByte(itemData[3]);

            stack = new ItemStack(material, size, durability);

            if (itemData.length == 5 && itemData[4].equalsIgnoreCase("GLOW"))
                GlowEffect.addGlow(stack);
        }
        else if (itemData[0].equalsIgnoreCase("P"))
        {
            String nmsPotionName = itemData[1].toLowerCase();
            boolean isSplash = Boolean.parseBoolean(itemData[2]);
            boolean isLingering = Boolean.parseBoolean(itemData[3]);

            stack = MojangShitUtils.getPotion(nmsPotionName, isSplash, isLingering);

            if (itemData.length == 5 && itemData[4].equalsIgnoreCase("GLOW"))
                GlowEffect.addGlow(stack);
        }
        else if (itemData[0].equalsIgnoreCase("E"))
        {
            EntityType entityType = EntityType.valueOf(itemData[1].toUpperCase());

            stack = MojangShitUtils.getMonsterEgg(entityType);

            if (itemData.length == 3 && itemData[2].equalsIgnoreCase("GLOW"))
                GlowEffect.addGlow(stack);
        }
        else
        {
            SamaGamesAPI.get().getPlugin().getLogger().warning("[PersistanceUtils] Failed to recover the correct item type! (" + String.join(", ", itemData) + ")");

            stack = new ItemStack(Material.DEAD_BUSH, 1);
        }

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.translateAlternateColorCodes('&', name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);

        return stack;
    }
}
