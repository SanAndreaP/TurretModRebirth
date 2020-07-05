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
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.projectile.Projectiles;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public class Harpoon
        implements IAmmunition
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "ammo.harpoon");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getBookEntryId() {
        return Resources.PATCHOULI_E_AMMO_HARPOON.resource;
    }

    @Nonnull
    @Override
    public ITurret getTurret() {
        return Turrets.HARPOON;
    }

    @Override
    public int getAmmoCapacity() {
        return 1;
    }

    @Override
    public Range<Float> getDamageInfo() {
        return Range.is(Projectiles.HARPOON.getDamage());
    }

    @Override
    public IProjectile getProjectile(ITurretInst turretInst) {
        return Projectiles.HARPOON;
    }
}
