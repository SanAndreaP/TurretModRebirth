/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.tileentity.electrolyte;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public final class ElectrolyteItemStackHandler
        extends ItemStackHandler
{
    private final ElectrolyteInventory parentHandler;

    public ElectrolyteItemStackHandler(ElectrolyteInventory handler) {
        super(handler.getStacksArray());
        this.parentHandler = handler;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return this.parentHandler.insertItem(slot, stack, simulate);
    }

    @Override
    public int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return this.parentHandler.getStackLimit(slot, stack);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        this.stacks = this.parentHandler.getStacksArray();
    }
}
