/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityProjectileCrossbowBolt
        extends EntityTurretProjectile
{
    public EntityProjectileCrossbowBolt(World world) {
        super(world);
    }

    public EntityProjectileCrossbowBolt(World world, Entity shooter, Entity target) {
        super(world, shooter, target);
    }

    public EntityProjectileCrossbowBolt(World world, Entity shooter, Vec3 shootingVec) {
        super(world, shooter, shootingVec);
    }

    @Override
    public float getArc() {
        return 0.4F;
    }

    @Override
    public String getRicochetSound() {
        return "random.bowhit";
    }

    @Override
    public float getInitialSpeedMultiplier() {
        return 1.0F;
    }

    @Override
    public float getDamage() {
        return 2.0F;
    }

    @Override
    public float getKnockbackStrengthH() {
        return 0.01F;
    }

    @Override
    public float getKnockbackStrengthV() {
        return 0.2F;
    }
}
