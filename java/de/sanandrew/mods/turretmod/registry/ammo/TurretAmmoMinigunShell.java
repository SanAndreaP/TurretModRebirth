/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileMinigunPebble;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretMinigun;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class TurretAmmoMinigunShell
        implements TurretAmmo
{
    public static final UUID SHELL_UUID = UUID.fromString("3851173D-3AC3-4F17-A488-68C33716AF26");
    public static final UUID PACK_UUID = UUID.fromString("50634E1E-94C4-4EF6-8D76-7C8CADDCAE85");
    private static final UUID TYPE_UUID = SHELL_UUID;

    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoMinigunShell(boolean isMulti, String modelName) {
        this.name = isMulti ? "mgshell_lrg" : "mgshell_sng";
        this.uuid = isMulti ? PACK_UUID : SHELL_UUID;
        this.capacity = isMulti ? 64 : 4;
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
        return EntityProjectileMinigunPebble.class;
    }

    @Override
    public Class<? extends EntityTurret> getTurret() {
        return EntityTurretMinigun.class;
    }

    @Override
    public float getInfoDamage() {
        return 0.3F;
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
        return "mgshell";
    }

    @Override
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(SHELL_UUID));
    }

    @Override
    public IProjectile getEntity(EntityTurret turret) {
        return new EntityProjectileMinigunPebble(turret.worldObj, turret, turret.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    public static class Single
            extends TurretAmmoMinigunShell
    {
        public Single() {
            super(false, "minigun_shell");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.MGSHELL_SNG;
        }
    }

    public static class Multi
            extends TurretAmmoMinigunShell
    {
        public Multi() {
            super(true, "minigun_shell_pack");
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.MGSHELL_MTP;
        }
    }
}
