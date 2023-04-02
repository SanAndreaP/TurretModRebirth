/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.ammo;

import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Projectile
        implements IProjectile
{
    private final ResourceLocation id;

    protected float speed = 1.0F;
    protected float arc = 0.4F;
    protected float damage = 4.0F;
    protected float horizontalKnockback = 0.01F;
    protected float verticalKnockback = 0.1F;
    protected double scatter = 0.1D;
    protected ResourceLocation ricochetSound = null;
    protected ResourceLocation texture = null;

    private SoundEvent ricochetSoundCache = null;

    protected Projectile(ResourceLocation id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getArc() {
        return this.arc;
    }

    @Override
    public float getDamage(@Nullable ITurretEntity turret, @Nullable IProjectileEntity projectile, @Nullable Entity target, @Nullable DamageSource damageSrc, float attackModifier) {
        return this.damage * attackModifier;
    }

    @Override
    public float getKnockbackHorizontal() {
        return this.horizontalKnockback;
    }

    @Override
    public float getKnockbackVertical() {
        return this.verticalKnockback;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return this.ricochetSoundCache = lazyLoad(this.ricochetSound, this.ricochetSoundCache);
    }

    @Override
    public double getScatterValue() {
        return this.scatter;
    }

    @Override
    public ResourceLocation getTexture(IProjectileEntity projectile) {
        return this.texture;
    }

    protected static SoundEvent lazyLoad(ResourceLocation id, SoundEvent currEvent) {
        return currEvent != null ? currEvent : (id != null ? ForgeRegistries.SOUND_EVENTS.getValue(id) : null);
    }
}
