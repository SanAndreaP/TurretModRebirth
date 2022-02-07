/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item.upgrades;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleUpgrade
        implements IUpgrade
{
    private final ResourceLocation id;
    private final ITurret[] applicableTurrets;
    private final IUpgrade dependantOn;

    SimpleUpgrade(String name, @Nullable ITurret... applicableTurrets) {
        this(name, null, applicableTurrets);
    }

    SimpleUpgrade(String name, IUpgrade dependantOn, @Nullable ITurret... applicableTurrets) {
        this.id = new ResourceLocation(TmrConstants.ID, name + "_upgrade");
        this.applicableTurrets = applicableTurrets;
        this.dependantOn = dependantOn;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nullable
    @Override
    public ITurret[] getApplicableTurrets() {
        return this.applicableTurrets;
    }

    @Override
    public IUpgrade getDependantOn() {
        return this.dependantOn;
    }
}
