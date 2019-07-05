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

/**
 * <p>A registry object defining a turret repair kit.</p>
 */
public interface IRepairKit
        extends IRegistryObject
{
    /**
     * <p>Returns the amount of health points (1HP = ½ hearts) this repair kit restores.</p>
     * <p>This will not be less than or equal to 0, as that should ignore a subsequent call to {@link IRepairKit#onHeal(ITurretInst)} and any other processing.</p>
     * <p>Use {@link IRepairKit#isApplicable(ITurretInst)} before calling this to test if this repair kit is viable.</p>
     *
     * @return The amount of health points.
     */
    float getHealAmount();

    /**
     * <p>Indicates wether this repair kit is viable to be applied to the given turret instance.</p>
     *
     * @param turret The turret instance that should be checked.
     * @return <tt>true</tt>, if this repair kit can be applied to the given turret instance; <tt>false</tt> otherwise.
     */
    boolean isApplicable(ITurretInst turret);

    default void onHeal(ITurretInst turret) { }
}
