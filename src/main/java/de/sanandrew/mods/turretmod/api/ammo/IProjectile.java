package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistryObject;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p>A turret projectile defined by its ammunition.</p>
 */
@SuppressWarnings("unused")
public interface IProjectile
        extends IRegistryObject
{
    /**
     * <p>Returns the speed with which the projectile travels.</p>
     *
     * @return the speed of the projectile.
     */
    float getSpeed();

    /**
     * <p>Returns the value of the trajectory arc. Higher values mean a greater curvature.</p>
     * <p>Turrets will shoot at an angle to still be able to hit targets.</p>
     *
     * @return the value of the trajectory arc.
     */
    float getArc();

    /**
     * <p>Returns the damage (in health points; 1.0 HP = ½ hearts) the projectile can deal to an entity.</p>
     * <p>All parameters may be <tt>null</tt> if the method is invoked solely for informational context.</p>
     *
     * @param turret The turret that shoots this projectile
     * @param projectile The projectile entity
     * @param target The target that got hit
     * @param damageSrc The damage source that causes the damage
     *
     * @param attackModifier
     * @return the damage the projectile deals on hit.
     */
    float getDamage(@Nullable ITurretInst turret, @Nullable IProjectileInst projectile, @Nullable Entity target, @Nullable DamageSource damageSrc, float attackModifier);

    /**
     * <p>Returns the damage (in health points; 1.0 HP = ½ hearts) the projectile can deal to an entity.</p>
     * <p>This method can (and should) be invoked only for informational context.</p>
     *
     * @return the damage the projectile deals on hit.
     */
    default float getDamage() {
        return this.getDamage(null, null, null, null, 1.0F);
    }

    /**
     * <p>Returns the amount of knockback the projectile deals to a target horizontally.</p>
     *
     * @return the amount of knockback.
     */
    float getKnockbackHorizontal();

    /**
     * <p>Returns the amount of knockback the projectile deals to a target vertically.</p>
     *
     * @return the amount of knockback.
     */
    float getKnockbackVertical();

    /**
     * <p>Returns the sound event which plays when the projectile hits something.</p>
     *
     * @return the sound event played on hit or <tt>null</tt>, if there is no sound to be played.
     */
    SoundEvent getRicochetSound();

    /**
     * <p>Returns how much the projectile can scatter when shot. Higher values mean less accuracy.</p>
     *
     * @return the scatter value.
     */
    double getScatterValue();

    /**
     * <p>Returns a custom damage source if it differs from the standard projectile damage source.</p>
     *
     * @param turret The turret that shoots this projectile.
     * @param projectile The projectile entity.
     * @param target The target that got hit.
     * @param type The type of entity the target should be recognized as, only differs from {@link TargetType#REGULAR} if the turret has an upgrade handling that type.
     *             <p><b>This method should return a different damage source type appropriate for this target type.</b></p>
     * @return a custom damage source appropriate for this projectile or <tt>null</tt>, if it's the standard projectile damage
     */
    default DamageSource getCustomDamageSource(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, Entity target, TargetType type) {
        return null;
    }

    /**
     * <p>Invoked when the projectile entity gets created by the turret.</p>
     *
     * @param turret The turret that creates this projectile.
     * @param projectile The projectile entity.
     */
    default void onShoot(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile) { }

    /**
     * <p>Invoked when the projectile ticks.</p>
     *
     * @param turret The turret that owns this projectile.
     * @param projectile The projectile entity.
     */
    default void tick(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile) { }

    /**
     * <p>Invoked when a projectile hits something, be it entity or block.</p>
     * <p>Returns wether or not the projectile should be processing the impact further (like damaging entities).</p>
     *
     * @param turret The turret that shoots this projectile.
     * @param projectile The projectile entity.
     * @param hitObj The object representing what the projectile hit.
     * @return <tt>true</tt>, if the projectile should further process the impact; <tt>false</tt> otherwise
     */
    default boolean processImpact(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, RayTraceResult hitObj) {
        return true;
    }

    /**
     * <p>Invoked when a projectile finishes hitting something, be it entity or block.</p>
     * <p>Returns wether or not the projectile should be killed and play the ricochet sound.</p>
     *
     * @param turret The turret that shoots this projectile.
     * @param projectile The projectile entity.
     * @param hitObj The object representing what the projectile hit.
     * @return <tt>true</tt>, if the projectile should be stopped and killed; <tt>false</tt> otherwise
     */
    default boolean finishImpact(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, RayTraceResult hitObj) {
        return true;
    }

    /**
     * <p>Invoked when an entity is successfully damaged by this projectile.</p>
     *
     * @param turret The turret that shoots this projectile.
     * @param projectile The projectile entity.
     * @param target The target that got hit.
     * @param damageSrc The damage source that caused the damage.
     */
    default void onPostEntityDamage(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile, Entity target, DamageSource damageSrc) { }

    /**
     * <p>Returns the multiplier the projectile applies to its velocity whilst traveling in air. A lower value means higher slowdown.</p>
     * <p>It is clamped between <tt>0.0</tt> (complete standstill) and <tt>1.0</tt> (unaltered speed).</p>
     *
     * @return the value by which velocity is multiplied each tick
     */
    default float getAirInertia() {
        return 1.0F;
    }

    /**
     * <p>Returns the multiplier the projectile applies to its velocity whilst traveling in liquids. A lower value means higher slowdown.</p>
     * <p>It is clamped between <tt>0.0</tt> (complete standstill) and <tt>1.0</tt> (unaltered speed).</p>
     * <p>The viscosity of the fluid (calculated by its tick delay) should be respected. The higher the viscosity, the slower the projectile should move.</p>
     *
     * @param viscosity the viscosity of the liquid the projectile is in. A viscosity of 1.0 is equal to water.
     * @return the value by which velocity is multiplied each tick
     * @see net.minecraft.fluid.Fluid#getTickDelay(net.minecraft.world.IWorldReader)
     */
    @SuppressWarnings("JavadocReference")
    default float getFluidInertia(float viscosity) {
        return 0.8F * (2.0F - viscosity);
    }

    default boolean detectFluidCollision() {
        return false;
    }

    /**
     * <p>An enumerator determining the type of the target when acquiring the source of damage.</p>
     *
     * @see IProjectile#getCustomDamageSource(ITurretInst, IProjectileInst, Entity, TargetType)
     */
    enum TargetType
    {
        /**
         * A regular entity, nothing special about that
         */
        REGULAR,
        /**
         * Endermen teleport away when taking projectile damage, preventing them to get hit
         */
        SPECIAL_ENDERMAN,
        /**
         * An ender dragon can only be hurt if the attacker is a player or the damage type is an explosion
         */
        SPECIAL_ENDER_DRAGON,
        /**
         * Withers won't take hits by projectile damage as soon as their health falls below half of maximum
         */
        SPECIAL_WITHER
    }
}
