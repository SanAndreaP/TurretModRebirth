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
import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Category("flame")
@SuppressWarnings("WeakerAccess")
public class Flame
        implements IProjectile
{
    static final ResourceLocation ID1 = new ResourceLocation(TmrConstants.ID, "flame.normal");
    static final ResourceLocation ID2 = new ResourceLocation(TmrConstants.ID, "flame.pure");

    @Value(comment = "Base damage this projectile can deal to a target.", range = @Range(minD = 0.0D, maxD = 1024.0D))
    public static float damage = 0.55F;
    @Value(comment = "Multiplier applied to the speed with which this projectile travels.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float speed = 0.6F;
    @Value(comment = "How much this projectile curves down/up. negative values let it go up, whereas positive values go down.", range = @Range(minD = -10.0D, maxD = 10.0D))
    public static float arc = -0.15F;
    @Value(comment = "Horizontal knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackH = 0.0F;
    @Value(comment = "Vertical (y) knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackV = 0.0F;
    @Value(comment = "How much more inaccurate this projectile's trajectory vector becomes. Higher values result in less accuracy.", range = @Range(minD = 0.0D, maxD = 10.0D))
    public static double scatter = 0.1D;
    @Value(comment = "How much damage this projectile looses after successfully hitting an entity. If the damage reaches 0, the projectile is killed.", range = @Range(minD = 0.0D, maxD = 1024.0D))
    public static float damageReduction = 0.5F;
    @Value(comment = "Wether or not purifying flames cause fire on blocks hit.")
    public static boolean purifyingFireBlocks = true;
    @Value(comment = "How high in percent the chance is of purifying flames causing fire on blocks hit.", range = @Range(minD = 0.0D, maxD = 100.0D))
    public static float purifyingFireBlocksChance = 1.0F;

    private final boolean purifying;
    private final ResourceLocation id;

    Flame(ResourceLocation id) {
        this.id = id;
        this.purifying = id.equals(ID2);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public void onCreate(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile) {
        if( turret != null ) {
            Entity projEntity = projectile.get();

            float rotXZ = -turret.get().rotationYawHead / 180.0F * (float) Math.PI;
            float rotY = -(turret.get().rotationPitch - 2.5F) / 180.0F * (float) Math.PI - 0.1F;

            projEntity.posX += Math.sin(rotXZ) * 0.7F * Math.cos(rotY);
            projEntity.posY += Math.sin(rotY) * 0.6F;
            projEntity.posZ += Math.cos(rotXZ) * 0.7F * Math.cos(rotY);

            projEntity.setPosition(projEntity.posX, projEntity.posY, projEntity.posZ);
        }
    }

    @Override
    public float getArc() {
        return arc;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public float getKnockbackHorizontal() {
        return knockbackH;
    }

    @Override
    public float getKnockbackVertical() {
        return knockbackV;
    }

    @Override
    public double getScatterValue() {
        return scatter;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    @Override
    public DamageSource getCustomDamageSrc(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, Entity target, TargetType type) {
        return EntityTurretProjectile.getDamageSource(turret, projectile, type).setFireDamage();
    }

    @Override
    public boolean onHit(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, RayTraceResult hitObj) {
        if( hitObj.typeOfHit != RayTraceResult.Type.ENTITY ) {
            Entity projEntity = projectile.get();
            if( this.purifying && purifyingFireBlocks && hitObj.typeOfHit == RayTraceResult.Type.BLOCK && !projEntity.world.isRemote && MiscUtils.RNG.randomFloat() * 100.0 < purifyingFireBlocksChance ) {
                BlockPos fire = hitObj.getBlockPos().offset(hitObj.sideHit);
                if( projEntity.world.isAirBlock(fire) ) {
                    projEntity.world.setBlockState(fire, Blocks.FIRE.getDefaultState(), 11); // 1 = block update, 2 = send to client, 8 = needs update
                }
            }

            return true;
        } else {
            float lastDmg = projectile.getLastCausedDamage();
            hitObj.entityHit.setFire(10);
            return lastDmg >= 0.0F && lastDmg - damageReduction <= 0.0F;
        }
    }

    @Override
    public boolean onDamageEntityPre(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, Entity target, DamageSource damageSrc, MutableFloat damage) {
        if( projectile.getLastDamagedEntity() == target ) {
            return false;
        }

        float lastDmg = projectile.getLastCausedDamage();
        if( lastDmg > 0.0F ) {
            damage.setValue(lastDmg - damageReduction);
        }

        return true;
    }
}
