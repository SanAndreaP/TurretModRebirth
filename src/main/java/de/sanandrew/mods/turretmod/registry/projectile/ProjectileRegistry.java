/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.turretmod.api.ammo.IProjectileRegistry;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ProjectileRegistry
        implements IProjectileRegistry
{
    public static final ProjectileRegistry INSTANCE = new ProjectileRegistry();

    private static final IProjectile NULL_PROJ = new IProjectile() {
        @Nonnull @Override public ResourceLocation getId() { return new ResourceLocation("null"); }
        @Override public float getSpeed() { return 0; }
        @Override public float getArc() { return 0; }
        @Override public float getDamage() { return 0; }
        @Override public float getKnockbackHorizontal() { return 0; }
        @Override public float getKnockbackVertical() { return 0; }
        @Override public SoundEvent getRicochetSound() { return null; }
        @Override public double getScatterValue() { return 0; }
        @Override public boolean isValid() { return false; }
    };

    private final Map<ResourceLocation, IProjectile> projectiles;
    private final Collection<IProjectile> projList;

    private ProjectileRegistry() {
        this.projectiles = new HashMap<>();
        this.projList = Collections.unmodifiableCollection(projectiles.values());
    }

    @Nonnull
    @Override
    public IProjectile getObject(ResourceLocation id) {
        return this.projectiles.getOrDefault(id, NULL_PROJ);
    }

    @Nonnull
    @Override
    public IProjectile getObject(ItemStack stack) {
        return NULL_PROJ;
    }

    @Nonnull
    @Override
    public IProjectile getDefaultObject() {
        return NULL_PROJ;
    }

    @Override
    public void register(@Nonnull IProjectile obj) {
        this.projectiles.put(obj.getId(), obj);
    }

    @Nonnull
    @Override
    public Collection<IProjectile> getObjects() {
        return this.projList;
    }
}
