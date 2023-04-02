/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.upgrades.delegate;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class Economy
        implements IUpgrade
{
    private final ResourceLocation id;

    Economy(String name) {
        this.id = new ResourceLocation(TmrConstants.ID, name + "_upgrade");
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public static class MK1
            extends Economy
    {
        public MK1() {
            super("economy_1");
        }
    }

    public static class MK2
            extends Economy
    {
        public MK2() {
            super("economy_2");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.ECONOMY_I;
        }
    }

    public static class MKInf
            extends Economy
    {
        public MKInf() {
            super("infinite_economy");
        }

        @Override
        public IUpgrade getDependantOn() {
            return Upgrades.ECONOMY_II;
        }
    }
}
