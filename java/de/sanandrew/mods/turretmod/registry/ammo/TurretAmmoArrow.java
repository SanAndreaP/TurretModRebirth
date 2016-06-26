/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class TurretAmmoArrow
        implements TurretAmmo
{
    public static final UUID ARROW_UUID = UUID.fromString("7b497e61-4e8d-4e49-ac71-414751e399e8");
    public static final UUID QUIVER_UUID = UUID.fromString("e6d51120-b52a-42ea-bf78-bebbc7d41c09");
    private static final UUID TYPE_UUID = ARROW_UUID;

    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoArrow(boolean quiver) {
        this.name = quiver ? "arrow_lrg" : "arrow_sng";
        this.uuid = quiver ? QUIVER_UUID : ARROW_UUID;
        this.capacity = quiver ? 16 : 1;
        this.itemModel = new ResourceLocation(TurretModRebirth.ID, "ammo/" + (quiver ? "arrow_pack" : "arrow"));
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
    public Class<? extends IProjectile> getEntityClass() {
        return EntityProjectileCrossbowBolt.class;
    }

    @Override
    public Class<? extends EntityTurret> getTurret() {
        return EntityTurretCrossbow.class;
    }

    @Override
    public float getInfoDamage() {
        return 3.0F;
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
        return "arrow";
    }

    @Override
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(ARROW_UUID));
    }

    @Override
    public IProjectile getEntity(EntityTurret turret) {
        return new EntityProjectileCrossbowBolt(turret.worldObj, turret, turret.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return itemModel;
    }

    public static class Single
            extends TurretAmmoArrow
    {
        public Single() {
            super(false);
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.ARROW_SNG;
        }
    }

    public static class Quiver
            extends TurretAmmoArrow
    {
        public Quiver() {
            super(true);
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.ARROW_MTP;
        }
    }
}
