/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectileInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.turret.TurretCryolator;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class CryoBall
        implements ITurretProjectile
{
    static final UUID ID1 = UUID.fromString("3A7F610D-0DE4-42F3-B3E2-9FFF5FC0693A");
    static final UUID ID2 = UUID.fromString("5CE32B23-3038-454E-8109-CA8CA2CB75F3");
    static final UUID ID3 = UUID.fromString("419B8825-24CF-4B20-B785-E78D95575A33");

    private final int level;
    private final int duration;
    private final UUID id;

    CryoBall(UUID id, int level, int duration) {
        this.id = id;
        this.level = level;
        this.duration = duration;
    }

    @Nonnull
    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public float getArc() {
        return TurretCryolator.projArc;
    }

    @Override
    public void onUpdate(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile) {
        Entity projEntity = projectile.get();
        if( projEntity.world.isRemote ) {
            TurretModRebirth.proxy.spawnParticle(EnumParticle.CRYO_PARTICLE, projEntity.posX, projEntity.posY, projEntity.posZ,
                                                 new Tuple(projEntity.motionX, projEntity.motionY, projEntity.motionZ));
        }
    }

    @Override
    public float getSpeed() {
        return TurretCryolator.projSpeed;
    }

    @Override
    public float getDamage() {
        return 0.0F;
    }

    @Override
    public float getKnockbackHorizontal() {
        return TurretCryolator.projKnockbackH;
    }

    @Override
    public float getKnockbackVertical() {
        return TurretCryolator.projKnockbackV;
    }

    @Override
    public double getScatterValue() {
        return TurretCryolator.projScatter;
    }

    @Override
    public boolean onDamageEntityPre(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc, MutableFloat damage) {
        if( !projectile.get().world.isRemote && target instanceof EntityLivingBase ) {
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.duration, this.level));
            return false;
        }

        return true;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return Sounds.RICOCHET_SPLASH;
    }

    @Override
    public void onDamageEntityPost(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc) {
        if( target instanceof EntityLivingBase ) {
            ((EntityLivingBase) target).hurtResistantTime = 0;
        }
    }
}
