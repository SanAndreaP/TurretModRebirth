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
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.projectile.Projectiles;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public class MinigunShell
        implements IAmmunition
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "ammo_minigun_shell");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nonnull
    @Override
    public ITurret getTurret() {
        return Turrets.MINIGUN;
    }

    @Override
    public int getAmmoCapacity() {
        return 4;
    }

    @Override
    public Range<Float> getDamageInfo() {
        return Range.is(Projectiles.MG_PEBBLE.getDamage());
    }

    @Override
    public IProjectile getProjectile(ITurretInst turretInst) {
        return Projectiles.MG_PEBBLE;
    }
}
