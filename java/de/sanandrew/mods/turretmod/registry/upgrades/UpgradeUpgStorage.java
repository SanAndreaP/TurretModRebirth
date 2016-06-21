/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class UpgradeUpgStorage
        implements TurretUpgrade
{
    private final ResourceLocation itemModel;
    private final String name;

    public UpgradeUpgStorage(String name) {
        this.name = name;
        this.itemModel = new ResourceLocation(TurretModRebirth.ID, "upgrades/" + name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getModId() {
        return TurretModRebirth.ID;
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    @Override
    public boolean isTurretApplicable(Class<? extends EntityTurret> turretCls) {
        return true;
    }

    @Override
    public void onApply(EntityTurret turret) {}

    @Override
    public void onRemove(EntityTurret turret) {}

    @Override
    public void onLoad(EntityTurret turret, NBTTagCompound nbt) {}

    @Override
    public void onSave(EntityTurret turret, NBTTagCompound nbt) {}

    public static class UpgradeStorageMK1
            extends UpgradeUpgStorage
    {

        public UpgradeStorageMK1() {
            super("upg_storage_i");
        }

        @Override
        public TurretUpgrade getDependantOn() {
            return null;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.UPG_STORAGE_1;
        }
    }

    public static class UpgradeStorageMK2
            extends UpgradeUpgStorage
    {
        private final TurretUpgrade dependant;

        public UpgradeStorageMK2() {
            super("upg_storage_ii");
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(UpgradeRegistry.UPG_STORAGE_I);
        }

        @Override
        public TurretUpgrade getDependantOn() {
            return this.dependant;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.UPG_STORAGE_2;
        }
    }

    public static class UpgradeStorageMK3
            extends UpgradeUpgStorage
    {
        private final TurretUpgrade dependant;

        public UpgradeStorageMK3() {
            super("upg_storage_iii");
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(UpgradeRegistry.UPG_STORAGE_II);
        }

        @Override
        public TurretUpgrade getDependantOn() {
            return this.dependant;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.UPG_STORAGE_3;
        }
    }
}
