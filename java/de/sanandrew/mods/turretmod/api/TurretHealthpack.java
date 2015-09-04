package de.sanandrew.mods.turretmod.api;

import net.minecraft.item.ItemStack;

public interface TurretHealthpack
{
    /**
     * Returns the healing item name. Can be non-unique, but it's preferred.
     * @return the name of the healing item
     */
    String getName();

    /**
     * Returns the amount of health a single item can restore. A value of 2 means, with a stack of 10 items, it'll heal the turret with 20 HP (10 hearts).
     * @return the amount of HP an item yields
     */
    float getAmount();

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
