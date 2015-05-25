package de.sanandrew.mods.turretmod.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface TurretProjectile<T extends EntityArrow>
{
    T getEntity();

    void setTarget(EntityLivingBase shooter, Entity target, float par4, float par5);

    DamageSource getDamageSource(Entity entity);
}
