/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.Collections;

public class AssemblyFilterInventory
        implements IInventory, INameable
{
    public final NonNullList<ItemStack> invStacks;

    public AssemblyFilterInventory(@Nonnull NonNullList<ItemStack> stacks) {
        this.invStacks = stacks;
    }

    @Override
    @Nonnull
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.invStacks.size() ? this.invStacks.get(slot) : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int slot, int amount) {
        if( ItemStackUtils.isValid(this.invStacks.get(slot)) ) {
            ItemStack itemstack;

            if( this.invStacks.get(slot).getCount() <= amount ) {
                itemstack = this.invStacks.get(slot);
                this.invStacks.set(slot, ItemStack.EMPTY);
                this.setChanged();
                return itemstack;
            } else {
                itemstack = this.invStacks.get(slot).split(amount);

                if( this.invStacks.get(slot).getCount() == 0 ) {
                    this.invStacks.set(slot, ItemStack.EMPTY);
                }

                this.setChanged();
                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int slot) {
        if( ItemStackUtils.isValid(this.invStacks.get(slot)) ) {
            ItemStack itemstack = this.invStacks.get(slot);
            this.invStacks.set(slot, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        this.invStacks.set(slot, stack);

        if( ItemStackUtils.isValid(stack) && stack.getCount() > this.getMaxStackSize() ) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return this.invStacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.invStacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        return new StringTextComponent("Assembly Filter");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void clearContent() {
        Collections.fill(this.invStacks, ItemStack.EMPTY);
    }
}
