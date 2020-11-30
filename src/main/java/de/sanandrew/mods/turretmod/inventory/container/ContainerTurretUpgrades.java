/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.item.ItemUpgrade;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerTurretUpgrades
        extends Container
{

    public ContainerTurretUpgrades(IInventory playerInv, UpgradeProcessor proc, boolean isRemote) {
        for( int i = 0; i < 4; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new SlotUpgrade(proc, j + i * 9, 8 + j * 18, 40 + i * 18, isRemote));
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
            } else if( origStack.getItem() instanceof ItemUpgrade ) {
                if( !this.mergeItemStack(slotStack, 0, 36, false) ) {
                    return ItemStack.EMPTY;
                }
            }

            if (TmrUtils.finishTransfer(player, origStack, slot, slotStack)) return ItemStack.EMPTY;
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
        private final boolean isRemote;

        SlotUpgrade(UpgradeProcessor proc, int id, int x, int y, boolean isRemote) {
            super(proc, id, x, y);
            this.upgProc = proc;
            this.isRemote = isRemote;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return (!this.isRemote || this.upgProc.canAccessRemotely())
                   && this.upgProc.isItemValidForSlot(this.getSlotIndex(), stack);
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return (!this.isRemote || this.upgProc.canAccessRemotely())
                   && super.canTakeStack(playerIn);
        }
    }
}
