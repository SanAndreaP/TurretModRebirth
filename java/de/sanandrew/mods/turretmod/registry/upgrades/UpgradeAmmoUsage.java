/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.entity.turret.ConsumptionListener;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class UpgradeAmmoUsage
        implements TurretUpgrade
{
    private final ResourceLocation itemModel;
    private final String name;
    private final ConsumptionListener listener;

    public UpgradeAmmoUsage(String name, ConsumptionListener listener) {
        this.name = name;
        this.itemModel = new ResourceLocation(TurretModRebirth.ID, "upgrades/" + name);
        this.listener = listener;
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
    public void onApply(EntityTurret turret) {
        turret.getTargetProcessor().addConsumptionListener(this.listener);
    }

    @Override
    public void onRemove(EntityTurret turret) {
        turret.getTargetProcessor().removeConsumptionListener(this.listener);
    }

    @Override
    public void onLoad(EntityTurret turret, NBTTagCompound nbt) {
        turret.getTargetProcessor().addConsumptionListener(this.listener);
    }

    public static class UpgradeAmmoUseI
            extends UpgradeAmmoUsage
    {
        public UpgradeAmmoUseI() {
            super("use_decr_i", (canConsumePrev, turret) -> TmrUtils.RNG.nextFloat() >= 0.1F);
        }

        @Override
        public TurretUpgrade getDependantOn() {
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
        private final TurretUpgrade dependant;

        public UpgradeAmmoUseII() {
            super("use_decr_ii", (canConsumePrev, turret) -> TmrUtils.RNG.nextFloat() >= 0.35F);
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(UpgradeRegistry.UPG_ECONOMY_I);
        }

        @Override
        public TurretUpgrade getDependantOn() {
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
        private final TurretUpgrade dependant;

        public UpgradeAmmoUseInf() {
            super("use_decr_inf", (canConsumePrev, turret) -> turret.getTargetProcessor().getAmmoCount() != turret.getTargetProcessor().getMaxAmmoCapacity() && canConsumePrev);
            this.dependant = UpgradeRegistry.INSTANCE.getUpgrade(UpgradeRegistry.UPG_ECONOMY_II);
        }

        @Override
        public TurretUpgrade getDependantOn() {
            return this.dependant;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.UPG_ECONOMY_INF;
        }
    }
}
