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
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import net.minecraft.util.ResourceLocation;

public abstract class UpgradeAmmoUsage
        implements ITurretUpgrade
{
    private final ResourceLocation itemModel;
    private final String name;

    UpgradeAmmoUsage(String name) {
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
    }
}
