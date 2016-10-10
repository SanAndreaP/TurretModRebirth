/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityProjectileLaser
        extends EntityTurretProjectile
{
    private int prevMaxHurtResistantTime;

    @SuppressWarnings("unused")
    public EntityProjectileLaser(World world) {
        super(world);
    }

    public EntityProjectileLaser(World world, Entity shooter, Entity target) {
        super(world, shooter, target);
    }

    @SuppressWarnings("unused")
    public EntityProjectileLaser(World world, Entity shooter, Vec3d shootingVec) {
        super(world, shooter, shootingVec);
    }

    @Override
    public float getArc() {
        return 0.0F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        //        if( this.worldObj.isRemote ) {
        //            TurretModRebirth.proxy.spawnParticle(EnumParticle.CRYO_PARTICLE, this.posX, this.posY, this.posZ, Triplet.with(this.motionX, this.motionY, this.motionZ));
        //        }
    }

    @Override
    public float getInitialSpeedMultiplier() {
        return 20.0F;
    }

    @Override
    public float getDamage() {
        return -1.5F;
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
    public SoundEvent getRicochetSound() {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    @Override
    public boolean onPreHit(Entity e, DamageSource dmgSource, float dmg) {
        if( super.onPreHit(e, dmgSource, dmg) ) {
            if( e instanceof EntityLivingBase ) {
                EntityLivingBase elb = ((EntityLivingBase) e);
                this.prevMaxHurtResistantTime = elb.maxHurtResistantTime;
                elb.maxHurtResistantTime = 10;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPostHit(Entity e, DamageSource dmg) {
        super.onPostHit(e, dmg);

        if( e instanceof EntityLivingBase ) {
            ((EntityLivingBase) e).maxHurtResistantTime = this.prevMaxHurtResistantTime;
//            e.setFire(2);
        }
    }
}
