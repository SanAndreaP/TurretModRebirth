/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api.repairkit;

import de.sanandrew.mods.turretmod.api.IRegistryObject;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;

public interface IRepairKit
        extends IRegistryObject
{
    float getHealAmount();

    boolean isApplicable(ITurretInst turret);

    default void onHeal(ITurretInst turret) { }
}
