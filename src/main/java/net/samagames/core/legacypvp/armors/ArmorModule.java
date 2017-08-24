package net.samagames.core.legacypvp.armors;

import net.samagames.core.ApiImplementation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
public class ArmorModule implements Listener{


    private ApiImplementation api;

    public ArmorModule(ApiImplementation api)
    {

        this.api = api;
    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    public void onEntityDamage(EntityDamageEvent event) {
        if (api.getGameManager().isLegacyPvP())
        {
            Entity entity = event.getEntity();
            if(entity instanceof Player) {
                Player player = (Player)entity;
                double baseDamage = event.getDamage(EntityDamageEvent.DamageModifier.BASE);
                int defensePoints = this.getDefensePoints(player.getInventory());
                double armorDamage = baseDamage * this.getLegacyDamageFactor(defensePoints);
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, armorDamage - baseDamage);
            }
        }
    }

    private double getDamageCorrectionFactor(PlayerInventory inventory, double damage) {
        int defensePoints = this.getDefensePoints(inventory);
        double legacy = this.getLegacyDamageFactor(defensePoints);
        double current = this.getCurrentDamageFactor(defensePoints, damage);
        return legacy / current;
    }

    private int getDefensePoints(PlayerInventory inventory) {
        byte points = 0;
        int points1 = points + this.getDefensePoints(inventory.getHelmet());
        points1 += this.getDefensePoints(inventory.getChestplate());
        points1 += this.getDefensePoints(inventory.getLeggings());
        points1 += this.getDefensePoints(inventory.getBoots());
        return points1;
    }

    private int getDefensePoints(ItemStack armorPiece) {
        return armorPiece == null?0:Armor.getForType(armorPiece.getType()).getDefensePoints();
    }

    private double getLegacyDamageFactor(int defensePoints) {
        int percent = 4 * defensePoints;
        return 1.0D - (double)percent / 100.0D;
    }

    private double getCurrentDamageFactor(int defensePoints, double damage) {
        return 1.0D - Math.max((double)defensePoints / 5.0D, (double)defensePoints - damage / 2.0D) / 25.0D;
    }

}
