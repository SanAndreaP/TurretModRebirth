package de.sanandrew.mods.turretmod.api;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds info about turret ammunition. Users of this interface can add custom ammo for turrets.<br>
 * To add new custom ammo, you need to implement this in a class and use the {@link TurretHealItem#HEAL_TYPES} list to add a new instance
 * of your custom ammo implementation.
 */
public interface TurretHealItem
{
    /**
     * The list of available healing items.<br>
     * If you want to add your own healing item, insert a new {@link TurretHealItem} instance here via {@code TurretHealItem.HEAL_TYPES.add(new TurretHealItem())}
     */
    List<TurretHealItem> HEAL_TYPES = new ArrayList<>();

    /**
     * Returns the healing item name. Can be non-unique, but it's preferred.
     * @return the name of the healing item
     */
    String getName();

    /**
     * Returns the amount of health a single item can restore. A value of 2 means, with a stack of 10 items, it'll heal the turret with 20 HP (10 hearts).
     * @return the amount of HP an item yields
     */
    int getAmount();

    /**
     * Returns the item used by the registry to determine the healing item implementation. This must be unique for every implementation!
     * @return the healing item
     */
    ItemStack getHealItem();

    /**
     * Checks whether the turret accepts this healing item.
     * @param turret The turret
     * @return true, if it's applicable, false otherwise
     */
    boolean isApplicablToTurret(Turret turret);
}
