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
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
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
    private final IInventory inventory;
    private final TileEntityTurretAssembly tile;

    public ContainerTurretAssembly(IInventory playerInv, TileEntityTurretAssembly assembly) {
        this.tile = assembly;
        this.inventory = this.tile.getInventory();

        this.addSlotToContainer(new SlotOutput(0, 172, 10));
        this.addSlotToContainer(new SlotAutoUpgrade(1, 14, 100));
        this.addSlotToContainer(new SlotSpeedUpgrade(2, 14, 118));
        this.addSlotToContainer(new SlotFilterUpgrade(3, 202, 100));
        this.addSlotToContainer(new SlotOutput(4, 202, 118));

        for( int i = 0; i < 2; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new SlotIngredients(j + i * 9 + 5, 36 + j * 18, 100 + i * 18));
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
        return this.inventory.isUsableByPlayer(player);
    }

    private boolean transferUpgrade(Item desiredItm, ItemStack origStack, ItemStack slotStack, int upgSlot) {
        int origStackSize = desiredItm.getItemStackLimit(origStack);
        desiredItm.setMaxStackSize(1);
        slotStack.stackSize = (1);
        if( !super.mergeItemStack(slotStack, 1 + upgSlot, 2 + upgSlot, false) ) {
            slotStack.stackSize = (origStack.stackSize);
            desiredItm.setMaxStackSize(origStackSize);
            return true;
        } else {
            slotStack.stackSize = (origStack.stackSize - 1);
        }
        desiredItm.setMaxStackSize(origStackSize);
        return false;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        boolean slotChanged = false;
        int start = beginSlot;

        if( reverse ) {
            start = endSlot - 1;
        }

        Slot slot;
        ItemStack slotStack;

        if( stack.isStackable() ) {
            while( stack.stackSize > 0 && (!reverse && start < endSlot || reverse && start >= beginSlot) ) {
                slot = this.inventorySlots.get(start);
                slotStack = slot.getStack();

                if( ItemStackUtils.areEqual(slotStack, stack) && slot.isItemValid(stack) && ItemStackUtils.isValid(slotStack) ) {
                    int combStackSize = slotStack.stackSize + stack.stackSize;

                    if( combStackSize <= stack.getMaxStackSize() ) {
                        stack.stackSize = (0);
                        slotStack.stackSize = (combStackSize);
                        slot.onSlotChanged();
                        slotChanged = true;
                    } else if( slotStack.stackSize < stack.getMaxStackSize() ) {
                        stack.stackSize -= (stack.getMaxStackSize() - slotStack.stackSize);
                        slotStack.stackSize = (stack.getMaxStackSize());
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

        if( stack.stackSize > 0 ) {
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
                    stack.stackSize = (0);
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
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack origStack = ItemStackUtils.getEmpty();
        Slot slot = this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            if( ItemStackUtils.isValid(slotStack) ) {
                origStack = slotStack.copy();

                if( slotId < 23 ) { // if clicked stack is from TileEntity
                    if( !super.mergeItemStack(slotStack, 23, 59, true) ) {
                        return ItemStackUtils.getEmpty();
                    }
                } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_AUTO ) {
                    if( transferUpgrade(ItemRegistry.ASSEMBLY_UPG_AUTO, origStack, slotStack, 0) ) {
                        return ItemStackUtils.getEmpty();
                    }
                } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_SPEED ) {
                    if( transferUpgrade(ItemRegistry.ASSEMBLY_UPG_SPEED, origStack, slotStack, 1) ) {
                        return ItemStackUtils.getEmpty();
                    }
                } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_FILTER ) {
                    if( transferUpgrade(ItemRegistry.ASSEMBLY_UPG_FILTER, origStack, slotStack, 2) ) {
                        return ItemStackUtils.getEmpty();
                    }
                } else if( !this.mergeItemStack(slotStack, 5, 23, false) ) { // if clicked stack is from player and also merge to input slots is sucessful
                    return ItemStackUtils.getEmpty();
                }

                if( slotStack.stackSize == 0 ) { // if stackSize of slot got to 0
                    slot.putStack(ItemStackUtils.getEmpty());
                } else { // update changed slot stack state
                    slot.onSlotChanged();
                }

                if( slotStack.stackSize == origStack.stackSize ) { // if nothing changed stackSize-wise
                    return ItemStackUtils.getEmpty();
                }

                slot.func_82870_a(player, slotStack);
            }
        }

        return origStack;
    }

    private class SlotOutput
            extends Slot
    {
        public SlotOutput(int id, int x, int y) {
            super(ContainerTurretAssembly.this.inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }

    private class SlotIngredients
            extends Slot
    {
        public SlotIngredients(int id, int x, int y) {
            super(ContainerTurretAssembly.this.inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack) && ContainerTurretAssembly.this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }

    private class SlotAutoUpgrade
            extends Slot
    {
        public SlotAutoUpgrade(int id, int x, int y) {
            super(ContainerTurretAssembly.this.inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return !ContainerTurretAssembly.this.tile.hasAutoUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_AUTO;
        }
    }

    private class SlotSpeedUpgrade
            extends Slot
    {
        public SlotSpeedUpgrade(int id, int x, int y) {
            super(ContainerTurretAssembly.this.inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return !ContainerTurretAssembly.this.tile.hasSpeedUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_SPEED;
        }
    }

    private class SlotFilterUpgrade
            extends Slot
    {
        public SlotFilterUpgrade(int id, int x, int y) {
            super(ContainerTurretAssembly.this.inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return !ContainerTurretAssembly.this.tile.hasFilterUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_FILTER;
        }
    }
}
