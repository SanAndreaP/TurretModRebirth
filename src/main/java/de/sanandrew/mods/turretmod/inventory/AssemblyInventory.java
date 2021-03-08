/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyInventory;
import de.sanandrew.mods.turretmod.item.ItemAssemblyUpgrade;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.IntStream;

@SuppressWarnings("NullableProblems")
public class AssemblyInventory
        implements IAssemblyInventory, INBTSerializable<CompoundNBT>
{
    public static final int RESOURCE_SLOTS = 18;
    public static final int RESOURCE_SLOT_FIRST;

    public static final int SLOT_OUTPUT = 0;
    public static final int SLOT_OUTPUT_CARTRIDGE = 1;
    public static final int SLOT_UPGRADE_AUTO = 2;
    public static final int SLOT_UPGRADE_SPEED = 3;
    public static final int SLOT_UPGRADE_FILTER = 4;
    public static final int SLOT_UPGRADE_REDSTONE = 5;

    private final NonNullList<ItemStack> stacks = NonNullList.withSize(24, ItemStack.EMPTY);

    private static final int[] SLOTS_INSERT = new int[RESOURCE_SLOTS];
    private static final int[] SLOTS_EXTRACT =  new int[] {SLOT_OUTPUT, SLOT_OUTPUT_CARTRIDGE};

    static {
        for( int i = 0, slotId = 6; i < RESOURCE_SLOTS; i++ ) SLOTS_INSERT[i] = slotId++;
        RESOURCE_SLOT_FIRST = SLOTS_INSERT[0];
    }

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
            return ItemAssemblyUpgrade.Filter.getFilterStacks(this.stacks.get(SLOT_UPGRADE_FILTER));
        } else {
            return ItemAssemblyUpgrade.Filter.getEmptyInv();
        }
    }

    public boolean hasAutoUpgrade() {
        return ItemStackUtils.isItem(this.stacks.get(SLOT_UPGRADE_AUTO), ItemRegistry.ASSEMBLY_UPG_AUTO);
    }

    public boolean hasSpeedUpgrade() {
        return ItemStackUtils.isItem(this.stacks.get(SLOT_UPGRADE_SPEED), ItemRegistry.ASSEMBLY_UPG_SPEED);
    }

    public boolean hasFilterUpgrade() {
        return ItemStackUtils.isItem(this.stacks.get(SLOT_UPGRADE_FILTER), ItemRegistry.ASSEMBLY_UPG_FILTER);
    }

    public boolean hasRedstoneUpgrade() {
        return ItemStackUtils.isItem(this.stacks.get(SLOT_UPGRADE_REDSTONE), ItemRegistry.ASSEMBLY_UPG_REDSTONE);
    }

    public boolean canFillOutput(ItemStack stack) {
        StackContainerSlotData ihm = getFirstItemContainer();
        if( ihm != null ) {
            if( fillItemContainer(ihm.handler, stack, true) ) {
//                this.tryPushOutputToItemContainer(ihm.handler);
                return true;
            }

//            this.pushItemContainerToOutput(ihm.slot);
        }

        ItemStack invStack = this.stacks.get(SLOT_OUTPUT);
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

        if( ItemStackUtils.isValid(this.stacks.get(SLOT_OUTPUT)) ) {
            this.stacks.get(SLOT_OUTPUT).grow(stack.getCount());
        } else {
            this.stacks.set(SLOT_OUTPUT, stack.copy());
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
        if( fillItemContainer(handler, this.stacks.get(SLOT_OUTPUT), true) ) {
            fillItemContainer(handler, this.stacks.get(SLOT_OUTPUT), false);
            this.stacks.set(SLOT_OUTPUT, ItemStack.EMPTY);
            this.markDirty();
        }
    }

    private void pushItemContainerToOutput(int containerSlot) {
        if( !ItemStackUtils.isValid(this.stacks.get(SLOT_OUTPUT_CARTRIDGE)) ) {
            this.stacks.set(SLOT_OUTPUT_CARTRIDGE, this.stacks.get(containerSlot).copy());
            this.stacks.set(containerSlot, ItemStack.EMPTY);
            this.markDirty();
        }
    }

    private StackContainerSlotData getFirstItemContainer() {
        for( int slot : SLOTS_INSERT ) {
            ItemStack stack = this.stacks.get(slot);
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
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.stacks.isEmpty() || this.stacks.stream().noneMatch(ItemStackUtils::isValid);
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return this.stacks.get(slot);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int slot, int size) {
        ItemStack stack = this.getStackInSlot(slot);
        if( ItemStackUtils.isValid(stack) ) {
            ItemStack newStack;

            if( stack.getCount() <= size ) {
                newStack = stack;

                this.stacks.set(slot, ItemStack.EMPTY);
            } else {
                newStack = stack.splitStack(size);

                if( stack.getCount() == 0 ) {
                    this.stacks.set(slot, ItemStack.EMPTY);
                }
            }

            this.updateTile(slot);

            return newStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int slot) {
        if( ItemStackUtils.isValid(this.stacks.get(slot)) ) {
            ItemStack itemstack = this.stacks.get(slot);
            this.stacks.set(slot, ItemStack.EMPTY);

            this.updateTile(slot);

            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        this.stacks.set(slot, stack);

        if( ItemStackUtils.isValid(stack) && stack.getCount() > this.getInventoryStackLimit() ) {
            stack.setCount(this.getInventoryStackLimit());
        }

        this.updateTile(slot);
    }

    private void updateTile(int slot) {
        if( !this.tile.getWorld().isRemote && slot < RESOURCE_SLOT_FIRST ) {
            if( slot == SLOT_UPGRADE_AUTO && !this.hasAutoUpgrade() ) {
                this.tile.setAutomated(false);
            }

            if( slot == SLOT_UPGRADE_REDSTONE ) {
                IBlockState blockState = this.tile.getWorld().getBlockState(this.tile.getPos());
                this.tile.getWorld().notifyBlockUpdate(this.tile.getPos(), blockState, blockState, 3);
            }

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
        if( slot == SLOT_OUTPUT || slot == SLOT_OUTPUT_CARTRIDGE || !ItemStackUtils.isValid(stack) ) {
            return false;
        }
        if( slot == SLOT_UPGRADE_AUTO && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_AUTO ) {
            return true;
        }
        if( slot == SLOT_UPGRADE_SPEED && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_SPEED ) {
            return true;
        }
        if( slot == SLOT_UPGRADE_FILTER && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_FILTER ) {
            return true;
        }
        if( slot == SLOT_UPGRADE_REDSTONE && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_REDSTONE ) {
            return true;
        }

        return Arrays.binarySearch(SLOTS_INSERT, slot) >= 0 && this.isStackAcceptable(stack, slot - RESOURCE_SLOT_FIRST);
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
        this.stacks.clear();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("inventory", ItemStackUtils.writeItemStacksToTag(this.stacks, 64));
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagList inventory = nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
        if( inventory.tagCount() == 23 ) {
            NonNullList<ItemStack> oldStacks = NonNullList.withSize(23, ItemStack.EMPTY);
            ItemStackUtils.readItemStacksFromTag(oldStacks, inventory);

            this.stacks.set(SLOT_OUTPUT, oldStacks.get(0));
            this.stacks.set(SLOT_UPGRADE_AUTO, oldStacks.get(1));
            this.stacks.set(SLOT_UPGRADE_SPEED, oldStacks.get(2));
            this.stacks.set(SLOT_UPGRADE_FILTER, oldStacks.get(3));
            this.stacks.set(SLOT_OUTPUT_CARTRIDGE, oldStacks.get(4));
            for(int i = 0, j = 5; i < SLOTS_INSERT.length; i++) {
                this.stacks.set(SLOTS_INSERT[i], oldStacks.get(j++));
            }
        } else {
            ItemStackUtils.readItemStacksFromTag(this.stacks, inventory);
        }
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
