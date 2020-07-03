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
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public class FluxCell
        implements IAmmunition
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "ammo.fluxcell");
    private static final ResourceLocation BOOK_ENTRY_ID = new ResourceLocation(TmrConstants.ID, "ammo/fluxcell");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getBookEntryId() {
        return BOOK_ENTRY_ID;
    }

    @Nonnull
    @Override
    public ITurret getTurret() {
        return Turrets.LASER;
    }

    @Override
    public int getAmmoCapacity() {
        return 1;
    }

    @Override
    public Range<Float> getDamageInfo() {
        return Range.is(Projectiles.LASER_NORMAL.getDamage());
    }

    @Override
    public IProjectile getProjectile(ITurretInst turretInst) {
        return turretInst.getUpgradeProcessor().hasUpgrade(Upgrades.ENDER_MEDIUM) ? Projectiles.LASER_BLURAY : Projectiles.LASER_NORMAL;
    }
}
