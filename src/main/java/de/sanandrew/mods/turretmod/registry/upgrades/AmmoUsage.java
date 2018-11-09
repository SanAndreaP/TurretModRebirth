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

public abstract class AmmoUsage
        implements ITurretUpgrade
{
    private final ResourceLocation itemModel;
    private final String name;

    AmmoUsage(String name) {
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

    public static class AmmoUseI
            extends AmmoUsage
    {
        public AmmoUseI() {
            super("use_decr_i");
        }

        @Override
        public ITurretUpgrade getDependantOn() {
            return null;
        }
    }

    public static class AmmoUseII
            extends AmmoUsage
    {
        private final ITurretUpgrade dependant;

        public AmmoUseII() {
            super("use_decr_ii");
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(Upgrades.ECONOMY_I);
        }

        @Override
        public ITurretUpgrade getDependantOn() {
            return this.dependant;
        }
    }

    public static class AmmoUseInf
            extends AmmoUsage
    {
        private final ITurretUpgrade dependant;

        public AmmoUseInf() {
            super("use_decr_inf");
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(Upgrades.ECONOMY_II);
        }

        @Override
        public ITurretUpgrade getDependantOn() {
            return this.dependant;
        }
    }
}
