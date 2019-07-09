package de.sanandrew.mods.turretmod.api.ammo;

import net.minecraft.entity.Entity;

/**
 * <p>An instance of a projectile entity.</p>
 */
public interface IProjectileInst
{
    /**
     * @return the last damage value caused to a target or {@link Float#MAX_VALUE}, if the projectile instance is new.
     */
    float getLastCausedDamage();

    /**
     * <p>Returns the last entity to have been damaged by this projectile.</p>
     *
     * @return the last damaged entity or <tt>null</tt>, if the projectile instance is new.
     */
    Entity getLastDamagedEntity();

    /**
     * @return this projectile instance as an {@link Entity}
     */
    Entity get();
}
