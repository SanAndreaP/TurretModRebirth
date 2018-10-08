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
import java.util.UUID;

/**
 * <p>An object defining an ammunition item.</p>
 * <p>This interface is used as a delegate for the actual item. An instance of this delegate is to be registered with the
 * {@link IAmmunitionRegistry} through {@link de.sanandrew.mods.turretmod.api.ITmrPlugin#registerAmmo(IAmmunitionRegistry)}</p>
 */
public interface IAmmunition
{
    /**
     * <p>Returns the unlocalized name for this ammo type item. The name can (and should) differ, even if they're the same type.</p>
     * <p><i>Example:</i> Arrows and Quivers (each proviing the same ammo type) have the name <tt>arrow_sng</tt> and
     *    <tt>arrow_lrg</tt> respectively.</p>
     *
     * @return A name for this item
     */
    String getName();

    /**
     * <p>Returns the ID for this ammo type item. It needs to be unique from all other items registered.</p>
     * <p><i>Example:</i> Arrows and Quivers IDs begin with <tt>7B49...</tt> and <tt>E6D5...</tt> respectively.</p>
     * <p>Cannot be <tt>null</tt>!</p>
     *
     * @return A unique ID for this item
     */
    @Nonnull
    UUID getId();

    /**
     * <p>Returns the ID for this ammo type item. It needs to be unique from all other types registered, but must
     *    be the same for different items with the same type.</p>
     * <p><i>Example:</i> Arrows and Quivers have the same ID beginning with <tt>7B49...</tt>, Flux Cells and Flux Cell
     *    Packs have an ID beginning with <tt>4880...</tt>, Cryo-Cells have a different ID depending on their tier.</p>
     * <p>Cannot be <tt>null</tt>!</p>
     *
     * @return A unique ID for this type
     */
    @Nonnull
    UUID getTypeId();

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
     * <p>Returns the location of the item model to be used for this ammo.</p>
     *
     * @return The location of the item model
     */
    ResourceLocation getModel();
}
