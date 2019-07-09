/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistryObject;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p>A registry object defining an ammunition item.</p>
 */
public interface IAmmunition
        extends IRegistryObject
{
    /**
     * <p>Returns the ammunition group.</p>
     * <p><i>Example:</i> Each Cryo-Cell ammunition object returns the same group instance (with the ID <tt>sapturretmod:cryocell</tt>).
     *
     * @return The group representing this ammunition object.
     */
    @Nonnull
    IAmmunitionGroup getGroup();

    /**
     * <p>Returns the damage dealt, represented as a range, by a projectile spawned by this ammunition object. This is primarily used by the Turret Lexicon.</p>
     * <p>If the projectile deals the same amount of damage, this returns {@link Range#is(Comparable) Range.is(damage)}.</p>
     * <p>If the projectile deals a variable amount of damage, this returns {@link Range#between(Comparable, Comparable) Range.between(minDamage, maxDamage)}.</p>
     *
     * @return The amount of damage, as a range, a projectile can deal.
     */
    @Nonnull
    Range<Float> getDamageInfo();

    /**
     * @return The amount of rounds provided by one item.
     */
    int getAmmoCapacity();

    /**
     * <p>Returns the projectile delegate to be shot.</p>
     * <p>If this returns null, no projectile is fired. Use the {@link de.sanandrew.mods.turretmod.api.event.TargetingEvent.ProcessorTick TargetingEvent.ProcessorTick} event
     *    in order for custom turret effects (like shooting multiple projectiles at once).</p>
     *
     * @param turretInst The turret firing the projectile.
     * @return The projectile delegate or <tt>null</tt>, if no projectile should be fired.
     */
    @Nullable
    IProjectile getProjectile(ITurretInst turretInst);
}
