/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectileInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Flame
        implements ITurretProjectile
{
    static final UUID ID1 = UUID.fromString("3C7C8732-7B9C-488D-9F30-C7A0723F1C54");
    static final UUID ID2 = UUID.fromString("E105B888-11D4-4491-A83E-885438DCD62A");

    private static final float DMG_REDUCTION = 0.5F;

    private final boolean purifying;
    private final UUID id;

    Flame(UUID id, boolean purifying) {
        this.id = id;
        this.purifying = purifying;
    }

    @Nonnull
    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public void onCreate(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile) {
        if( turret != null ) {
            Entity projEntity = projectile.get();

            float rotXZ = -turret.get().rotationYawHead / 180.0F * (float) Math.PI;
            float rotY = -(turret.get().rotationPitch - 2.5F) / 180.0F * (float) Math.PI - 0.1F;
            boolean isUpsideDown = turret.isUpsideDown();

            projEntity.posX += (Math.sin(rotXZ) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);
            projEntity.posY += (Math.sin(rotY) * 0.6F) * (isUpsideDown ? -1.0F : 1.0F) - (isUpsideDown ? 1.0F : 0.0F);
            projEntity.posZ += (Math.cos(rotXZ) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);

            projEntity.setPosition(projEntity.posX, projEntity.posY, projEntity.posZ);
        }
    }

    @Override
    public float getArc() {
        return -0.15F;
    }

    @Override
    public float getSpeed() {
        return 0.6F;
    }

    @Override
    public float getDamage() {
        return 3.0F;
    }

    @Override
    public float getKnockbackHorizontal() {
        return 0.0F;
    }

    @Override
    public float getKnockbackVertical() {
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
    public boolean onHit(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, RayTraceResult hitObj) {
        if( hitObj.typeOfHit != RayTraceResult.Type.ENTITY ) {
            Entity projEntity = projectile.get();
            if( this.purifying && hitObj.typeOfHit == RayTraceResult.Type.BLOCK && !projEntity.world.isRemote && MiscUtils.RNG.randomInt(100) == 0 ) {
                BlockPos fire = hitObj.getBlockPos().offset(hitObj.sideHit);
                if( projEntity.world.isAirBlock(fire) ) {
                    projEntity.world.setBlockState(fire, Blocks.FIRE.getDefaultState(), 11); // 1 = block update, 2 = send to client, 8 = needs update
                }
            }

            return true;
        } else {
            hitObj.entityHit.setFire(5);
            return MiscUtils.between(0.0F, projectile.getLastCausedDamage() - DMG_REDUCTION, -1.0F);
        }
    }

    @Override
    public boolean onDamageEntityPre(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc, MutableFloat damage) {
        if( projectile.getLastDamagedEntity() == target ) {
            return false;
        }

        float lastDmg = projectile.getLastCausedDamage();
        if( lastDmg > 0.0F ) {
            damage.setValue(lastDmg - 0.5F);
            return true;
        } else if( lastDmg - 0.5F > -1.0F ) {
            projectile.get().setDead();
            return false;
        } else {
            return true;
        }
    }
}
