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
import net.minecraft.util.ResourceLocation;

import java.util.Objects;
import java.util.function.Function;

public class UpgradeBasic
        implements ITurretUpgrade
{

    private final String name;
    private final Function<ITurret, Boolean> isTurretApplicable;
    private final ResourceLocation itemModel;
    private final ITurretUpgrade dependantOn;

    public UpgradeBasic(String name, Function<ITurret, Boolean> isTurretApplicable) {
        this.name = name;
        this.isTurretApplicable = Objects.requireNonNull(isTurretApplicable);
        this.itemModel = new ResourceLocation(TmrConstants.ID, "upgrades/" + name);
        this.dependantOn = null;
    }

    public UpgradeBasic(String name, Function<ITurret, Boolean> isTurretApplicable, ITurretUpgrade dependantOn) {
        this.name = name;
        this.isTurretApplicable = Objects.requireNonNull(isTurretApplicable);
        this.itemModel = new ResourceLocation(TmrConstants.ID, "upgrades/" + name);
        this.dependantOn = dependantOn;
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
        return this.isTurretApplicable.apply(turret);
    }

    @Override
    public ITurretUpgrade getDependantOn() {
        return this.dependantOn;
    }
}
