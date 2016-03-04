package de.sanandrew.mods.turretmod.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
public abstract class EntityTurretProjectile
        extends Entity
        implements IProjectile
{
    public EntityTurretProjectile(World world) {
        super(world);
        this.renderDistanceWeight = 10.0D;
    }
}
