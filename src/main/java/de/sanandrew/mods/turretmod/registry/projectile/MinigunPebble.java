/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectileInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.turret.TurretMinigun;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class MinigunPebble
        implements ITurretProjectile
{
    private static final UUID ID = UUID.fromString("CFED3E55-284B-4697-9FB0-682BFB736101");

    @Nonnull
    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public void onCreate(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile) {
        if( turret != null ) {
            TurretMinigun.MyRAM ram = turret.getRAM(TurretMinigun.MyRAM::new);

            float shift = (ram.isLeftShot ? 45.0F : -45.0F) / 180.0F * (float) Math.PI;
            float rotXZ = -turret.get().rotationYawHead / 180.0F * (float) Math.PI;
            float rotY = -(turret.get().rotationPitch - 7.5F) / 180.0F * (float) Math.PI - 0.1F;
            boolean isUpsideDown = turret.isUpsideDown();

            Entity projEntity = projectile.get();
            projEntity.posX += (Math.sin(rotXZ + shift) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);
            projEntity.posY += (Math.sin(rotY) * 0.6F) * (isUpsideDown ? -1.0F : 1.0F) - (isUpsideDown ? 1.0F : 0.0F);
            projEntity.posZ += (Math.cos(rotXZ + shift) * 0.7F * Math.cos(rotY)) * (isUpsideDown ? -1.0F : 1.0F);

            projEntity.setPosition(projEntity.posX, projEntity.posY, projEntity.posZ);
        }
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
    public float getSpeed() {
        return 3.0F;
    }

    @Override
    public float getDamage() {
        return 0.3F;
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
        return 0.01D;
    }

    @Override
    public boolean onDamageEntityPre(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc, MutableFloat damage) {
        if( target instanceof EntityLivingBase ) {
            ((EntityLivingBase) target).hurtResistantTime = 0;
        }

        return true;
    }

    @Override
    public void onDamageEntityPost(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc) {
        if( target instanceof EntityLivingBase ) {
            ((EntityLivingBase) target).hurtResistantTime = ((EntityLivingBase) target).maxHurtResistantTime;
        }
    }
}
