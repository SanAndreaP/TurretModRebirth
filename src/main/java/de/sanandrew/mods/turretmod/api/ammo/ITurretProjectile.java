package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * <p>An object defining a projectile fired by a turret.</p>
 * <p>This interface is used as a delegate for the actual projectile entity. An instance of this delegate is to be registered with the {@link IProjectileRegistry} </p>
 */
@SuppressWarnings("unused")
public interface ITurretProjectile
{
    /**
     * <p>Returns the ID of this projectile. Must be unique to each projectile delegate.</p>
     * <p>Cannot be <tt>null</tt>!</p>
     *
     * @return the unique ID for this delegate.
     */
    @Nonnull
    UUID getId();

    /**
     * <p>Returns the speed with which the projectile travels.</p>
     *
     * @return the speed of the projectile
     */
    float getSpeed();

    /**
     * <p>Returns the value of the trajectory arc. Higher values mean a greater curvature.</p>
     * <p>Turrets will shoot at an angle to still be able to hit targets.</p>
     *
     * @return the value of the trajectory arc
     */
    float getArc();

    /**
     * <p>Returns the damage the projectile can deal to an entity.</p>
     *
     * @return the damage the projectile deals on hit
     */
    float getDamage();

    /**
     * <p>Returns the amount of knockback the projectile deals to a target horizontally.</p>
     *
     * @return the amount of knockback
     */
    float getKnockbackHorizontal();

    /**
     * <p>Returns the amount of knockback the projectile deals to a target vertically.</p>
     *
     * @return the amount of knockback
     */
    float getKnockbackVertical();

    /**
     * <p>Returns the sound event which plays when the projectile hits something.</p>
     *
     * @return the sound event played on hit or <tt>null</tt>, if there is no sound to be played
     */
    SoundEvent getRicochetSound();

    /**
     * <p>Returns how much the projectile can scatter when shot. Higher values mean less accuracy.</p>
     *
     * @return the scatter value
     */
    double getScatterValue();

    /**
     * <p>Returns a custom damage source if it differs from the standard projectile damage source.</p>
     *
     * @param turret The turret that shoots this projectile
     * @param projectile The projectile entity
     * @param target The target that got hit
     * @param isIndirect Wether or not this can return something like {@link DamageSource#causeThrownDamage(Entity, Entity)}
     *                   <p>If false return something to prevent endermen from teleporting (so something that's not indirect nor unblockable damage)</p>
     * @return a custom damage source appropriate for this projectile or <tt>null</tt>, if it's the standard projectile damage
     */
    default DamageSource getCustomDamageSrc(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, boolean isIndirect) {
        return null;
    }

    /**
     * <p>Called when the projectile entity gets created by the turret.</p>
     *
     * @param turret The turret that shoots this projectile
     * @param projectile The projectile entity
     */
    default void onCreate(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile) { }

    /**
     * <p>Called when the projectile updates. You can spawn particles here or something.</p>
     *
     * @param turret The turret that shoots this projectile
     * @param projectile The projectile entity
     */
    default void onUpdate(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile) { }

    /**
     * <p>Called when a projectile hits something, be it entity or block and returns wether or not the projectile will be killed and play the ricochet sound.</p>
     *
     * @param turret The turret that shoots this projectile
     * @param projectile The projectile entity
     * @param hitObj The object representing what the projectile hit
     * @return <tt>true</tt>, if the projectile should be stopped and killed, <tt>false</tt> otherwise
     */
    default boolean onHit(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, RayTraceResult hitObj) {
        return true;
    }

    /**
     * <p>Called when an entity is about to be damaged by this projectile.</p>
     * <p>If this returns <tt>false</tt>, the projectile won't damage the target entity and no further processing on that entity (like knockback
     * or setting revenge target) will be done.</p>
     *
     * @param turret The turret that shoots this projectile
     * @param projectile The projectile entity
     * @param target The target that got hit
     * @param damageSrc The damage source that causes the damage
     * @param damage A modifiable object containing the damage value
     * @return <tt>true</tt>, if the target should be damaged, <tt>false</tt> otherwise
     */
    default boolean onDamageEntityPre(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc, MutableFloat damage) {
        return !(target instanceof EntityWither && ((EntityWither) target).isArmored() && damageSrc.isProjectile());
    }

    /**
     * <p>Called when an entity is successfully damaged by this projectile.</p>
     *
     * @param turret The turret that shoots this projectile
     * @param projectile The projectile entity
     * @param target The target that got hit
     * @param damageSrc The damage source that caused the damage
     */
    default void onDamageEntityPost(@Nullable ITurretInst turret, @Nonnull ITurretProjectileInst projectile, Entity target, DamageSource damageSrc) { }

    /**
     * <p>Returns how much the projectile keeps its velocity when traveling in air. Lower values mean more slowdown.</p>
     * <p>The value is clamped between <tt>0.0</tt> and <tt>1.0</tt></p>
     *
     * @return the value by which velocity is multiplied each tick
     */
    default float getSpeedMultiplierAir() {
        return 1.0F;
    }

    /**
     * <p>Returns how much the projectile keeps its velocity when traveling in liquids. Lower values mean more slowdown.</p>
     * <p>The value is clamped between <tt>0.0</tt> and <tt>1.0</tt> and is additionally dependend on the viscosity of the liquid.</p>
     *
     * @return the value by which velocity is multiplied each tick
     */
    default float getSpeedMultiplierLiquid() {
        return 0.8F;
    }
}
