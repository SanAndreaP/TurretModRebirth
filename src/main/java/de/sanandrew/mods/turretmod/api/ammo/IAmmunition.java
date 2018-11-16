/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p>An object defining an ammunition item.</p>
 * <p>This interface is used as a delegate for the actual item. An instance of this delegate is to be registered with the
 * {@link IAmmunitionRegistry} through {@link de.sanandrew.mods.turretmod.api.ITmrPlugin#registerAmmo(IAmmunitionRegistry)}</p>
 */
public interface IAmmunition
{
    /**
     * <p>Returns the ID for this ammo type item. It needs to be unique from all other items registered.</p>
     * <p>Cannot be <tt>null</tt>!</p>
     *
     * @return A unique ID for this item
     */
    @Nonnull
    ResourceLocation getId();

    /**
     * <p>Returns the ammo group. This is used in the Turret Info Tablet to group ammunition types together in one page.</p>
     * <p><i>Example:</i> Arrows and Quivers have the same group with an ID beginning with {@code 7B49...}, All Cryo-Cells and their
     *    Packs have a group with an ID beginning with {@code 0B56...}.</p>
     * <p>Cannot be {@code null}!</p>
     *
     * @return A group instance
     */
    @Nonnull
    IAmmunitionGroup getGroup();

    /**
     * <p>Returns the damage dealt by a projectile spawned by this ammo type for the Turret Info Tablet.</p>
     * <p>If the projectile can deal higher amounts of damage under specific circumstances, return the lowest value here.</p>
     *
     * @return The (lowest) amount of damage a projectile can deal
     */
    float getDamageInfo();

    /**
     * <p>Returns how many rounds of ammo this item can hold.</p>
     *
     * @return The amount of rounds provided by one item
     */
    int getAmmoCapacity();

    /**
     * <p>Returns the projectile delegate to be shot.</p>
     * <p>If this returns null, no projectile is fired. Use the {@link de.sanandrew.mods.turretmod.api.event.TargetingEvent.ProcessorTick} event
     *    in order for custom turret effects.</p>
     *
     * @param turretInst The turret firing the projectile
     * @return The projectile delegate or <tt>null</tt>, if no projectile should be fired.
     */
    @Nullable
    ITurretProjectile getProjectile(ITurretInst turretInst);

    /**
     * Returns wether or not this ammo type is considered valid.
     * @return <tt>true</tt>, if this ammo is valid and usable, <tt>false</tt> otherwise.
     */
    default boolean isValid() {
        return true;
    }
}
