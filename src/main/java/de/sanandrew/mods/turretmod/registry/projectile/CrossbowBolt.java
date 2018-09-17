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
import de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

public class CrossbowBolt
        implements ITurretProjectile
{
    private static final UUID ID = UUID.fromString("F6311C66-393F-48C8-8BE6-8F6E51D0660F");

    @Nonnull
    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public float getArc() {
        return TurretCrossbow.projArc;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    @Override
    public float getSpeed() {
        return TurretCrossbow.projSpeed;
    }

    @Override
    public float getDamage() {
        return TurretCrossbow.projDamage;
    }

    @Override
    public float getKnockbackHorizontal() {
        return TurretCrossbow.projKnockbackH;
    }

    @Override
    public float getKnockbackVertical() {
        return TurretCrossbow.projKnockbackV;
    }

    @Override
    public double getScatterValue() {
        return TurretCrossbow.projScatter;
    }
}
