/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurretFlamethrower;
import de.sanandrew.mods.turretmod.util.Sounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityProjectileFlame
        extends EntityTurretProjectile
{
    public float damage;
    public boolean purifying;

    @SuppressWarnings("unused")
    public EntityProjectileFlame(World world) {
        super(world);
        this.damage = 2.75F;
    }

    public EntityProjectileFlame(World world, Entity shooter, Entity target) {
        super(world, shooter, target);

        if( shooter instanceof EntityTurretFlamethrower ) {
            EntityTurretFlamethrower turret = (EntityTurretFlamethrower) shooter;
            float rotXZ = -turret.rotationYawHead / 180.0F * (float) Math.PI;
            float rotY = -(turret.rotationPitch - 2.5F) / 180.0F * (float) Math.PI - 0.1F;
            boolean isUpsideDown = turret.isUpsideDown;

            this.posX += (Math.sin(rotXZ) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);
            this.posY += (Math.sin(rotY) * 0.6F) * (isUpsideDown ? -1.0F : 1.0F) - (isUpsideDown ? 1.0F : 0.0F);
            this.posZ += (Math.cos(rotXZ) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);

            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    @SuppressWarnings("unused")
    public EntityProjectileFlame(World world, Entity shooter, Vec3d shootingVec) {
        super(world, shooter, shootingVec);
    }

    @Override
    public float getArc() {
        return -0.25F;
    }

    @Override
    public float getInitialSpeedMultiplier() {
        return 0.6F;
    }

    @Override
    public float getDamage() {
        return this.damage;
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
        return 0.1D;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    @Override
    protected void processHit(@SuppressWarnings("UnusedParameters") RayTraceResult hitObj) {
        this.setPosition(hitObj.hitVec.xCoord, hitObj.hitVec.yCoord, hitObj.hitVec.zCoord);
        this.playSound(this.getRicochetSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
        if( hitObj.typeOfHit != RayTraceResult.Type.ENTITY ) {
            this.setDead();
        }
    }

    @Override
    public void onPostHit(Entity e, DamageSource dmgSource) {
        super.onPostHit(e, dmgSource);

//        this.damage -= 0.5F;
//        if( this.damage < 0.0F ) {
//            this.setDead();
//        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);

        buffer.writeBoolean(this.purifying);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);

        this.purifying = buffer.readBoolean();
    }
}
