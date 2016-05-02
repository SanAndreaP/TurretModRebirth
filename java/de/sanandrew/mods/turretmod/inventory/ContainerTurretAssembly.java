/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
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

        this.addSlotToContainer(new SlotOutput(assembly, 0, 172, 10));
        this.addSlotToContainer(new SlotAutoUpgrade(assembly, 1, 14, 100));

        for( int i = 0; i < 2; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(assembly, j + i * 9 + 2, 36 + j * 18, 100 + i * 18));
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
        return this.tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack origStack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            origStack = slotStack.copy();

            if( slotId < 20 ) { // if clicked stack is from TileEntity
                if( !this.mergeItemStack(slotStack, 20, 55, true) ) {
                    return null;
                }
            } else if( origStack.getItem() == ItemRegistry.asbAuto ) {
                int origStackSize = ItemRegistry.asbAuto.getItemStackLimit(origStack);
                ItemRegistry.asbAuto.setMaxStackSize(1);
                slotStack.stackSize = 1;
                if( !this.mergeItemStack(slotStack, 1, 2, false) ) {
                    slotStack.stackSize = origStack.stackSize;
                    ItemRegistry.asbAuto.setMaxStackSize(origStackSize);
                    return null;
                } else {
                    slotStack.stackSize = origStack.stackSize - 1;
                }
                ItemRegistry.asbAuto.setMaxStackSize(origStackSize);
            } else if( !this.mergeItemStack(slotStack, 2, 20, false) ) { // if clicked stack is from player and also merge to input slots is sucessful
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

    public static class SlotAutoUpgrade
            extends Slot
    {
        private final TileEntityTurretAssembly assembly;

        public SlotAutoUpgrade(TileEntityTurretAssembly assembly, int id, int x, int y) {
            super(assembly, id, x, y);
            this.assembly = assembly;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack != null && stack.getItem() == ItemRegistry.asbAuto && !ItemStackUtils.isValidStack(assembly.getStackInSlot(1));
        }
    }
}
