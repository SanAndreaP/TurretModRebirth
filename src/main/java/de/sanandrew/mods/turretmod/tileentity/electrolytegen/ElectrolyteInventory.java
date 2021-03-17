/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.electrolytegen;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public final class ElectrolyteInventory
        extends ItemStackHandler
        implements IElectrolyteInventory
{
    @Nullable
    LazyOptional<ElectrolyteInventory> lazyOptional = LazyOptional.of(() -> this);
    final Supplier<World> world;

    private static final Range<Integer> INPUT_SLOTS = Range.between(0, 8);
    private static final Range<Integer> OUTPUT_SLOTS = Range.between(INPUT_SLOTS.getMaximum() + 1, INPUT_SLOTS.getMaximum() + 5);

    private static final int MAX_SLOTS = OUTPUT_SLOTS.getMaximum() + 1;

    public ElectrolyteInventory(Supplier<World> worldSupplier) {
        super(MAX_SLOTS);
        this.world = worldSupplier;
    }

    public boolean isOutputFull(@Nonnull ItemStack stack) {
        ItemStack myStack = stack.copy();
        for( int i = OUTPUT_SLOTS.getMinimum(), sz = OUTPUT_SLOTS.getMaximum(); i <= sz && ItemStackUtils.isValid(myStack); i++ ) {
            myStack = super.insertItem(i, myStack, true);
        }

        return ItemStackUtils.isValid(myStack);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        this.validateSlotIndex(slot);
        if( this.isItemValidForSlot(slot, stack) ) {
            return super.insertItem(slot, stack, simulate);
        }

        return stack;
    }

    public void addExtraction(@Nonnull ItemStack stack) {
        ItemStack myStack = stack.copy();
        for( int i = OUTPUT_SLOTS.getMinimum(), sz = OUTPUT_SLOTS.getMaximum(); i <= sz && ItemStackUtils.isValid(myStack); i++ ) {
            myStack = super.insertItem(i, myStack, false);
        }
    }

    @Override
    public int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return INPUT_SLOTS.contains(slot) ? 1 : super.getStackLimit(slot, stack);
    }

    @Nonnull
    public ItemStack extractInsertItem(int slot, int amount, boolean simulate) {
        if( INPUT_SLOTS.contains(slot) ) {
            return super.extractItem(slot, amount, simulate);
        }

        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if( OUTPUT_SLOTS.contains(slot) ) {
            return super.extractItem(slot, amount, simulate);
        }

        return ItemStack.EMPTY;
    }

    NonNullList<ItemStack> getStacksArray() {
        return this.stacks;
    }

    @Override
    public int getSizeInventory() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.stacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return this.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return this.extractItem(index, this.getStackInSlot(index).getCount(), false);
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        this.removeStackFromSlot(index);
        this.insertItem(index, stack, false);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() { }

    @Override
    public boolean isUsableByPlayer(@Nonnull PlayerEntity player) { return true; }

    @Override
    public void openInventory(@Nonnull PlayerEntity player) { }

    @Override
    public void closeInventory(@Nonnull PlayerEntity player) { }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return INPUT_SLOTS.contains(index)
               && ElectrolyteManager.INSTANCE.getFuel(this.world.get(), stack) != null
               && !ItemStackUtils.isValid(this.stacks.get(index));
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }


    @SuppressWarnings("unchecked")
    <T> LazyOptional<T> getLO() {
        return (LazyOptional<T>) this.lazyOptional;
    }

    @Override
    public World getWorld() {
        return world.get();
    }
}
