/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistryType;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p>An object defining an ammunition item.</p>
 * <p>This interface is used as a delegate for the actual item. An instance of this delegate is to be registered with the
 * {@link IAmmunitionRegistry} through {@link de.sanandrew.mods.turretmod.api.ITmrPlugin#registerAmmo(IAmmunitionRegistry)}</p>
 */
public interface IAmmunition
        extends IRegistryType
{
    /**
     * <p>Returns the ammunition group. This is used in the Turret Info Tablet to group ammo types together in one page.</p>
     * <p><i>Example:</i> Each Cryo-Cell type returns the same group instance (with the ID {@code sapturretmod:cryocell})
     * <p>Cannot be <tt>null</tt></p>
     *
     * @return A group instance
     */
    @Nonnull
    IAmmunitionGroup getGroup();

    /**
     * <p>Returns the damage dealt, represented as a range, by a projectile spawned by this ammo type for the Turret Info Tablet.</p>
     * <p>If the projectile deals the same amount of damage, return {@link Range#is(Comparable) Range#is(damage)}.</p>
     * <p>If the projectile deals a variable amount of damage, return {@link Range#between(Comparable, Comparable) Range#between(minDamage, maxDamage)}.</p>
     * <p>Cannot be <tt>null</tt>!</p>
     *
     * @return The amount of damage, as range, a projectile can deal
     */
    @Nonnull
    Range<Float> getDamageInfo();

    /**
     * @return The amount of rounds provided by one item
     */
    int getAmmoCapacity();

    /**
     * <p>Returns the projectile delegate to be shot.</p>
     * <p>If this returns null, no projectile is fired. Use the {@link de.sanandrew.mods.turretmod.api.event.TargetingEvent.ProcessorTick} event
     *    in order for custom turret effects.</p>
     *
     * @param turretInst The turret firing the projectile
     * @return The projectile delegate or <tt>null</tt>, if no projectile should be fired
     */
    @Nullable
    ITurretProjectile getProjectile(ITurretInst turretInst);
}
