/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.turretmod.api.ammo.IProjectileRegistry;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ProjectileRegistry
        implements IProjectileRegistry
{
    public static final ProjectileRegistry INSTANCE = new ProjectileRegistry();

    private final Map<UUID, ITurretProjectile> projectiles;

    private ProjectileRegistry() {
        this.projectiles = new HashMap<>();
    }

    @Override
    public ITurretProjectile getProjectile(UUID id) {
        return this.projectiles.get(id);
    }

    @Override
    public void registerProjectile(@Nonnull ITurretProjectile projectile) throws IllegalArgumentException {
        UUID id = projectile.getId();
        if( this.projectiles.containsKey(id) ) {
            throw new IllegalArgumentException(String.format("Cannot register projectiles with the same ID %s!", id));
        }

        this.projectiles.put(id, projectile);
    }

    @Override
    public void removeProjectile(UUID id) {
        this.projectiles.remove(id);
    }
}
