package de.sanandrew.mods.turretmod.api.ammo;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * <p>Registry for turret projectile delegates.</p>
 */
@SuppressWarnings("unused")
public interface IProjectileRegistry
{
    /**
     * <p>Returns the projectile delegate registered with the given ID, <tt>null</tt> if there is no delegate with that ID registered.</p>
     *
     * @param id The ID the delegate is registered with
     *
     * @return the projectile delegate or <tt>null</tt>
     */
    ITurretProjectile getProjectile(UUID id);

    /**
     * <p>Registers a new projectile delegate.</p>
     *
     * @param projectile The new projectile delegate to be registered
     *
     * @throws IllegalArgumentException when the delegate ID is already registered
     */
    void registerProjectile(@Nonnull ITurretProjectile projectile) throws IllegalArgumentException;

    /**
     * <p>Removes the delegate with the given ID from the registry.</p>
     *
     * @param id The ID to be removed
     */
    void removeProjectile(UUID id);
}
