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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

class InventoryAssemblyFilter
        implements IInventory
{
    public final NonNullList<ItemStack> invStacks;

    public InventoryAssemblyFilter(@Nonnull NonNullList<ItemStack> stacks) {
        this.invStacks = stacks;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < this.invStacks.size() ? this.invStacks.get(slot) : ItemStackUtils.getEmpty();
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int slot, int amount) {
        if( ItemStackUtils.isValid(this.invStacks.get(slot)) ) {
            ItemStack itemstack;

            if( this.invStacks.get(slot).getCount() <= amount ) {
                itemstack = this.invStacks.get(slot);
                this.invStacks.set(slot, ItemStackUtils.getEmpty());
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.invStacks.get(slot).splitStack(amount);

                if( this.invStacks.get(slot).getCount() == 0 ) {
                    this.invStacks.set(slot, ItemStackUtils.getEmpty());
                }

                this.markDirty();
                return itemstack;
            }
        } else {
            return ItemStackUtils.getEmpty();
        }
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int slot) {
        if( ItemStackUtils.isValid(this.invStacks.get(slot)) ) {
            ItemStack itemstack = this.invStacks.get(slot);
            this.invStacks.set(slot, ItemStackUtils.getEmpty());
            return itemstack;
        } else {
            return ItemStackUtils.getEmpty();
        }
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        this.invStacks.set(slot, stack);

        if( ItemStackUtils.isValid(stack) && stack.getCount() > this.getInventoryStackLimit() ) {
            stack.setCount(this.getInventoryStackLimit());
        }

        this.markDirty();
    }

    @Override
    public int getSizeInventory() {
        return this.invStacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.invStacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Override
    public String getName() {
        return "Assembly Filter";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) { }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.invStacks.replaceAll(stack -> ItemStackUtils.getEmpty());
    }
}
