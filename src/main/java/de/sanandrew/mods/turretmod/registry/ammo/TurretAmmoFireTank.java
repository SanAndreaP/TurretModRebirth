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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileFlame;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretFlamethrower;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TurretAmmoFireTank
        implements ITurretAmmo<EntityProjectileFlame>
{
    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoFireTank(boolean isMulti, String modelName) {
        this.name = isMulti ? "tank_lrg" : "tank_sng";
        this.uuid = isMulti ? TurretAmmunitions.FUELTANK_PACK : TurretAmmunitions.FUELTANK;
        this.capacity = isMulti ? 256 : 16;
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
    public Class<EntityProjectileFlame> getEntityClass() {
        return EntityProjectileFlame.class;
    }

    @Override
    public Class<? extends EntityTurret> getTurret() {
        return EntityTurretFlamethrower.class;
    }

    @Override
    public float getInfoDamage() {
        return 0.5F;
    }

    @Override
    public UUID getTypeId() {
        return TurretAmmunitions.FUELTANK;
    }

    @Override
    public UUID getGroupId() {
        return TurretAmmunitions.FUELTANK;
    }

    @Override
    public String getInfoName() {
        return "tank";
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.turret_ammo.getAmmoItem(1, TurretAmmoRegistry.INSTANCE.getType(TurretAmmunitions.FUELTANK));
    }

    @Override
    public EntityProjectileFlame getEntity(EntityTurret turret) {
        EntityProjectileFlame flame = new EntityProjectileFlame(turret.world, turret, turret.getTargetProcessor().getTarget());
        flame.purifying = turret.getUpgradeProcessor().hasUpgrade(UpgradeRegistry.FUEL_PURIFY);
        return flame;
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    public static class Single
            extends TurretAmmoFireTank
    {
        public Single() {
            super(false, "tank");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.FUELTANK_SNG;
        }
    }

    public static class Multi
            extends TurretAmmoFireTank
    {
        public Multi() {
            super(true, "tank_pack");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.FUELTANK_MTP;
        }
    }
}
