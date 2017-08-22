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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileBullet;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TurretAmmoBullet
        implements ITurretAmmo<EntityProjectileBullet>
{
    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoBullet(boolean isMulti, String modelName) {
        this.name = isMulti ? "bullet_lrg" : "bullet_sng";
        this.uuid = isMulti ? TurretAmmunitions.BULLET_PACK : TurretAmmunitions.BULLET;
        this.capacity = isMulti ? 32 : 2;
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
    public Class<EntityProjectileBullet> getEntityClass() {
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
        return TurretAmmunitions.BULLET;
    }

    @Override
    public UUID getGroupId() {
        return TurretAmmunitions.BULLET;
    }

    @Override
    public String getInfoName() {
        return "bullet";
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.turret_ammo.getAmmoItem(1, TurretAmmoRegistry.INSTANCE.getType(TurretAmmunitions.BULLET));
    }

    @Override
    public EntityProjectileBullet getEntity(EntityTurret turret) {
        return new EntityProjectileBullet(turret.world, turret, turret.getTargetProcessor().getTarget());
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
