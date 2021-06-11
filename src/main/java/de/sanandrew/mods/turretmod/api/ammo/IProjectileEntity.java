package de.sanandrew.mods.turretmod.api.ammo;

import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

/**
 * <p>An instance of a projectile entity.</p>
 */
public interface IProjectileEntity
{
    /**
     * @return the last damage value caused to a target or {@link Float#MAX_VALUE}, if the projectile instance is new.
     */
    float getLastCausedDamage();

    /**
     * <p>Returns the last entities to have been damaged by this projectile.</p>
     *
     * @return the last damaged entities. This is an empty array if no entities have been damaged yet.
     */
    @Nonnull
    Entity[] getLastDamagedEntities();

    /**
     * @return this projectile instance as an {@link Entity}
     */
    Entity get();

    IAmmunition getAmmunition();

    String getAmmunitionSubtype();

    IProjectile getDelegate();
}
