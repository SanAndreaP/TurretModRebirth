/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileInst;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ProjectileRegistry
        implements IProjectileRegistry
{
    public static final ProjectileRegistry INSTANCE = new ProjectileRegistry();

    private static final IProjectile EMPTY = new IProjectile() {
        @Nonnull @Override public ResourceLocation getId() { return new ResourceLocation("null"); }
        @Override public float getSpeed() { return 0; }
        @Override public float getArc() { return 0; }
        @Override public float getDamage(@Nullable ITurretInst turret, @Nullable IProjectileInst projectile, @Nullable Entity target, @Nullable DamageSource damageSrc, float attackModifier) { return 0; }
        @Override public float getKnockbackHorizontal() { return 0; }
        @Override public float getKnockbackVertical() { return 0; }
        @Override public SoundEvent getRicochetSound() { return null; }
        @Override public double getScatterValue() { return 0; }
        @Override public boolean isValid() { return false; }
        @Override public ResourceLocation getTexture(IProjectileInst projectileInst) { return null; }
    };

    private final Map<ResourceLocation, IProjectile> projectiles;
    private final Collection<IProjectile> projList;

    private ProjectileRegistry() {
        this.projectiles = new HashMap<>();
        this.projList = Collections.unmodifiableCollection(projectiles.values());
    }

    @Nonnull
    @Override
    public IProjectile get(ResourceLocation id) {
        return this.projectiles.getOrDefault(id, EMPTY);
    }

    @Nonnull
    @Override
    public IProjectile get(ItemStack stack) {
        return EMPTY;
    }

    @Nonnull
    @Override
    public IProjectile getDefault() {
        return EMPTY;
    }

    @Override
    public void register(@Nonnull IProjectile obj) {
        this.projectiles.put(obj.getId(), obj);
    }

    @Nonnull
    @Override
    public Collection<IProjectile> getAll() {
        return this.projList;
    }
}
