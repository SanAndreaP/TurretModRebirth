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
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class SimpleUpgrade
        implements IUpgrade
{
    private final ResourceLocation id;
    private final ResourceLocation bookEntryId;
    private final ITurret[] applicableTurrets;
    private final IUpgrade dependantOn;

    SimpleUpgrade(String name, @Nullable ITurret... applicableTurrets) {
        this(name, null, null, applicableTurrets);
    }

    SimpleUpgrade(String name, ResourceLocation bookEntryId, @Nullable ITurret... applicableTurrets) {
        this(name, null, bookEntryId, applicableTurrets);
    }

    SimpleUpgrade(String name, IUpgrade dependantOn, @Nullable ITurret... applicableTurrets) {
        this(name, dependantOn, new ResourceLocation(TmrConstants.ID, "upgrade_" + name), applicableTurrets);
    }

    SimpleUpgrade(String name, IUpgrade dependantOn, ResourceLocation bookEntryId, @Nullable ITurret... applicableTurrets) {
        this.id = new ResourceLocation(TmrConstants.ID, "upgrade." + name);
        this.bookEntryId = bookEntryId;
        this.applicableTurrets = applicableTurrets;
        this.dependantOn = dependantOn;

    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public ResourceLocation getBookEntryId() {
        return this.bookEntryId;
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
