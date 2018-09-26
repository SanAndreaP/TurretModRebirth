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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TurretAmmoFluxCell
        implements IAmmunition
{
    private final String name;
    private final UUID id;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoFluxCell(boolean isMulti) {
        this.name = isMulti ? "fluxcell_pack" : "fluxcell";
        this.id = isMulti ? Ammunitions.FLUXCELL_PACK : Ammunitions.FLUXCELL;
        this.capacity = isMulti ? 16 : 1;
        this.itemModel = new ResourceLocation(TmrConstants.ID, "turret_ammo/" + this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public int getAmmoCapacity() {
        return this.capacity;
    }

    @Override
    public float getDamageInfo() {
        return 2.5F;
    }

    @Override
    public UUID getTypeId() {
        return Ammunitions.FLUXCELL;
    }

    @Nonnull
    @Override
    public IAmmunitionGroup getGroup() {
        return Ammunitions.Groups.FLUX_CELL;
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return AmmunitionRegistry.INSTANCE.getAmmoItem(AmmunitionRegistry.INSTANCE.getType(Ammunitions.FLUXCELL));
    }

    @Override
    public ITurretProjectile getProjectile(ITurretInst turretInst) {
        return Projectiles.LASER;
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }
}
