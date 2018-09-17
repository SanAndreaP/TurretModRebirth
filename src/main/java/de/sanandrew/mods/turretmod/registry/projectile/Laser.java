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
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Laser
        implements ITurretProjectile
{
    private static final UUID ID = UUID.fromString("CFED3E55-284B-4697-9FB0-682BFB736101");

    @Nonnull
    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public float getArc() {
        return 0.0F;
    }

    @Override
    public float getSpeed() {
        return 20.0F;
    }

    @Override
    public float getDamage() {
        return 1.5F;
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
    public SoundEvent getRicochetSound() {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    @Override
    public double getScatterValue() {
        return 0;
    }

    @Override
    public DamageSource getCustomDamageSrc(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, boolean isIndirect) {
        DamageSource dmg;

        if( isIndirect ) {
            dmg = DamageSource.causeThrownDamage(projectile.get(), turret == null ? projectile.get() : turret.get());
        } else {
            dmg = DamageSource.causeThornsDamage(turret == null ? projectile.get() : turret.get());
        }

        if( turret == null || !turret.getUpgradeProcessor().hasUpgrade(Upgrades.ENDER_MEDIUM) ) {
            dmg.setFireDamage();
        }

        return dmg;
    }

    @Override
    public boolean onDamageEntityPre(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc, MutableFloat damage) {
        if( target instanceof EntityLivingBase ) {
            EntityLivingBase elb = ((EntityLivingBase) target);

            if( turret == null || !turret.getUpgradeProcessor().hasUpgrade(Upgrades.ENDER_MEDIUM) ) {
                return !elb.isImmuneToFire();
            } else if( !elb.isImmuneToFire() ) {
                damage.setValue(damage.floatValue() * 1.25F);
            }

            elb.hurtResistantTime = 0;
        }

        return true;
    }

    @Override
    public void onDamageEntityPost(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc) {
        if( target instanceof EntityLivingBase ) {
            ((EntityLivingBase) target).hurtResistantTime = ((EntityLivingBase) target).maxHurtResistantTime;
        }

        target.setFire(2);
    }
}
