/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryAssemblyFilter
        implements IInventory
{
    public ItemStack[] invStacks;

    public InventoryAssemblyFilter(ItemStack[] stacks) {
        this.invStacks = stacks;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < this.invStacks.length ? this.invStacks[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if( this.invStacks[slot] != null ) {
            ItemStack itemstack;

            if( this.invStacks[slot].stackSize <= amount ) {
                itemstack = this.invStacks[slot];
                this.invStacks[slot] = null;
                this.markDirty();
                return itemstack;
            } else {
                itemstack = this.invStacks[slot].splitStack(amount);

                if( this.invStacks[slot].stackSize == 0 ) {
                    this.invStacks[slot] = null;
                }

                this.markDirty();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        if( this.invStacks[slot] != null ) {
            ItemStack itemstack = this.invStacks[slot];
            this.invStacks[slot] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.invStacks[slot] = stack;

        if( stack != null && stack.stackSize > this.getInventoryStackLimit() ) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    @Override
    public int getSizeInventory() {
        return this.invStacks.length;
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
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
        for( int i = 0; i < this.invStacks.length; i++ ) {
            this.invStacks[i] = null;
        }
    }
}
