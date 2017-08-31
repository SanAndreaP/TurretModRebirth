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

public abstract class UpgradeAmmoUsage
        implements ITurretUpgrade
{
    private final ResourceLocation itemModel;
    private final String name;

    public UpgradeAmmoUsage(String name) {
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
    public boolean isTurretApplicable(ITurret turret) {
        return true;
    }

    public static class UpgradeAmmoUseI
            extends UpgradeAmmoUsage
    {
        public UpgradeAmmoUseI() {
            super("use_decr_i");
        }

        @Override
        public ITurretUpgrade getDependantOn() {
            return null;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.UPG_ECONOMY_I;
        }
    }

    public static class UpgradeAmmoUseII
            extends UpgradeAmmoUsage
    {
        private final ITurretUpgrade dependant;

        public UpgradeAmmoUseII() {
            super("use_decr_ii");
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(Upgrades.ECONOMY_I);
        }

        @Override
        public ITurretUpgrade getDependantOn() {
            return this.dependant;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.UPG_ECONOMY_II;
        }
    }

    public static class UpgradeAmmoUseInf
            extends UpgradeAmmoUsage
    {
        private final ITurretUpgrade dependant;

        public UpgradeAmmoUseInf() {
            super("use_decr_inf");
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(Upgrades.ECONOMY_II);
        }

        @Override
        public ITurretUpgrade getDependantOn() {
            return this.dependant;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.UPG_ECONOMY_INF;
        }
    }
}
