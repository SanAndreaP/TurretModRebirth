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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCryoCell;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TurretAmmoCryoCell
        implements ITurretAmmo<EntityProjectileCryoCell>
{private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoCryoCell(String name, UUID uuid, int capacity) {
        this.name = name;
        this.uuid = uuid;
        this.capacity = capacity;
        this.itemModel = new ResourceLocation(TmrConstants.ID, "turret_ammo/" + name);
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
    public Class<EntityProjectileCryoCell> getEntityClass() {
        return EntityProjectileCryoCell.class;
    }

    @Override
    public Class<? extends EntityTurret> getTurret() {
        return EntityTurretCryolator.class;
    }

    @Override
    public float getInfoDamage() {
        return 0.0F;
    }

    @Override
    public UUID getGroupId() {
        return TurretAmmunitions.CRYOCELL_MK1;
    }

    @Override
    public String getInfoName() {
        return "cryocell";
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.turret_ammo.getAmmoItem(1, TurretAmmoRegistry.INSTANCE.getType(this.getTypeId()));
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    public static class SingleMK1
            extends TurretAmmoCryoCell
    {
        public SingleMK1() {
            super("cryocell_1", TurretAmmunitions.CRYOCELL_MK1, 1);
        }

        @Override
        public UUID getTypeId() {
            return TurretAmmunitions.CRYOCELL_MK1;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.CRYOCELL_1_SNG;
        }

        @Override
        public EntityProjectileCryoCell getEntity(EntityTurret turret) {
            return new EntityProjectileCryoCell(turret.world, turret, turret.getTargetProcessor().getTarget()).setLevelAndDuration(0, 300);
        }
    }

    public static class MultiMK1
            extends TurretAmmoCryoCell
    {
        public MultiMK1() {
            super("cryocell_pack_1", TurretAmmunitions.CRYOCELL_PACK_MK1, 16);
        }

        @Override
        public UUID getTypeId() {
            return TurretAmmunitions.CRYOCELL_MK1;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.CRYOCELL_1_MTP;
        }

        @Override
        public EntityProjectileCryoCell getEntity(EntityTurret turret) {
            return new EntityProjectileCryoCell(turret.world, turret, turret.getTargetProcessor().getTarget()).setLevelAndDuration(0, 300);
        }
    }

    public static class SingleMK2
            extends TurretAmmoCryoCell
    {
        public SingleMK2() {
            super("cryocell_2", TurretAmmunitions.CRYOCELL_MK2, 1);
        }

        @Override
        public UUID getTypeId() {
            return TurretAmmunitions.CRYOCELL_MK2;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.CRYOCELL_2_SNG;
        }

        @Override
        public EntityProjectileCryoCell getEntity(EntityTurret turret) {
            return new EntityProjectileCryoCell(turret.world, turret, turret.getTargetProcessor().getTarget()).setLevelAndDuration(2, 200);
        }
    }

    public static class MultiMK2
            extends TurretAmmoCryoCell
    {
        public MultiMK2() {
            super("cryocell_pack_2", TurretAmmunitions.CRYOCELL_PACK_MK2, 16);
        }

        @Override
        public UUID getTypeId() {
            return TurretAmmunitions.CRYOCELL_MK2;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.CRYOCELL_2_MTP;
        }

        @Override
        public EntityProjectileCryoCell getEntity(EntityTurret turret) {
            return new EntityProjectileCryoCell(turret.world, turret, turret.getTargetProcessor().getTarget()).setLevelAndDuration(2, 200);
        }
    }

    public static class SingleMK3
            extends TurretAmmoCryoCell
    {
        public SingleMK3() {
            super("cryocell_3", TurretAmmunitions.CRYOCELL_MK3, 1);
        }

        @Override
        public UUID getTypeId() {
            return TurretAmmunitions.CRYOCELL_MK3;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.CRYOCELL_3_SNG;
        }

        @Override
        public EntityProjectileCryoCell getEntity(EntityTurret turret) {
            return new EntityProjectileCryoCell(turret.world, turret, turret.getTargetProcessor().getTarget()).setLevelAndDuration(4, 100);
        }
    }

    public static class MultiMK3
            extends TurretAmmoCryoCell
    {
        public MultiMK3() {
            super("cryocell_pack_3", TurretAmmunitions.CRYOCELL_PACK_MK3, 16);
        }

        @Override
        public UUID getTypeId() {
            return TurretAmmunitions.CRYOCELL_MK3;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.CRYOCELL_3_MTP;
        }

        @Override
        public EntityProjectileCryoCell getEntity(EntityTurret turret) {
            return new EntityProjectileCryoCell(turret.world, turret, turret.getTargetProcessor().getTarget()).setLevelAndDuration(4, 100);
        }
    }
}
