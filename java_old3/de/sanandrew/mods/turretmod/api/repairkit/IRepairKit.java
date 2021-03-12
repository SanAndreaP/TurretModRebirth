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
 *
 * @see IRepairKitRegistry
 */
public interface IRepairKit
        extends IRegistryObject
{
    /**
     * <p>Returns the amount of health points (1HP = ½ hearts) this repair kit restores.</p>
     * <p>If this returns a value less than or equal to 0, subsequent calls to {@link IRepairKit#onHeal(ITurretInst)} and any other processing should be ignored.</p>
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

    /**
     * <p>Invoked when the given turret has been successfully healed by this repair kit.</p>
     * <p>Some repair kits execute additional code that apply special effects like regeneration to the turret.</p>
     *
     * @param turret The turret instance that got healed.
     */
    default void onHeal(ITurretInst turret) { }
}
