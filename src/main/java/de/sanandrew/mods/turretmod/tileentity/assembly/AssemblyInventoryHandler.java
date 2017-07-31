/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.assembly;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.item.ItemAssemblyUpgrade;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
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

import javax.annotation.Nonnull;

class AssemblyInventoryHandler
        implements ISidedInventory, INBTSerializable<NBTTagCompound>
{
    private NonNullList<ItemStack> assemblyStacks = NonNullList.withSize(23, ItemStack.EMPTY);

    private static final int[] SLOTS_INSERT = new int[] {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
    private static final int[] SLOTS_EXTRACT =  new int[] {0};

    private final TileEntityTurretAssembly tile;

    AssemblyInventoryHandler(TileEntityTurretAssembly tile) {
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


    NonNullList<ItemStack> getFilterStacks() {
        if( this.hasFilterUpgrade() ) {
            return ItemAssemblyUpgrade.Filter.getFilterStacks(this.assemblyStacks.get(3));
        } else {
            return ItemAssemblyUpgrade.Filter.getEmptyInv();
        }
    }

    boolean hasAutoUpgrade() {
        return ItemStackUtils.isItem(this.assemblyStacks.get(1), ItemRegistry.assembly_upg_auto);
    }

    boolean hasSpeedUpgrade() {
        return ItemStackUtils.isItem(this.assemblyStacks.get(2), ItemRegistry.assembly_upg_speed);
    }

    boolean hasFilterUpgrade() {
        return ItemStackUtils.isItem(this.assemblyStacks.get(3), ItemRegistry.assembly_upg_filter);
    }

    boolean canFillOutput() {
        ItemStack invStack = this.assemblyStacks.get(0);
        return !ItemStackUtils.isValid(invStack) || invStack.getCount() < invStack.getMaxStackSize();
    }

    boolean canFillOutput(ItemStack stack) {
        ItemStack invStack = this.assemblyStacks.get(0);
        return this.canFillOutput() && ItemStackUtils.canStack(invStack, stack, true);
    }

    void fillOutput(ItemStack stack) {
        if( ItemStackUtils.isValid(this.assemblyStacks.get(0)) ) {
            this.assemblyStacks.get(0).grow(stack.getCount());
        } else {
            this.assemblyStacks.set(0, stack.copy());
            this.markDirty();
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? SLOTS_EXTRACT : (side == EnumFacing.UP ? new int[0] : SLOTS_INSERT);
    }

    @Override
    public boolean canInsertItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {
        return this.isItemValidForSlot(slot, stack) && side != EnumFacing.DOWN && side != EnumFacing.UP;
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, EnumFacing side) {
        return slot == 0 && side == EnumFacing.DOWN;
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
            this.tile.automate = false;
        }

        if( ItemStackUtils.isValid(this.assemblyStacks.get(slot)) ) {
            ItemStack itemstack;

            if( this.assemblyStacks.get(slot).getCount() <= size ) {
                itemstack = this.assemblyStacks.get(slot);
                this.assemblyStacks.set(slot, ItemStack.EMPTY);

                if( slot <= 4 ) {
                    this.markDirty();
                }

                return itemstack;
            } else {
                itemstack = this.assemblyStacks.get(slot).splitStack(size);

                if( this.assemblyStacks.get(slot).getCount() == 0 ) {
                    this.assemblyStacks.set(slot, ItemStack.EMPTY);
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
            this.assemblyStacks.set(slot, ItemStack.EMPTY);
            if( slot <= 4 ) {
                this.markDirty();
            }
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        if( !this.hasAutoUpgrade() ) {
            this.tile.automate = false;
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
        return null;
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
                && ( (slot > 4 && this.isStackAcceptable(stack, slot - 5)) || (slot == 1 && stack.getItem() == ItemRegistry.assembly_upg_auto)
                || (slot == 2 && stack.getItem() == ItemRegistry.assembly_upg_speed)
                || (slot == 3 && stack.getItem() == ItemRegistry.assembly_upg_filter) );
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
        for( int i = 0; i < this.assemblyStacks.size(); i++ ) {
            this.assemblyStacks.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("inventory", ItemStackUtils.writeItemStacksToTag(this.assemblyStacks, 64));
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        ItemStackUtils.readItemStacksFromTag(this.assemblyStacks, nbt.getTagList("stack", Constants.NBT.TAG_COMPOUND));
    }
}
