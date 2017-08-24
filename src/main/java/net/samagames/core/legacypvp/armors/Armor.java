package net.samagames.core.legacypvp.armors;

import com.google.common.collect.Maps;
import org.bukkit.Material;

import java.util.Map;

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
public class Armor {
    private static final Map<Material, Armor> armorMap = Maps.newConcurrentMap();
    public static Armor LEATHER_HELMET;
    public static Armor LEATHER_CHESTPLATE;
    public static Armor LEATHER_LEGGINGS;
    public static Armor LEATHER_BOOTS;
    public static Armor GOLD_HELMET;
    public static Armor GOLD_CHESTPLATE;
    public static Armor GOLD_LEGGINGS;
    public static Armor GOLD_BOOTS;
    public static Armor CHAINMAIL_HELMET;
    public static Armor CHAINMAIL_CHESTPLATE;
    public static Armor CHAINMAIL_LEGGINGS;
    public static Armor CHAINMAIL_BOOTS;
    public static Armor IRON_HELMET;
    public static Armor IRON_CHESTPLATE;
    public static Armor IRON_LEGGINGS;
    public static Armor IRON_BOOTS;
    public static Armor DIAMOND_HELMET;
    public static Armor DIAMOND_CHESTPLATE;
    public static Armor DIAMOND_LEGGINGS;
    public static Armor DIAMOND_BOOTS;
    public static Armor UNKNOWN;
    private final int defensePoints;

    private Armor(Material material, int armorPoints) {
        this.defensePoints = armorPoints;
        if(material != null) {
            armorMap.put(material, this);
        }
    }

    public int getDefensePoints() {
        return this.defensePoints;
    }

    public static Armor getForType(Material type) {
        return (Armor)armorMap.getOrDefault(type, UNKNOWN);
    }

    static {
        LEATHER_HELMET = new Armor(Material.LEATHER_HELMET, 1);
        LEATHER_CHESTPLATE = new Armor(Material.LEATHER_CHESTPLATE, 3);
        LEATHER_LEGGINGS = new Armor(Material.LEATHER_LEGGINGS, 2);
        LEATHER_BOOTS = new Armor(Material.LEATHER_BOOTS, 1);
        GOLD_HELMET = new Armor(Material.GOLD_HELMET, 2);
        GOLD_CHESTPLATE = new Armor(Material.GOLD_CHESTPLATE, 5);
        GOLD_LEGGINGS = new Armor(Material.GOLD_LEGGINGS, 3);
        GOLD_BOOTS = new Armor(Material.GOLD_BOOTS, 1);
        CHAINMAIL_HELMET = new Armor(Material.CHAINMAIL_HELMET, 2);
        CHAINMAIL_CHESTPLATE = new Armor(Material.CHAINMAIL_CHESTPLATE, 5);
        CHAINMAIL_LEGGINGS = new Armor(Material.CHAINMAIL_LEGGINGS, 4);
        CHAINMAIL_BOOTS = new Armor(Material.CHAINMAIL_BOOTS, 1);
        IRON_HELMET = new Armor(Material.IRON_HELMET, 2);
        IRON_CHESTPLATE = new Armor(Material.IRON_CHESTPLATE, 6);
        IRON_LEGGINGS = new Armor(Material.IRON_LEGGINGS, 5);
        IRON_BOOTS = new Armor(Material.IRON_BOOTS, 2);
        DIAMOND_HELMET = new Armor(Material.DIAMOND_HELMET, 3);
        DIAMOND_CHESTPLATE = new Armor(Material.DIAMOND_CHESTPLATE, 8);
        DIAMOND_LEGGINGS = new Armor(Material.DIAMOND_LEGGINGS, 6);
        DIAMOND_BOOTS = new Armor(Material.DIAMOND_BOOTS, 3);
        UNKNOWN = new Armor((Material)null, 0);
    }
}
