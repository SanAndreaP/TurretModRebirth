/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileLaser;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretLaser;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class TurretAmmoFluxCell
        implements TurretAmmo<EntityProjectileLaser>
{
    public static final UUID CELL_UUID = UUID.fromString("48800C6A-9A31-4F45-8AD5-DD02B8B18BCB");
    public static final UUID PACK_UUID = UUID.fromString("1F427D47-1BED-41D4-8810-47FF274424B6");
    private static final UUID TYPE_UUID = CELL_UUID;

    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoFluxCell(boolean isMulti, String modelName) {
        this.name = isMulti ? "ecell_lrg" : "ecell_sng";
        this.uuid = isMulti ? PACK_UUID : CELL_UUID;
        this.capacity = isMulti ? 16 : 1;
        this.itemModel = new ResourceLocation(TurretModRebirth.ID, "turret_ammo/" + modelName);
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
    public Class<? extends EntityTurret> getTurret() {
        return EntityTurretLaser.class;
    }

    @Override
    public float getInfoDamage() {
        return 2.5F;
    }

    @Override
    public UUID getTypeId() {
        return TYPE_UUID;
    }

    @Override
    public UUID getGroupId() {
        return TYPE_UUID;
    }

    @Override
    public String getInfoName() {
        return "ecell";
    }

    @Override
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(CELL_UUID));
    }

    @Override
    public EntityProjectileLaser getEntity(EntityTurret turret) {
        return new EntityProjectileLaser(turret.world, turret, turret.getTargetProcessor().getTarget());
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
            return TurretAssemblyRecipes.BULLET_SNG;
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
            return TurretAssemblyRecipes.BULLET_MTP;
        }
    }
}
