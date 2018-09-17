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
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

public class Bullet
        implements ITurretProjectile
{
    private static final UUID ID = UUID.fromString("C25768A6-0618-4E7A-98B2-EED2F79A74C2");

    @Nonnull
    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public float getArc() {
        return 0.05F;
    }

    @Override
    public float getSpeed() {
        return 3.0F;
    }

    @Override
    public float getDamage() {
        return 2.75F;
    }

    @Override
    public float getKnockbackHorizontal() {
        return 0.005F;
    }

    @Override
    public float getKnockbackVertical() {
        return 0.1F;
    }

    @Override
    public double getScatterValue() {
        return 0;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return Sounds.RICOCHET_BULLET;
    }
}
