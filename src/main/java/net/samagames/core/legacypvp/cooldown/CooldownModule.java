package net.samagames.core.legacypvp.cooldown;

import net.samagames.core.ApiImplementation;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

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
public class CooldownModule implements Listener
{
    private ApiImplementation api;

    private int[] data = new int[Material.values().length];

    public CooldownModule(ApiImplementation api)
    {
        this.api = api;

        data[Material.WOOD_AXE.ordinal()] = 1;
        data[Material.GOLD_AXE.ordinal()] = 2;
        data[Material.WOOD_SWORD.ordinal()] = 3;
        data[Material.GOLD_SWORD.ordinal()] = 4;
        data[Material.STONE_AXE.ordinal()] = 5;
        data[Material.STONE_SWORD.ordinal()] = 6;
        data[Material.IRON_AXE.ordinal()] = 7;
        data[Material.IRON_SWORD.ordinal()] = 8;
        data[Material.DIAMOND_AXE.ordinal()] = 9;
        data[Material.DIAMOND_SWORD.ordinal()] = 10;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if (api.getGameManager().isLegacyPvP())
        {
            AttributeInstance genericAttackSpeedAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);

            if (genericAttackSpeedAttribute != null)
                genericAttackSpeedAttribute.setBaseValue(1024.0D);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (api.getGameManager().isLegacyPvP()) {
            Entity attacker = event.getDamager();
            if (attacker instanceof Player) {
                Player player = (Player) attacker;
                ItemStack inHand = player.getInventory().getItemInMainHand();
                if (inHand != null) {
                    double baseDamage = event.getDamage(EntityDamageEvent.DamageModifier.BASE);
                    double currentDamage = this.getCurrentDamage(inHand.getType());
                    if (currentDamage != 0.0D) {
                        double damageFactor = baseDamage / currentDamage;
                        double legacyDamage = this.getLegacyDamage(inHand.getType()) * damageFactor;
                        event.setDamage(EntityDamageEvent.DamageModifier.BASE, legacyDamage);
                    }
                }
            }
        }
    }

    private double getLegacyDamage(Material type) {
        switch(data[type.ordinal()]) {
            case 1:
            case 2:
                return 4.0D;
            case 3:
            case 4:
            case 5:
                return 5.0D;
            case 6:
            case 7:
                return 6.0D;
            case 8:
            case 9:
                return 7.0D;
            case 10:
                return 8.0D;
            default:
                return 0.0D;
        }
    }

    private double getCurrentDamage(Material type) {
        switch(data[type.ordinal()]) {
            case 1:
            case 2:
            case 10:
                return 7.0D;
            case 3:
            case 4:
                return 4.0D;
            case 5:
            case 7:
            case 9:
                return 9.0D;
            case 6:
                return 5.0D;
            case 8:
                return 6.0D;
            default:
                return 0.0D;
        }
    }
}
