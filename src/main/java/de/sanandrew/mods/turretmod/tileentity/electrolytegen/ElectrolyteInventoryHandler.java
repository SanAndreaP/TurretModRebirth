/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.electrolytegen;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.registry.electrolytegen.ElectrolyteRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

final class ElectrolyteInventoryHandler
        extends ItemStackHandler
{
    private final TileEntityElectrolyteGenerator tile;

    ElectrolyteInventoryHandler(TileEntityElectrolyteGenerator tile) {
        super(14);
        this.tile = tile;
    }

    boolean isOutputFull(@Nonnull ItemStack stack) {
        ItemStack myStack = stack.copy();
        for( int i = 9; i < 14 && ItemStackUtils.isValid(myStack); i++ ) {
            myStack = super.insertItem(i, myStack, true);
        }

        return ItemStackUtils.isValid(myStack);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        this.validateSlotIndex(slot);
        if( slot < 9 && ElectrolyteRegistry.getFuel(stack).isValid() && !ItemStackUtils.isValid(this.stacks.get(slot)) ) {
            return super.insertItem(slot, stack, simulate);
        }

        return stack;
    }

    void addExtraction(@Nonnull ItemStack stack) {
        ItemStack myStack = stack.copy();
        for( int i = 9; i < 14 && ItemStackUtils.isValid(myStack); i++ ) {
            myStack = super.insertItem(i, myStack, false);
        }
    }

    @Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return slot < 9 ? 1 : super.getStackLimit(slot, stack);
    }

    @Nonnull
    ItemStack extractInsertItem(int slot, boolean simulate) {
        if( slot < 9 ) {
            return super.extractItem(slot, 1, simulate);
        }

        return ItemStackUtils.getEmpty();
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if( slot > 8 ) {
            return super.extractItem(slot, amount, simulate);
        }

        return ItemStackUtils.getEmpty();
    }

    NonNullList<ItemStack> getStacksArray() {
        return this.stacks;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        this.tile.containerItemHandler.onLoad();
    }
}
