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
import de.sanandrew.mods.turretmod.item.ItemAssemblyUpgrade;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAssemblyFilter
        extends Container
{
    private ItemStack filterStack;
    private int filterStackSlot;
    private InventoryAssemblyFilter filterInv;

    public ContainerAssemblyFilter(IInventory playerInv, ItemStack stack, int slot) {
        this.filterStack = stack;
        this.filterStackSlot = slot;
        this.filterInv = new InventoryAssemblyFilter(ItemAssemblyUpgrade.Filter.getFilterStacks(stack));

        for( int i = 0; i < 2; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new SlotFilter(this.filterInv, j + i * 9, 8 + j * 18, 17 + i * 18));
            }
        }

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 66 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 124));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.filterInv.isUseableByPlayer(player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        if( !player.world.isRemote && ItemStackUtils.isValid(player.getHeldItemMainhand()) && player.getHeldItemMainhand().getItem() == ItemRegistry.assembly_upg_filter ) {
            ItemAssemblyUpgrade.Filter.setFilterStacks(this.filterStack, this.filterInv.invStacks);
            player.inventory.setInventorySlotContents(this.filterStackSlot, this.filterStack.copy());
            player.inventoryContainer.detectAndSendChanges();
        }

        super.onContainerClosed(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            assert slotStack != null;
            ItemStack origStack = slotStack.copy();

            if( slotId < 18 ) {
                slot.putStack(null);
            } else {
                for( int i = 0; i < 18; i++ ) {
                    Slot fltSlot = this.inventorySlots.get(i);
                    if( !fltSlot.getHasStack() ) {
                        origStack.stackSize = 1;
                        fltSlot.putStack(origStack);
                        return null;
                    }
                }
            }
        }

        return null;
    }

    public static class SlotFilter
            extends Slot
    {
        public SlotFilter(InventoryAssemblyFilter filter, int id, int x, int y) {
            super(filter, id, x, y);
        }

        @Override
        public boolean canTakeStack(EntityPlayer player) {
            this.putStack(null);
            return false;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if( ItemStackUtils.isValid(stack) ) {
                this.putStack(stack.copy());
            }
            return false;
        }
    }
}
