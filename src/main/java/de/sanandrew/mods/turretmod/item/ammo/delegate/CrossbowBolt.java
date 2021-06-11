/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item.ammo.delegate;

import de.sanandrew.mods.turretmod.api.ammo.Ammunition;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.entity.projectile.Projectiles;
import de.sanandrew.mods.turretmod.entity.turret.Turrets;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CrossbowBolt
        extends Ammunition
{
    public CrossbowBolt(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getCapacity() {
        return 1;
    }

    @Nonnull
    @Override
    public ITurret getApplicableTurret() {
        return Turrets.CROSSBOW;
    }

    @Override
    public IProjectile getProjectile(ITurretEntity turret) {
        return Projectiles.CB_BOLT;
    }
}
