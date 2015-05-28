/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.util.TurretRegistry.HealInfo;
import net.minecraft.item.ItemStack;

public interface TurretInfo<T extends Turret>
{
    Class<T> getTurretClass();

    String getName();

    String getIcon();

    TurretInfo<T> addAmmoType(TurretAmmo ammoType);

    TurretInfo<T> applyHealItems(HealInfo... healTypes);

    TurretAmmo getAmmo(ItemStack stack);

    HealInfo getHeal(ItemStack stack);

    ItemStack[] getDepletedAmmoStacks(int ammoCount);
}
