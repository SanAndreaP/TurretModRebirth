/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

@Category("bullet")
@SuppressWarnings("WeakerAccess")
public class Bullet
        implements ITurretProjectile
{
    private static final UUID ID = UUID.fromString("C25768A6-0618-4E7A-98B2-EED2F79A74C2");

    @Value(comment = "Base damage this projectile can deal to a target.", range = @Range(minD = 0.0D, maxD = 1024.0D))
    public static float damage = 2.75F;
    @Value(comment = "Multiplier applied to the speed with which this projectile travels.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float speed = 3.0F;
    @Value(comment = "How much this projectile curves down/up. negative values let it go up, whereas positive values go down.", range = @Range(minD = -10.0D, maxD = 10.0D))
    public static float arc = 0.05F;
    @Value(comment = "Horizontal knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackH = 0.005F;
    @Value(comment = "Vertical (y) knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackV = 0.1F;
    @Value(comment = "How much more inaccurate this projectiles' trajectory vector becomes. Higher values result in less accuracy.", range = @Range(minD = 0.0D, maxD = 10.0D))
    public static double scatter = 0.0D;

    @Nonnull
    @Override
    public UUID getId() {
        return ID;
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
        return Sounds.RICOCHET_BULLET;
    }
}
