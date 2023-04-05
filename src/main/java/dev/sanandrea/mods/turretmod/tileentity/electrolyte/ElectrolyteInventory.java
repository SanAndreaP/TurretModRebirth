/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.tileentity.electrolyte;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.turretmod.api.ILeveledInventory;
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
        implements ILeveledInventory
{
    @Nullable
    LazyOptional<ElectrolyteInventory> lazyOptional = LazyOptional.of(() -> this);
    final Supplier<World> levelSupplier;

    public static final int INPUT_SLOT_COUNT = 9;
    public static final int OUTPUT_SLOT_COUNT = 5;
    private static final Range<Integer> INPUT_SLOTS = Range.between(0, INPUT_SLOT_COUNT - 1);
    private static final Range<Integer> OUTPUT_SLOTS = Range.between(INPUT_SLOTS.getMaximum() + 1, INPUT_SLOTS.getMaximum() + OUTPUT_SLOT_COUNT);

    private static final int MAX_SLOTS = OUTPUT_SLOTS.getMaximum() + 1;

    private boolean checkForOutputSlotsOnInsert = false;

    public ElectrolyteInventory(Supplier<World> levelSupplier) {
        super(MAX_SLOTS);
        this.levelSupplier = levelSupplier;
    }

    public boolean isOutputFull(@Nonnull ItemStack stack) {
        ItemStack myStack = stack.copy();
        for( int i = OUTPUT_SLOTS.getMinimum(), sz = OUTPUT_SLOTS.getMaximum(); i <= sz && ItemStackUtils.isValid(myStack); i++ ) {
            myStack = this.insertItemOutput(i, myStack, true);
        }

        return ItemStackUtils.isValid(myStack);
    }

    private ItemStack insertItemOutput(int slot, @Nonnull ItemStack stack, boolean simulate) {
        this.checkForOutputSlotsOnInsert = true;
        ItemStack result = this.insertItem(slot, stack, simulate);
        this.checkForOutputSlotsOnInsert = false;
        return result;
    }

    public void addExtraction(@Nonnull ItemStack stack) {
        ItemStack myStack = stack.copy();
        for( int i = OUTPUT_SLOTS.getMinimum(), sz = OUTPUT_SLOTS.getMaximum(); i <= sz && ItemStackUtils.isValid(myStack); i++ ) {
            myStack = this.insertItemOutput(i, myStack, false);
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
    public int getContainerSize() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.stacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Nonnull
    @Override
    public ItemStack getItem(int slot) {
        return this.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int index, int count) {
        return this.extractItem(index, count, false);
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return this.extractItem(index, this.getStackInSlot(index).getCount(), false);
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        this.removeItemNoUpdate(index);
        this.insertItem(index, stack, false);
    }

    @Override
    public void setChanged() { }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) { return true; }

    @Override
    public boolean isItemValid(int index, @Nonnull ItemStack stack) {
        if( this.checkForOutputSlotsOnInsert ) {
            return OUTPUT_SLOTS.contains(index);
        } else {
            return INPUT_SLOTS.contains(index)
                   && ElectrolyteManager.INSTANCE.getFuel(this.levelSupplier.get(), stack) != null
                   && !ItemStackUtils.isValid(this.stacks.get(index));
        }
    }

    @Override
    public void clearContent() {
        this.stacks.clear();
    }

    @SuppressWarnings("unchecked")
    <T> LazyOptional<T> getLO() {
        return (LazyOptional<T>) this.lazyOptional;
    }

    @Override
    public World getLevel() {
        return levelSupplier.get();
    }
}