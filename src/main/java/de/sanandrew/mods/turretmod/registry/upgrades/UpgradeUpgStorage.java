/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class UpgradeUpgStorage
        implements ITurretUpgrade
{
    private final ResourceLocation itemModel;
    private final String name;

    public UpgradeUpgStorage(String name) {
        this.name = name;
        this.itemModel = new ResourceLocation(TmrConstants.ID, "upgrades/" + name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }

    @Override
    public boolean isTurretApplicable(ITurret turretCls) {
        return true;
    }

    public static class UpgradeStorageMK1
            extends UpgradeUpgStorage
    {

        public UpgradeStorageMK1() {
            super("upg_storage_i");
        }

        @Override
        public ITurretUpgrade getDependantOn() {
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
        private final ITurretUpgrade dependant;

        public UpgradeStorageMK2() {
            super("upg_storage_ii");
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(UpgradeRegistry.UPG_STORAGE_I);
        }

        @Override
        public ITurretUpgrade getDependantOn() {
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
        private final ITurretUpgrade dependant;

        public UpgradeStorageMK3() {
            super("upg_storage_iii");
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(UpgradeRegistry.UPG_STORAGE_II);
        }

        @Override
        public ITurretUpgrade getDependantOn() {
            return this.dependant;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.UPG_STORAGE_3;
        }
    }
}
