/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.item.ItemAssemblyUpgrade;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class AssemblyInventory
        implements ISidedInventory, INBTSerializable<NBTTagCompound>
{
    public static final int RESOURCE_SLOTS = 18;

    private final NonNullList<ItemStack> assemblyStacks = NonNullList.withSize(23, ItemStackUtils.getEmpty());

    private static final int[] SLOTS_INSERT = new int[] {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
    private static final int[] SLOTS_EXTRACT =  new int[] {0, 4};

    private final TileEntityTurretAssembly tile;

    public AssemblyInventory(TileEntityTurretAssembly tile) {
        this.tile = tile;
    }

    private boolean isStackAcceptable(@Nonnull ItemStack stack, int insrtSlot) {
        if( this.hasFilterUpgrade() ) {
            NonNullList<ItemStack> filter = this.getFilterStacks();
            if( ItemStackUtils.isStackInList(stack, filter) ) {
                return ItemStackUtils.areEqual(stack, filter.get(insrtSlot));
            } else {
                return !ItemStackUtils.isValid(filter.get(insrtSlot));
            }
        }

        return true;
    }

    public NonNullList<ItemStack> getFilterStacks() {
        if( this.hasFilterUpgrade() ) {
            return ItemAssemblyUpgrade.Filter.getFilterStacks(this.assemblyStacks.get(3));
        } else {
            return ItemAssemblyUpgrade.Filter.getEmptyInv();
        }
    }

    public boolean hasAutoUpgrade() {
        return ItemStackUtils.isItem(this.assemblyStacks.get(1), ItemRegistry.ASSEMBLY_UPG_AUTO);
    }

    public boolean hasSpeedUpgrade() {
        return ItemStackUtils.isItem(this.assemblyStacks.get(2), ItemRegistry.ASSEMBLY_UPG_SPEED);
    }

    public boolean hasFilterUpgrade() {
        return ItemStackUtils.isItem(this.assemblyStacks.get(3), ItemRegistry.ASSEMBLY_UPG_FILTER);
    }

    public boolean canFillOutput(ItemStack stack) {
        StackContainerSlotData ihm = getFirstItemContainer();
        if( ihm != null ) {
            if( fillItemContainer(ihm.handler, stack, true) ) {
                this.tryPushOutputToItemContainer(ihm.handler);
                return true;
            }

            this.pushItemContainerToOutput(ihm.slot);
        }

        ItemStack invStack = this.assemblyStacks.get(0);
        return !ItemStackUtils.isValid(invStack) || invStack.getCount() < invStack.getMaxStackSize() && ItemStackUtils.canStack(invStack, stack, true);
    }

    public void fillOutput(ItemStack stack) {
        StackContainerSlotData ihm = getFirstItemContainer();
        if( ihm != null ) {
            if( fillItemContainer(ihm.handler, stack, false) ) {
                this.tryPushOutputToItemContainer(ihm.handler);
                return;
            }
            this.pushItemContainerToOutput(ihm.slot);
        }

        if( ItemStackUtils.isValid(this.assemblyStacks.get(0)) ) {
            this.assemblyStacks.get(0).grow(stack.getCount());
        } else {
            this.assemblyStacks.set(0, stack.copy());
            this.markDirty();
        }
    }

    private static boolean fillItemContainer(IItemHandler handler, ItemStack stack, boolean simulate) {
        for( int i = 0, max = handler.getSlots(); i < max; i++ ) {
            stack = handler.insertItem(i, stack, simulate);
            if( !ItemStackUtils.isValid(stack) ) {
                return true;
            }
        }

        return false;
    }

    private void tryPushOutputToItemContainer(IItemHandler handler) {
        if( fillItemContainer(handler, this.assemblyStacks.get(0), true) ) {
            fillItemContainer(handler, this.assemblyStacks.get(0), false);
            this.assemblyStacks.set(0, ItemStack.EMPTY);
            this.markDirty();
        }
    }

    private void pushItemContainerToOutput(int containerSlot) {
        if( !ItemStackUtils.isValid(this.assemblyStacks.get(4)) ) {
            this.assemblyStacks.set(4, this.assemblyStacks.get(containerSlot).copy());
            this.assemblyStacks.set(containerSlot, ItemStack.EMPTY);
            this.markDirty();
        }
    }

    private StackContainerSlotData getFirstItemContainer() {
        for( int slot : SLOTS_INSERT ) {
            ItemStack stack = this.assemblyStacks.get(slot);
            if( ItemStackUtils.isValid(stack) ) {
                IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if( itemHandler != null ) {
                    return new StackContainerSlotData(itemHandler, slot);
                }
            }
        }

        return null;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? SLOTS_EXTRACT : (side == EnumFacing.UP ? new int[0] : SLOTS_INSERT);
    }

    @Override
    public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {
        return IntStream.of(SLOTS_INSERT).anyMatch(s -> s == slot) && this.isItemValidForSlot(slot, stack) && side != EnumFacing.DOWN && side != EnumFacing.UP;
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {
        return IntStream.of(SLOTS_EXTRACT).anyMatch(s -> s == slot) && side == EnumFacing.DOWN;
    }

    @Override
    public int getSizeInventory() {
        return this.assemblyStacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.assemblyStacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return this.assemblyStacks.get(slot);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int slot, int size) {
        if( !this.hasAutoUpgrade() ) {
            this.tile.setAutomated(false);
        }

        ItemStack stack = this.getStackInSlot(slot);
        if( ItemStackUtils.isValid(stack) ) {
            ItemStack itemstack;

            if( stack.getCount() <= size ) {
                itemstack = stack;
                this.assemblyStacks.set(slot, ItemStackUtils.getEmpty());

                if( slot <= 4 ) {
                    this.markDirty();
                }

                return itemstack;
            } else {
                itemstack = stack.splitStack(size);

                if( stack.getCount() == 0 ) {
                    this.assemblyStacks.set(slot, ItemStackUtils.getEmpty());
                }

                if( slot <= 4 ) {
                    this.markDirty();
                }

                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int slot) {
        if( ItemStackUtils.isValid(this.assemblyStacks.get(slot)) ) {
            ItemStack itemstack = this.assemblyStacks.get(slot);
            this.assemblyStacks.set(slot, ItemStackUtils.getEmpty());
            if( slot <= 4 ) {
                this.markDirty();
            }
            return itemstack;
        } else {
            return ItemStackUtils.getEmpty();
        }
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        if( !this.hasAutoUpgrade() ) {
            this.tile.setAutomated(false);
        }

        this.assemblyStacks.set(slot, stack);

        if( ItemStackUtils.isValid(stack) && stack.getCount() > this.getInventoryStackLimit() ) {
            stack.setCount(this.getInventoryStackLimit());
        }

        if( slot <= 4 ) {
            this.markDirty();
        }
    }

    @Override
    public String getName() {
        return this.tile.getCustomName();
    }

    @Override
    public boolean hasCustomName() {
        return this.tile.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.tile.getDisplayName();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        this.tile.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        BlockPos tilePos = this.tile.getPos();
        return this.tile.getWorld().getTileEntity(tilePos) == this.tile && player.getDistanceSq(tilePos.getX() + 0.5D, tilePos.getY() + 0.5D, tilePos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return slot != 0 && ItemStackUtils.isValid(stack)
                && ( (slot > 4 && this.isStackAcceptable(stack, slot - 5)) || (slot == 1 && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_AUTO)
                || (slot == 2 && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_SPEED)
                || (slot == 3 && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_FILTER) );
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
        this.assemblyStacks.clear();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("inventory", ItemStackUtils.writeItemStacksToTag(this.assemblyStacks, 64));
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        ItemStackUtils.readItemStacksFromTag(this.assemblyStacks, nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND));
    }

    private static final class StackContainerSlotData
    {
        IItemHandler handler;
        int slot;

        StackContainerSlotData(IItemHandler itemHandler, int slot) {
            this.handler = itemHandler;
            this.slot = slot;
        }
    }
}
