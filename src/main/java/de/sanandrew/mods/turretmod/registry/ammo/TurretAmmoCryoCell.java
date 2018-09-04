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
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCryoCell;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TurretAmmoCryoCell
        implements IAmmunition<EntityProjectileCryoCell>
{private final String name;
    private final UUID id;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoCryoCell(String name, UUID id, int capacity) {
        this.name = name;
        this.id = id;
        this.capacity = capacity;
        this.itemModel = new ResourceLocation(TmrConstants.ID, "turret_ammo/" + name);
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
    public Class<EntityProjectileCryoCell> getEntityClass() {
        return EntityProjectileCryoCell.class;
    }

    @Override
    public ITurret getTurret() {
        return Turrets.CRYOLATOR;
    }

    @Override
    public float getInfoDamage() {
        return 0.0F;
    }

    @Override
    public UUID getGroupId() {
        return Ammunitions.CRYOCELL_MK1;
    }

    @Override
    public String getGroupName() {
        return "cryocell";
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return AmmunitionRegistry.INSTANCE.getAmmoItem(AmmunitionRegistry.INSTANCE.getType(this.getTypeId()));
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    public static class SingleMK1
            extends TurretAmmoCryoCell
    {
        public SingleMK1() {
            super("cryocell_1", Ammunitions.CRYOCELL_MK1, 1);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK1;
        }

        @Override
        public EntityProjectileCryoCell getEntity(ITurretInst turretInst) {
            return new EntityProjectileCryoCell(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget()).setLevelAndDuration(0, 300);
        }
    }

    public static class MultiMK1
            extends TurretAmmoCryoCell
    {
        public MultiMK1() {
            super("cryocell_pack_1", Ammunitions.CRYOCELL_PACK_MK1, 16);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK1;
        }

        @Override
        public EntityProjectileCryoCell getEntity(ITurretInst turretInst) {
            return new EntityProjectileCryoCell(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget()).setLevelAndDuration(0, 300);
        }
    }

    public static class SingleMK2
            extends TurretAmmoCryoCell
    {
        public SingleMK2() {
            super("cryocell_2", Ammunitions.CRYOCELL_MK2, 1);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK2;
        }

        @Override
        public EntityProjectileCryoCell getEntity(ITurretInst turretInst) {
            return new EntityProjectileCryoCell(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget()).setLevelAndDuration(2, 200);
        }
    }

    public static class MultiMK2
            extends TurretAmmoCryoCell
    {
        public MultiMK2() {
            super("cryocell_pack_2", Ammunitions.CRYOCELL_PACK_MK2, 16);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK2;
        }

        @Override
        public EntityProjectileCryoCell getEntity(ITurretInst turretInst) {
            return new EntityProjectileCryoCell(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget()).setLevelAndDuration(2, 200);
        }
    }

    public static class SingleMK3
            extends TurretAmmoCryoCell
    {
        public SingleMK3() {
            super("cryocell_3", Ammunitions.CRYOCELL_MK3, 1);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK3;
        }

        @Override
        public EntityProjectileCryoCell getEntity(ITurretInst turretInst) {
            return new EntityProjectileCryoCell(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget()).setLevelAndDuration(4, 100);
        }
    }

    public static class MultiMK3
            extends TurretAmmoCryoCell
    {
        public MultiMK3() {
            super("cryocell_pack_3", Ammunitions.CRYOCELL_PACK_MK3, 16);
        }

        @Override
        public UUID getTypeId() {
            return Ammunitions.CRYOCELL_MK3;
        }

        @Override
        public EntityProjectileCryoCell getEntity(ITurretInst turretInst) {
            return new EntityProjectileCryoCell(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget()).setLevelAndDuration(4, 100);
        }
    }
}
