/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileBullet;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileFlame;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretFlamethrower;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class TurretAmmoFireTank
        implements TurretAmmo<EntityProjectileFlame>
{
    public static final UUID TANK_UUID = UUID.fromString("0CA51FA8-FD33-4C3D-A9AB-BA29DFFF4ABA");
    public static final UUID PACK_UUID = UUID.fromString("5C41DA09-8C7E-4191-8520-058E69C36DC0");
    private static final UUID TYPE_UUID = TANK_UUID;

    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoFireTank(boolean isMulti, String modelName) {
        this.name = isMulti ? "tank_lrg" : "tank_sng";
        this.uuid = isMulti ? PACK_UUID : TANK_UUID;
        this.capacity = isMulti ? 256 : 16;
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
        return TYPE_UUID;
    }

    @Override
    public UUID getGroupId() {
        return TYPE_UUID;
    }

    @Override
    public String getInfoName() {
        return "tank";
    }

    @Override
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TANK_UUID));
    }

    @Override
    public EntityProjectileFlame getEntity(EntityTurret turret) {
        return new EntityProjectileFlame(turret.worldObj, turret, turret.getTargetProcessor().getTarget());
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
            //TODO: change recipe
            return TurretAssemblyRecipes.BULLET_SNG;
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
            //TODO: change recipe
            return TurretAssemblyRecipes.BULLET_MTP;
        }
    }
}
