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
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerTurretUpgrades
        extends Container
{

    public ContainerTurretUpgrades(IInventory playerInv, UpgradeProcessor proc) {
        for( int i = 0; i < 4; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new SlotUpgrade(proc, j + i * 9, 7 + j * 18, 25 + i * 18));
            }
        }

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 7 + j * 18, 110 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new Slot(playerInv, i, 7 + i * 18, 168));
        }
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

    private boolean transferUpgrade(Item desiredItm, ItemStack origStack, ItemStack slotStack) {
        int origStackSize = desiredItm.getItemStackLimit(origStack);
        desiredItm.setMaxStackSize(1);
        slotStack.stackSize = (1);

        if( !this.mergeItemStack(slotStack, 0, 36, false) ) {
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
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack origStack = ItemStackUtils.getEmpty();
        Slot slot = this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            if( ItemStackUtils.isValid(slotStack) ) {
                slotStack = slotStack.copy();
                origStack = slotStack.copy();

                if( slotId < 36 ) { // if clicked stack is from Processor
                    if( !super.mergeItemStack(slotStack, 36, 72, true) ) {
                        return ItemStackUtils.getEmpty();
                    }
                } else if( origStack.getItem() == ItemRegistry.TURRET_UPGRADE ) {
                    if( transferUpgrade(origStack.getItem(), origStack, slotStack) ) {
                        return ItemStackUtils.getEmpty();
                    }
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

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    public static class SlotUpgrade
            extends Slot
    {
        private final UpgradeProcessor upgProc;

        public SlotUpgrade(UpgradeProcessor proc, int id, int x, int y) {
            super(proc, id, x, y);
            this.upgProc = proc;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return this.upgProc.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }
}
