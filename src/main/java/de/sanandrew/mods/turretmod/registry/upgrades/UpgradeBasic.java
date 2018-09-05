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

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

public class UpgradeBasic
        implements ITurretUpgrade
{

    private final String name;
    private final ITurret[] applicableTurrets;
    private final ResourceLocation itemModel;
    private final ITurretUpgrade dependantOn;

    UpgradeBasic(String name, @Nullable ITurret... applicableTurrets) {
        this.name = name;
        this.applicableTurrets = applicableTurrets;
        this.itemModel = new ResourceLocation(TmrConstants.ID, "upgrades/" + name);
        this.dependantOn = null;
    }

    UpgradeBasic(String name, ITurretUpgrade dependantOn, @Nullable ITurret... applicableTurrets) {
        this.name = name;
        this.applicableTurrets = applicableTurrets;
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

    @Nullable
    @Override
    public ITurret[] getApplicableTurrets() {
        return this.applicableTurrets;
    }

    @Override
    public ITurretUpgrade getDependantOn() {
        return this.dependantOn;
    }
}
