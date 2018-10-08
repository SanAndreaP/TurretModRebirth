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
import de.sanandrew.mods.turretmod.util.TmrUtils;
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
                this.addSlotToContainer(new SlotUpgrade(proc, j + i * 9, 8 + j * 18, 40 + i * 18));
            }
        }

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 183));
        }
    }

    @Override
    protected boolean mergeItemStack(@Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        return TmrUtils.mergeItemStack(this, stack, beginSlot, endSlot, reverse);
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
        ItemStack origStack = ItemStackUtils.getEmpty();
        Slot slot = this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack().copy();
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

            if( slotStack.getCount() == 0 ) { // if stackSize of slot got to 0
                slot.putStack(ItemStackUtils.getEmpty());
            } else { // update changed slot stack state
                slot.onSlotChanged();
            }

            if( slotStack.getCount() == origStack.getCount() ) { // if nothing changed stackSize-wise
                return ItemStackUtils.getEmpty();
            }

            slot.onTake(player, slotStack);
        }

        return origStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    static class SlotUpgrade
            extends Slot
    {
        private final UpgradeProcessor upgProc;

        SlotUpgrade(UpgradeProcessor proc, int id, int x, int y) {
            super(proc, id, x, y);
            this.upgProc = proc;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return this.upgProc.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }
}
