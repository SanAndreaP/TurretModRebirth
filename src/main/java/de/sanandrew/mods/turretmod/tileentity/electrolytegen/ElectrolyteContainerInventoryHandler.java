/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.electrolytegen;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

final class ElectrolyteContainerInventoryHandler
        extends ItemStackHandler
{
    private final ElectrolyteInventoryHandler parentHandler;

    public ElectrolyteContainerInventoryHandler(ElectrolyteInventoryHandler handler) {
        super(handler.getStacksArray());
        this.parentHandler = handler;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return this.parentHandler.insertItem(slot, stack, simulate);
    }

    @Override
    protected int getStackLimit(int slot, ItemStack stack) {
        return this.parentHandler.getStackLimit(slot, stack);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.stacks = this.parentHandler.getStacksArray();
    }
}
