/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionGroup;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.projectile.Projectiles;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class MinigunShell
        implements IAmmunition
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "ammo.minigunshell");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int getAmmoCapacity() {
        return 4;
    }

    @Override
    public float getDamageInfo() {
        return Projectiles.MG_PEBBLE.getDamage();
    }

    @Nonnull
    @Override
    public IAmmunitionGroup getGroup() {
        return Ammunitions.Groups.MG_SHELL;
    }

    @Override
    public ITurretProjectile getProjectile(ITurretInst turretInst) {
        return Projectiles.MG_PEBBLE;
    }
}
