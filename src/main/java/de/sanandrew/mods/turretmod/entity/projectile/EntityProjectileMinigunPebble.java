/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurretMinigun;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityProjectileMinigunPebble
        extends EntityTurretProjectile
{
    public EntityProjectileMinigunPebble(World world) {
        super(world);
    }

    public EntityProjectileMinigunPebble(World world, Entity shooter, Entity target) {
        super(world, shooter, target);

        if( shooter instanceof EntityTurretMinigun ) {
            EntityTurretMinigun turret = (EntityTurretMinigun) shooter;

            float shift = (turret.leftShot ? 45.0F : -45.0F) / 180.0F * (float) Math.PI;
            float rotXZ = -turret.rotationYawHead / 180.0F * (float) Math.PI;
            float rotY = -(turret.rotationPitch - 7.5F) / 180.0F * (float) Math.PI - 0.1F;
            boolean isUpsideDown = turret.isUpsideDown;

            this.posX += (Math.sin(rotXZ + shift) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);
            this.posY += (Math.sin(rotY) * 0.6F) * (isUpsideDown ? -1.0F : 1.0F) - (isUpsideDown ? 1.0F : 0.0F);
            this.posZ += (Math.cos(rotXZ + shift) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);

            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    public EntityProjectileMinigunPebble(World world, Entity shooter, Vec3d shootingVec) {
        super(world, shooter, shootingVec);
    }

    @Override
    public float getArc() {
        return 0.001F;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return Sounds.ricochet_bullet;
    }

    @Override
    public float getInitialSpeedMultiplier() {
        return 3.0F;
    }

    @Override
    public float getDamage() {
        return 0.3F;
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
        return 0.01D;
    }

    @Override
    public void onPostHit(Entity e, DamageSource dmgSource) {
        super.onPostHit(e, dmgSource);

        if( e instanceof EntityLivingBase ) {
            ((EntityLivingBase) e).hurtResistantTime = 0;
        }
    }
}
