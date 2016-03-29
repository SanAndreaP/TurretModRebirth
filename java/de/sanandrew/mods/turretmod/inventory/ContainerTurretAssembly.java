/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTurretAssembly
        extends Container
{
    private TileEntityTurretAssembly tile;

    public ContainerTurretAssembly(IInventory playerInv, TileEntityTurretAssembly assembly) {
        this.tile = assembly;

        this.addSlotToContainer(new SlotOutput(assembly, 0, 162, 10));

        for( int i = 0; i < 2; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(assembly, j + i * 9 + 1, 36 + j * 18, 100 + i * 18));
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
    public boolean canInteractWith(EntityPlayer player)
    {
        return this.tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack origStack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            origStack = slotStack.copy();

            if( slotId < 19 ) { // if clicked stack is from TileEntity
                if( !this.mergeItemStack(slotStack, 19, 55, true) ) {
                    return null;
                }
            } else if( !this.mergeItemStack(slotStack, 1, 18, false) ) { // if clicked stack is from player and also merge to input slots is sucessful
                return null;
            }

            if( slotStack.stackSize == 0 ) { // if stackSize of slot got to 0
                slot.putStack(null);
            } else { // update changed slot stack state
                slot.onSlotChanged();
            }

            if( slotStack.stackSize == origStack.stackSize ) { // if nothing changed stackSize-wise
                return null;
            }

            slot.onPickupFromSlot(player, slotStack);
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
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
