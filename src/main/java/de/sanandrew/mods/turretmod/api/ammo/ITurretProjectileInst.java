package de.sanandrew.mods.turretmod.api.ammo;

import net.minecraft.entity.Entity;

public interface ITurretProjectileInst
{
    /**
     * @return the last damage value caused to a target; {@link Float#MAX_VALUE}, if the projectile instance is new
     */
    float getLastCausedDamage();

    /**
     * @return this instance as an {@link Entity}
     */
    Entity get();
}
