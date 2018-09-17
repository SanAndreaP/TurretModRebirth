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
import io.netty.buffer.ByteBuf;
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

public class EntityProjectileFlame
        implements ITurretProjectile
{
    static final UUID ID1 = UUID.fromString("3A7F610D-0DE4-42F3-B3E2-9FFF5FC0693A");
    static final UUID ID2 = UUID.fromString("5CE32B23-3038-454E-8109-CA8CA2CB75F3");
    static final UUID ID3 = UUID.fromString("419B8825-24CF-4B20-B785-E78D95575A33");

    private final boolean purifying;
    private final UUID id;

    EntityProjectileFlame(UUID id, boolean purifying) {
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
        return -0.25F;
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
        if( hitObj.entityHit == null ) {
            Entity projEntity = projectile.get();
            projEntity.setDead();
            if( this.purifying && hitObj.typeOfHit == RayTraceResult.Type.BLOCK && !projEntity.world.isRemote && MiscUtils.RNG.randomInt(100) == 0 ) {
                BlockPos fire = hitObj.getBlockPos().offset(hitObj.sideHit);
                if( projEntity.world.isAirBlock(fire) ) {
                    projEntity.world.setBlockState(fire, Blocks.FIRE.getDefaultState(), 11); // 1 = block update, 2 = send to client, 8 = needs update
                }
            }
        } else {
            hitObj.entityHit.setFire(5);
        }

        return true;
    }

    @Override
    public boolean onDamageEntityPre(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc, MutableFloat damage) {


        super.onPostHit(e, dmgSource);

        this.damage -= 0.5F;
        if( this.damage < 0.0F ) {
            this.setDead();
        }
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
