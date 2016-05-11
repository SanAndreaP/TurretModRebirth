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
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncTileEntity;
import de.sanandrew.mods.turretmod.network.TileClientSync;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

public class TileEntityPotatoGenerator
        extends TileEntity
        implements ISidedInventory, TileClientSync, IEnergyProvider
{
    public static final int MAX_FLUX_STORAGE = 150_000;
    public static final int MAX_FLUX_EXTRACT = 750;

    public int fluxExtractPerTick;

    private ItemStack[] invStacks = new ItemStack[23];
    private static final int[] SLOTS_INSERT = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] SLOTS_PROCESSING = new int[] {9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int[] SLOTS_EXTRACT = new int[] {18, 19, 20, 21, 22};
    private int[] progress = new int[9];

    private int fluxAmount;
    private int prevFluxAmount;
    private boolean doSync;

    private static final Map<Item, Triplet<Float, ItemStack, ItemStack>> FUELS = new HashMap<>(3);

    public static void initializeRecipes() {
        FUELS.put(Items.potato, Triplet.with(1.0F, new ItemStack(Items.sugar, 1), new ItemStack(Items.baked_potato, 1)));
        FUELS.put(Items.poisonous_potato, Triplet.with(1.2F, new ItemStack(Items.sugar, 1), new ItemStack(Items.gunpowder)));
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return side == ForgeDirection.DOWN.ordinal() ? SLOTS_EXTRACT : side == ForgeDirection.UP.ordinal() ? new int[0] : SLOTS_INSERT;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack) && side != ForgeDirection.DOWN.ordinal() && side != ForgeDirection.UP.ordinal();
    }

    @Override
    public void updateEntity() {
        this.fluxExtractPerTick = Math.min(this.fluxAmount, MAX_FLUX_EXTRACT);

        if( this.fluxAmount + 65 < MAX_FLUX_STORAGE ) {
            float effectiveness = 0.0F;

            for( int i = 0; i < SLOTS_PROCESSING.length; i++ ) {
                if( this.invStacks[SLOTS_PROCESSING[i]] != null ) {
                    if( --this.progress[i] < 1 ) {
                        this.invStacks[SLOTS_PROCESSING[i]] = null;
                        this.markDirty();
                    } else {
                        effectiveness += FUELS.get(this.invStacks[SLOTS_PROCESSING[i]].getItem()).getValue0();
                    }
                    this.doSync = true;
                } else if( this.invStacks[SLOTS_INSERT[i]] != null ) {
                    this.invStacks[SLOTS_PROCESSING[i]] = this.invStacks[SLOTS_INSERT[i]].copy();
                    this.invStacks[SLOTS_PROCESSING[i]].stackSize = 1;
                    if( --this.invStacks[SLOTS_INSERT[i]].stackSize < 1 ) {
                        this.invStacks[SLOTS_INSERT[i]] = null;
                    }
                    this.progress[i] = 200;
                    this.markDirty();
                    this.doSync = true;
                }
            }

            if( effectiveness > 0.1F ) {
                this.fluxAmount += StrictMath.round(StrictMath.pow(1.6D, effectiveness) / (68.0D + (127433.0D / 177119.0D)) * 80.0D);
            }
        }

        if( this.prevFluxAmount != this.fluxAmount ) {
            this.doSync = true;
        }

        if( this.doSync ) {
            PacketRegistry.sendToAllAround(new PacketSyncTileEntity(this), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 64.0D);
        }

        this.prevFluxAmount = this.fluxAmount;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return side == ForgeDirection.DOWN.ordinal() && ArrayUtils.contains(SLOTS_EXTRACT, slot);
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
        return ItemStackUtils.isValidStack(stack) && (stack.getItem() == Items.potato || stack.getItem() == Items.poisonous_potato)
               && ArrayUtils.contains(SLOTS_INSERT, slot) && this.invStacks[SLOTS_PROCESSING[ArrayUtils.indexOf(SLOTS_INSERT, slot)]] == null;
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
        return from != ForgeDirection.UP;
    }
}
