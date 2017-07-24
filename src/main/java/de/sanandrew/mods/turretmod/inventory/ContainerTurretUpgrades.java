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

    private boolean transferUpgrade(Item desiredItm, @Nonnull ItemStack origStack, @Nonnull ItemStack slotStack) {
        int origStackSize = desiredItm.getItemStackLimit(origStack);
        desiredItm.setMaxStackSize(1);
        slotStack.setCount(1);

        if( !this.mergeItemStack(slotStack, 0, 36, false) ) {
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
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack origStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            origStack = slotStack.copy();

            if( slotId < 36 ) { // if clicked stack is from Processor
                if( !super.mergeItemStack(slotStack, 36, 72, true) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.turret_upgrade ) {
                if( transferUpgrade(origStack.getItem(), origStack, slotStack) ) {
                    return ItemStack.EMPTY;
                }
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
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return this.upgProc.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }
}
