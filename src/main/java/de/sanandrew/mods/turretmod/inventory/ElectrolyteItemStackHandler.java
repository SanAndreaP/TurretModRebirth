/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.turretmod.inventory.ElectrolyteInventory;
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
