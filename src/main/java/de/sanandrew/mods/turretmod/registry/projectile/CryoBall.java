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
import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.EnumEffect;
import de.sanandrew.mods.turretmod.registry.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Category("cryo ball")
@SuppressWarnings("WeakerAccess")
public class CryoBall
        implements IProjectile
{
    static final ResourceLocation ID1 = new ResourceLocation(TmrConstants.ID, "cryoball.slow");
    static final ResourceLocation ID2 = new ResourceLocation(TmrConstants.ID, "cryoball.slower");
    static final ResourceLocation ID3 = new ResourceLocation(TmrConstants.ID, "cryoball.slowest");

    @Value(comment = "Base damage this projectile can deal to a target.", range = @Range(minD = 0.0D, maxD = 1024.0D))
    public static float damage = 0.0F;
    @Value(comment = "Multiplier applied to the speed with which this projectile travels.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float speed = 1.5F;
    @Value(comment = "How much this projectile curves down/up. negative values let it go up, whereas positive values go down.", range = @Range(minD = -10.0D, maxD = 10.0D))
    public static float arc = 0.05F;
    @Value(comment = "Horizontal knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackH = 0.0F;
    @Value(comment = "Vertical (y) knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackV = 0.0F;
    @Value(comment = "How much more inaccurate this projectile's trajectory vector becomes. Higher values result in less accuracy.", range = @Range(minD = 0.0D, maxD = 10.0D))
    public static double scatter = 0.1D;
    @Value(comment = "Which level of slowness this projectile applies on its first level.", range = @Range(minI = 0, maxI = 10))
    public static int slownessLevelFirst = 1;
    @Value(comment = "Which level of slowness this projectile applies on its second level.", range = @Range(minI = 0, maxI = 10))
    public static int slownessLevelSecond = 3;
    @Value(comment = "Which level of slowness this projectile applies on its third level.", range = @Range(minI = 0, maxI = 10))
    public static int slownessLevelThird = 5;
    @Value(comment = "How long the slowness lasts in ticks, when this projectile applies it on its first level. 20 ticks = 1 second.", range = @Range(minI = 0))
    public static int slownessDurationFirst = 300;
    @Value(comment = "How long the slowness lasts in ticks, when this projectile applies it on its second level. 20 ticks = 1 second.", range = @Range(minI = 0))
    public static int slownessDurationSecond = 250;
    @Value(comment = "How long the slowness lasts in ticks, when this projectile applies it on its third level. 20 ticks = 1 second.", range = @Range(minI = 0))
    public static int slownessDurationThird = 200;

    private final ResourceLocation id;

    CryoBall(ResourceLocation id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public float getArc() {
        return arc;
    }

    @Override
    public void onUpdate(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile) {
        Entity projEntity = projectile.get();
        if( projEntity.world.isRemote ) {
            EnumEffect.CRYO_PARTICLE.addEffect(projEntity, new Tuple(projEntity.motionX, projEntity.motionY, projEntity.motionZ));
        }
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
    public boolean onDamageEntityPre(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, Entity target, DamageSource damageSrc, MutableFloat damage) {
        if( !projectile.get().world.isRemote && target instanceof EntityLivingBase ) {
            if( this.id.equals(ID1) && slownessLevelFirst > 0 ) {
                ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, slownessDurationFirst, slownessLevelFirst - 1));
            } else if( this.id.equals(ID2) && slownessLevelSecond > 0 ) {
                ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, slownessDurationSecond, slownessLevelSecond - 1));
            } else if( this.id.equals(ID3) && slownessLevelThird > 0 ) {
                ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, slownessDurationThird, slownessLevelThird - 1));
            }

            return false;
        }

        return true;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return Sounds.RICOCHET_SPLASH;
    }

    @Override
    public void onDamageEntityPost(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, Entity target, DamageSource damageSrc) {
        target.hurtResistantTime = 0;
    }
}
