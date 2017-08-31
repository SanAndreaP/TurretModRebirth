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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectilePebble;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TurretAmmoShotgunShell
        implements IAmmunition<EntityProjectilePebble>
{
    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoShotgunShell(boolean isMulti, String modelName) {
        this.name = isMulti ? "sgshell_lrg" : "sgshell_sng";
        this.uuid = isMulti ? Ammunitions.SGSHELL_PACK : Ammunitions.SGSHELL;
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
    public Class<EntityProjectilePebble> getEntityClass() {
        return EntityProjectilePebble.class;
    }

    @Override
    public ITurret getTurret() {
        return Turrets.SHOTGUN;
    }

    @Override
    public float getInfoDamage() {
        return 0.5F;
    }

    @Override
    public UUID getTypeId() {
        return Ammunitions.SGSHELL;
    }

    @Override
    public UUID getGroupId() {
        return Ammunitions.SGSHELL;
    }

    @Override
    public String getInfoName() {
        return "sgshell";
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.turret_ammo.getAmmoItem(1, AmmunitionRegistry.INSTANCE.getType(Ammunitions.SGSHELL));
    }

    @Override
    public EntityProjectilePebble getEntity(ITurretInst turretInst) {
        return new EntityProjectilePebble(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    public static class Single
            extends TurretAmmoShotgunShell
    {
        public Single() {
            super(false, "shotgun_shell");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.SGSHELL_SNG;
        }
    }

    public static class Multi
            extends TurretAmmoShotgunShell
    {
        public Multi() {
            super(true, "shotgun_shell_pack");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.SGSHELL_MTP;
        }
    }
}
