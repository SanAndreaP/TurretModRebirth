/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item.upgrades.delegate;

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

    public SimpleUpgrade(String name, @Nullable ITurret... applicableTurrets) {
        this(name, null, applicableTurrets);
    }

    public SimpleUpgrade(String name, IUpgrade dependantOn, @Nullable ITurret... applicableTurrets) {
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
