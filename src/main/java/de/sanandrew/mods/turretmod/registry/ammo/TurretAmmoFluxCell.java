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
import de.sanandrew.mods.turretmod.api.ammo.ITurretAmmo;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileLaser;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TurretAmmoFluxCell
        implements ITurretAmmo<EntityProjectileLaser>
{
    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoFluxCell(boolean isMulti, String modelName) {
        this.name = isMulti ? "ecell_lrg" : "ecell_sng";
        this.uuid = isMulti ? TurretAmmunitions.FLUXCELL_PACK : TurretAmmunitions.FLUXCELL;
        this.capacity = isMulti ? 16 : 1;
        this.itemModel = new ResourceLocation(TmrConstants.ID, "turret_ammo/" + modelName);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

    @Override
    public int getAmmoCapacity() {
        return this.capacity;
    }

    @Override
    public Class<EntityProjectileLaser> getEntityClass() {
        return EntityProjectileLaser.class;
    }

    @Override
    public ITurret getTurret() {
        return Turrets.LASER;
    }

    @Override
    public float getInfoDamage() {
        return 2.5F;
    }

    @Override
    public UUID getTypeId() {
        return TurretAmmunitions.FLUXCELL;
    }

    @Override
    public UUID getGroupId() {
        return TurretAmmunitions.FLUXCELL;
    }

    @Override
    public String getInfoName() {
        return "ecell";
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.turret_ammo.getAmmoItem(1, TurretAmmoRegistry.INSTANCE.getType(TurretAmmunitions.FLUXCELL));
    }

    @Override
    public EntityProjectileLaser getEntity(ITurretInst turretInst) {
        return new EntityProjectileLaser(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    public static class Single
            extends TurretAmmoFluxCell
    {
        public Single() {
            super(false, "ecell");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.FLUXCELL_SNG;
        }
    }

    public static class Multi
            extends TurretAmmoFluxCell
    {
        public Multi() {
            super(true, "ecell_pack");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.FLUXCELL_MTP;
        }
    }
}
