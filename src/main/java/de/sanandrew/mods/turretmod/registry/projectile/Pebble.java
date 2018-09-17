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
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Pebble
        implements ITurretProjectile
{
    private static final UUID ID = UUID.fromString("87E13300-2A58-456C-9712-B440C48D5376");

    @Nonnull
    @Override
    public UUID getId() {
        return ID;
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
        return 0.5F;
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
        return 0.8D;
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
