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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCryoCell;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class TurretAmmoCryoCell
        implements TurretAmmo<EntityProjectileCryoCell>
{
    public static final UUID CELL_MK1_UUID = UUID.fromString("0B567594-E5CA-48B5-A538-E87C213F439C");
    public static final UUID PACK_MK1_UUID = UUID.fromString("7DE80386-CE9E-4039-ADA6-F7131996E522");
    public static final UUID CELL_MK2_UUID = UUID.fromString("CB5BE826-0480-4D30-AF1F-23BE19329B37");
    public static final UUID PACK_MK2_UUID = UUID.fromString("82D1E748-ABDE-4911-96B3-B43E5AA716CB");
    public static final UUID CELL_MK3_UUID = UUID.fromString("3181E328-0151-44E0-ADD2-5FCB6B724AEC");
    public static final UUID PACK_MK3_UUID = UUID.fromString("399B8468-B68F-40FD-B442-156760161283");
    public static final UUID TYPE_MK1_UUID = CELL_MK1_UUID;
    public static final UUID TYPE_MK2_UUID = CELL_MK2_UUID;
    public static final UUID TYPE_MK3_UUID = CELL_MK3_UUID;

    private final String name;
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
        return TYPE_MK1_UUID;
    }

    @Override
    public String getInfoName() {
        return "cryocell";
    }

    @Override
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(this.getTypeId()));
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    public static class SingleMK1
            extends TurretAmmoCryoCell
    {
        public SingleMK1() {
            super("cryocell_1", CELL_MK1_UUID, 1);
        }

        @Override
        public UUID getTypeId() {
            return TYPE_MK1_UUID;
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
            super("cryocell_pack_1", PACK_MK1_UUID, 16);
        }

        @Override
        public UUID getTypeId() {
            return TYPE_MK1_UUID;
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
            super("cryocell_2", CELL_MK2_UUID, 1);
        }

        @Override
        public UUID getTypeId() {
            return TYPE_MK2_UUID;
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
            super("cryocell_pack_2", PACK_MK2_UUID, 16);
        }

        @Override
        public UUID getTypeId() {
            return TYPE_MK2_UUID;
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
            super("cryocell_3", CELL_MK3_UUID, 1);
        }

        @Override
        public UUID getTypeId() {
            return TYPE_MK3_UUID;
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
            super("cryocell_pack_3", PACK_MK3_UUID, 16);
        }

        @Override
        public UUID getTypeId() {
            return TYPE_MK3_UUID;
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
