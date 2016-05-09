/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity;

import cofh.api.energy.IEnergyProvider;
import de.sanandrew.mods.turretmod.network.TileClientSync;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPotatoGenerator
        extends TileEntity
        implements ISidedInventory, TileClientSync, IEnergyProvider
{
    public static final int MAX_FLUX_STORAGE = 150_000;
    public static final int MAX_FLUX_EXTRACT = 500;

    public int fluxExtractPerTick;

    private ItemStack[] invStacks = new ItemStack[18];
    private static final int[] SLOTS_INSERT = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private float[] progress = new float[9];

    private int fluxAmount;

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return side == ForgeDirection.DOWN.ordinal() || side == ForgeDirection.UP.ordinal() ? new int[0] : SLOTS_INSERT;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack) && side != ForgeDirection.DOWN.ordinal() && side != ForgeDirection.UP.ordinal();
    }

    @Override
    public void updateEntity() {
        this.fluxExtractPerTick = Math.min(this.fluxAmount, MAX_FLUX_EXTRACT);

        int effectiveness = 0;

        if( this.fluxAmount + 65 < MAX_FLUX_STORAGE ) {
            for( int i = 0; i < 9; i++ ) {
                if( this.invStacks[i + 9] != null ) {
                    this.progress[i] -= 0.1F;
                    if( this.progress[i] <= 0.0F ) {
                        this.invStacks[i + 9] = null;
                    } else {
                        effectiveness++;
                    }
                } else if( this.invStacks[i] != null ) {
                    this.invStacks[i + 9] = this.invStacks[i].copy();
                    this.invStacks[i + 9].stackSize = 1;
                    if( --this.invStacks[i].stackSize <= 0 ) {
                        this.invStacks[i] = null;
                    }
                    this.progress[i] = 10.0F;
                }
            }

            this.fluxAmount += MathHelper.floor_double(StrictMath.pow(10.0D, effectiveness / 5.0D));
        }
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return this.invStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.invStacks[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int size) {
        if( this.invStacks[slot] != null ) {
            ItemStack itemstack;

            if( this.invStacks[slot].stackSize <= size ) {
                itemstack = this.invStacks[slot];
                this.invStacks[slot] = null;
                return itemstack;
            } else {
                itemstack = this.invStacks[slot].splitStack(size);

                if( this.invStacks[slot].stackSize == 0 ) {
                    this.invStacks[slot] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
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
    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return ItemStackUtils.isValidStack(stack) && (stack.getItem() == Items.potato || stack.getItem() == Items.poisonous_potato) && slot < 9 && this.invStacks[slot + 9] == null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.fluxAmount);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fluxAmount = buf.readInt();
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.fluxExtractPerTick, Math.min(MAX_FLUX_EXTRACT, maxExtract));

        if( !simulate ) {
            this.fluxAmount -= energyExtracted;
            this.fluxExtractPerTick -= energyExtracted;
//            this.doSync = true;
        }

        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return this.fluxAmount;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return MAX_FLUX_STORAGE;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return false;
    }
}
