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
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerTurretAssembly
        extends Container
{
    private TileEntityTurretAssembly tile;

    public ContainerTurretAssembly(IInventory playerInv, TileEntityTurretAssembly assembly) {
        this.tile = assembly;

        this.addSlotToContainer(new SlotOutput(assembly, 0, 172, 10));
        this.addSlotToContainer(new SlotAutoUpgrade(assembly, 1, 14, 100));
        this.addSlotToContainer(new SlotSpeedUpgrade(assembly, 2, 14, 118));
        this.addSlotToContainer(new SlotFilterUpgrade(assembly, 3, 202, 100));
        this.addSlotToContainer(new SlotOutput(assembly, 4, 202, 118));

        for( int i = 0; i < 2; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new SlotIngredients(assembly, j + i * 9 + 5, 36 + j * 18, 100 + i * 18));
            }
        }

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 36 + j * 18, 140 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new Slot(playerInv, i, 36 + i * 18, 198));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tile.isUsableByPlayer(player);
    }

    private boolean transferUpgrade(Item desiredItm, @Nonnull ItemStack origStack, @Nonnull ItemStack slotStack, int upgSlot) {
        int origStackSize = desiredItm.getItemStackLimit(origStack);
        desiredItm.setMaxStackSize(1);
        slotStack.setCount(1);
        if( !super.mergeItemStack(slotStack, 1 + upgSlot, 2 + upgSlot, false) ) {
            slotStack.setCount(origStack.getCount());
            desiredItm.setMaxStackSize(origStackSize);
            return true;
        } else {
            slotStack.setCount(origStack.getCount() - 1);
        }
        desiredItm.setMaxStackSize(origStackSize);
        return false;
    }

    @Override
    protected boolean mergeItemStack(@Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        boolean slotChanged = false;
        int start = beginSlot;

        if( reverse ) {
            start = endSlot - 1;
        }

        Slot slot;
        ItemStack slotStack;

        if( stack.isStackable() ) {
            while( stack.getCount() > 0 && (!reverse && start < endSlot || reverse && start >= beginSlot) ) {
                slot = this.inventorySlots.get(start);
                slotStack = slot.getStack();

                if( ItemStackUtils.areEqual(slotStack, stack) && slot.isItemValid(stack) ) {
                    int combStackSize = slotStack.getCount() + stack.getCount();

                    if( combStackSize <= stack.getMaxStackSize() ) {
                        stack.setCount(0);
                        slotStack.setCount(combStackSize);
                        slot.onSlotChanged();
                        slotChanged = true;
                    } else if( slotStack.getCount() < stack.getMaxStackSize() ) {
                        stack.shrink(stack.getMaxStackSize() - slotStack.getCount());
                        slotStack.setCount(stack.getMaxStackSize());
                        slot.onSlotChanged();
                        slotChanged = true;
                    }
                }

                if( reverse ) {
                    start--;
                } else {
                    start++;
                }
            }
        }

        if( stack.getCount() > 0 ) {
            if( reverse ) {
                start = endSlot - 1;
            } else {
                start = beginSlot;
            }

            while( !reverse && start < endSlot || reverse && start >= beginSlot ) {
                slot = this.inventorySlots.get(start);

                if( !ItemStackUtils.isValid(slot.getStack()) && slot.isItemValid(stack) ) {
                    slot.putStack(stack.copy());
                    slot.onSlotChanged();
                    stack.setCount(0);
                    slotChanged = true;
                    break;
                }

                if( reverse ) {
                    start--;
                } else {
                    start++;
                }
            }
        }

        return slotChanged;
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack origStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            origStack = slotStack.copy();

            if( slotId < 23 ) { // if clicked stack is from TileEntity
                if( !super.mergeItemStack(slotStack, 23, 59, true) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.assembly_upg_auto ) {
                if( transferUpgrade(ItemRegistry.assembly_upg_auto, origStack, slotStack, 0) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.assembly_upg_speed ) {
                if( transferUpgrade(ItemRegistry.assembly_upg_speed, origStack, slotStack, 1) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.assembly_upg_filter ) {
                if( transferUpgrade(ItemRegistry.assembly_upg_filter, origStack, slotStack, 2) ) {
                    return ItemStack.EMPTY;
                }
            } else if( !this.mergeItemStack(slotStack, 5, 23, false) ) { // if clicked stack is from player and also merge to input slots is sucessful
                return ItemStack.EMPTY;
            }

            if( slotStack.getCount() == 0 ) { // if stackSize of slot got to 0
                slot.putStack(ItemStack.EMPTY);
            } else { // update changed slot stack state
                slot.onSlotChanged();
            }

            if( slotStack.getCount() == origStack.getCount() ) { // if nothing changed stackSize-wise
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return origStack;
    }

    public static class SlotOutput
            extends Slot
    {
        public SlotOutput(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return false;
        }
    }

    public static class SlotIngredients
            extends Slot
    {
        private final TileEntityTurretAssembly assembly;

        public SlotIngredients(TileEntityTurretAssembly assembly, int id, int x, int y) {
            super(assembly, id, x, y);
            this.assembly = assembly;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return super.isItemValid(stack) && this.assembly.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }

    public static class SlotAutoUpgrade
            extends Slot
    {
        private final TileEntityTurretAssembly assembly;

        public SlotAutoUpgrade(TileEntityTurretAssembly assembly, int id, int x, int y) {
            super(assembly, id, x, y);
            this.assembly = assembly;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return !this.assembly.hasAutoUpgrade() && stack.getItem() == ItemRegistry.assembly_upg_auto;
        }
    }

    public static class SlotSpeedUpgrade
            extends Slot
    {
        private final TileEntityTurretAssembly assembly;

        public SlotSpeedUpgrade(TileEntityTurretAssembly assembly, int id, int x, int y) {
            super(assembly, id, x, y);
            this.assembly = assembly;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return !this.assembly.hasSpeedUpgrade() && stack.getItem() == ItemRegistry.assembly_upg_speed;
        }
    }

    public static class SlotFilterUpgrade
            extends Slot
    {
        private final TileEntityTurretAssembly assembly;

        public SlotFilterUpgrade(TileEntityTurretAssembly assembly, int id, int x, int y) {
            super(assembly, id, x, y);
            this.assembly = assembly;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return !this.assembly.hasFilterUpgrade() && stack.getItem() == ItemRegistry.assembly_upg_filter;
        }
    }
}
