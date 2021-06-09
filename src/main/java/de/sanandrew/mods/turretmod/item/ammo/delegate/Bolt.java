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
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.projectile.Projectiles;
import de.sanandrew.mods.turretmod.entity.turret.Turrets;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class Bolt
        extends Ammunition
{
    public Bolt(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getAmmoCapacity() {
        return 1;
    }

    @Nonnull
    @Override
    public ITurret getTurret() {
        return Turrets.CROSSBOW;
    }

    @Override
    public IProjectile getProjectile(ITurretInst turretInst) {
        return Projectiles.CB_BOLT;
    }
}
