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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectilePebble;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public abstract class TurretAmmoShotgunShell
        implements TurretAmmo
{
    public static final UUID SHELL_UUID = UUID.fromString("3B3AA3F7-DA37-4B92-8F18-53694361447F");
    public static final UUID PACK_UUID = UUID.fromString("6F3DB2C0-E881-462A-AC3A-6358EA7A1FE8");
    private static final UUID TYPE_UUID = SHELL_UUID;

    private final String name;
    private final UUID uuid;
    private final int capacity;

    public TurretAmmoShotgunShell(boolean isMulti) {
        this.name = isMulti ? "sgshell_lrg" : "sgshell_sng";
        this.uuid = isMulti ? PACK_UUID : SHELL_UUID;
        this.capacity = isMulti ? 16 : 1;
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
    public String getItemDesc() {
        return null;
    }

    @Override
    public int getAmmoCapacity() {
        return this.capacity;
    }

    @Override
    public Class<? extends IProjectile> getEntity() {
        return EntityProjectilePebble.class;
    }

    @Override
    public Class<? extends EntityTurret> getTurret() {
        return EntityTurretShotgun.class;
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
        return "sgshell";
    }

    @Override
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(SHELL_UUID));
    }

    public static class Single
            extends TurretAmmoShotgunShell
    {
        public Single() {
            super(false);
        }

        @Override
        public String getIcon() {
            return "shotgun_shell";
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
            super(true);
        }

        @Override
        public String getIcon() {
            return "shotgun_shell_pack";
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.SGSHELL_MTP;
        }
    }
}
