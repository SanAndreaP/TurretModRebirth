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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileMinigunPebble;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretMinigun;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TurretAmmoMinigunShell
        implements TurretAmmo<EntityProjectileMinigunPebble>
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
    public Class<EntityProjectileMinigunPebble> getEntityClass() {
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
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.turret_ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(SHELL_UUID));
    }

    @Override
    public EntityProjectileMinigunPebble getEntity(EntityTurret turret) {
        return new EntityProjectileMinigunPebble(turret.world, turret, turret.getTargetProcessor().getTarget());
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
