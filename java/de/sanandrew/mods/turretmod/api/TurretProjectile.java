package de.sanandrew.mods.turretmod.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;

public interface TurretProjectile<T extends EntityArrow>
{
    T getEntity();

    void setTarget(EntityLivingBase shooter, Entity target, float par4, float par5);
}
