/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileBullet;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectilePebble;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class TurretAmmoBullet
        implements TurretAmmo
{
    public static final UUID BULLET_UUID = UUID.fromString("E8CB6C41-00FE-4FA0-AD98-FC8DAD6609AC");
    public static final UUID PACK_UUID = UUID.fromString("FD7B2FBF-9BB7-437F-9FF0-A21842D3A94A");
    private static final UUID TYPE_UUID = BULLET_UUID;

    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoBullet(boolean isMulti, String modelName) {
        this.name = isMulti ? "bullet_lrg" : "bullet_sng";
        this.uuid = isMulti ? PACK_UUID : BULLET_UUID;
        this.capacity = isMulti ? 32 : 2;
        this.itemModel = new ResourceLocation(TurretModRebirth.ID, "ammo/" + modelName);
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
        return EntityProjectileBullet.class;
    }

    @Override
    public Class<? extends EntityTurret> getTurret() {
        return EntityTurretRevolver.class;
    }

    @Override
    public float getInfoDamage() {
        return 2.75F;
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
        return "bullet";
    }

    @Override
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(BULLET_UUID));
    }

    @Override
    public IProjectile getEntity(EntityTurret turret) {
        return new EntityProjectileBullet(turret.worldObj, turret, turret.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    public static class Single
            extends TurretAmmoBullet
    {
        public Single() {
            super(false, "bullet");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.BULLET_SNG;
        }
    }

    public static class Multi
            extends TurretAmmoBullet
    {
        public Multi() {
            super(true, "bullet_pack");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.BULLET_MTP;
        }
    }
}
