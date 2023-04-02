/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item.repairkits.delegate;

import de.sanandrew.mods.turretmod.api.repairkit.IRepairKit;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class SimpleRepairKit
        implements IRepairKit
{
    private final ResourceLocation id;
    private final float restoreAmount;

    public SimpleRepairKit(@Nonnull ResourceLocation id, float restoreAmount) {
        this.id = id;
        this.restoreAmount = restoreAmount;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public float getBaseRestorationAmount() {
        return this.restoreAmount;
    }
}
