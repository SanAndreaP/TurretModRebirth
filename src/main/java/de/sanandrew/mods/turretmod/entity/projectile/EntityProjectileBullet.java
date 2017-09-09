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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityProjectileBullet
        extends EntityTurretProjectile
{
    @SuppressWarnings("unused")
    public EntityProjectileBullet(World world) {
        super(world);
    }

    public EntityProjectileBullet(World world, Entity shooter, Entity target) {
        super(world, shooter, target);
    }

    @SuppressWarnings("unused")
    public EntityProjectileBullet(World world, Entity shooter, Vec3d shootingVec) {
        super(world, shooter, shootingVec);
    }

    @Override
    public float getArc() {
        return 0.05F;
    }

    @Override
    public float getInitialSpeedMultiplier() {
        return 3.0F;
    }

    @Override
    public float getDamage() {
        return 2.75F;
    }

    @Override
    public float getKnockbackStrengthH() {
        return 0.005F;
    }

    @Override
    public float getKnockbackStrengthV() {
        return 0.1F;
    }

//    @Override
//    public boolean onPreHit(Entity e, DamageSource dmgSource, float dmg) {
//        if( !this.world.isRemote && e instanceof EntityLivingBase ) {
//            ((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.duration, this.level));
//            if( e instanceof EntityCreature && this.shooterCache instanceof EntityTurret ) {
//                setEntityTarget((EntityCreature) e, (EntityTurret) this.shooterCache);
//            }
//            this.playSound(this.getRicochetSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
//            this.setDead();
//            return false;
//        }
//
//        return super.onPreHit(e, dmgSource, dmg);
//    }

    @Override
    public SoundEvent getRicochetSound() {
        return Sounds.RICOCHET_BULLET;
    }

//    @Override
//    public void onPostHit(Entity e, DamageSource dmg) {
//        super.onPostHit(e, dmg);
//
//        if( e instanceof EntityLivingBase ) {
//            ((EntityLivingBase) e).hurtResistantTime = 0;
//        }
//    }
}
