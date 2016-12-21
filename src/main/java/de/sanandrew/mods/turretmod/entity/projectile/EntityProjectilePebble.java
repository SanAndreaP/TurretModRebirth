/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityProjectilePebble
        extends EntityTurretProjectile
{
    public EntityProjectilePebble(World world) {
        super(world);
    }

    public EntityProjectilePebble(World world, Entity shooter, Entity target) {
        super(world, shooter, target);
    }

    public EntityProjectilePebble(World world, Entity shooter, Vec3d shootingVec) {
        super(world, shooter, shootingVec);
    }

    @Override
    public float getArc() {
        return 0.001F;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return Sounds.RICOCHET_BULLET;
    }

    @Override
    public float getInitialSpeedMultiplier() {
        return 3.0F;
    }

    @Override
    public float getDamage() {
        return 0.5F;
    }

    @Override
    public float getKnockbackStrengthH() {
        return 0.0F;
    }

    @Override
    public float getKnockbackStrengthV() {
        return 0.0F;
    }

    @Override
    public double getScatterValue() {
        return 0.8D;
    }

    @Override
    public void onPostHit(Entity e, DamageSource dmgSource) {
        super.onPostHit(e, dmgSource);

        if( e instanceof EntityLivingBase ) {
            ((EntityLivingBase) e).hurtResistantTime = 0;
        }
    }
}
