/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerElectrolyteGenerator
        extends Container
{
    private TileEntityElectrolyteGenerator tile;

    public ContainerElectrolyteGenerator(IInventory playerInv, TileEntityElectrolyteGenerator generator) {
        this.tile = generator;

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new SlotIngredients(generator, i, 8 + i*18, 17));
        }
        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new SlotProcessing(generator, i+9, 8 + i*18, 43));
        }
        for( int i = 0; i < 5; i++ ) {
            this.addSlotToContainer(new SlotOutput(generator, i+18, 44 + i*18, 76));
        }

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 198));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tile.isUseableByPlayer(player);
    }

    protected boolean mergeItemStackInput(ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        boolean slotChanged = false;
        int start;
        Slot slot;
        ItemStack slotStack;

        if( reverse ) {
            start = endSlot - 1;
        } else {
            start = beginSlot;
        }

        while( !reverse && start < endSlot || reverse && start >= beginSlot ) {
            slot = this.inventorySlots.get(start);
            slotStack = slot.getStack();

            if( slotStack == null && slot.isItemValid(stack) ) {
                slot.putStack(stack.copy());
                slot.getStack().stackSize = 1;
                slot.onSlotChanged();
                stack.stackSize--;
                slotChanged = true;
                break;
            }

            if( reverse ) {
                start--;
            } else {
                start++;
            }
        }

        return slotChanged;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack origStack = null;
        Slot slot = this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            assert slotStack != null;
            origStack = slotStack.copy();

            if( slotId < 23 ) { // if clicked stack is from TileEntity
                if( !this.mergeItemStack(slotStack, 22, 58, true) ) {
                    return null;
                }
            } else if( !this.mergeItemStackInput(slotStack, 0, 9, false) ) { // if clicked stack is from player and also merge to input slots is sucessful
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

        @Override
        public int getSlotStackLimit() {
            return 64;
        }
    }

    public static class SlotProcessing
            extends Slot
    {
        public SlotProcessing(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer player) {
            return false;
        }
    }

    public static class SlotIngredients
            extends Slot
    {
        private final TileEntityElectrolyteGenerator generator;

        public SlotIngredients(TileEntityElectrolyteGenerator generator, int id, int x, int y) {
            super(generator, id, x, y);
            this.generator = generator;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack) && this.generator.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }

//    public static class SlotAutoUpgrade
//            extends Slot
//    {
//        private final TileEntityTurretAssembly assembly;
//
//        public SlotAutoUpgrade(TileEntityTurretAssembly assembly, int id, int x, int y) {
//            super(assembly, id, x, y);
//            this.assembly = assembly;
//        }
//
//        @Override
//        public boolean isItemValid(ItemStack stack) {
//            return stack != null && !this.assembly.hasAutoUpgrade() && stack.getItem() == ItemRegistry.asbAuto;
//        }
//    }
//
//    public static class SlotSpeedUpgrade
//            extends Slot
//    {
//        private final TileEntityTurretAssembly assembly;
//
//        public SlotSpeedUpgrade(TileEntityTurretAssembly assembly, int id, int x, int y) {
//            super(assembly, id, x, y);
//            this.assembly = assembly;
//        }
//
//        @Override
//        public boolean isItemValid(ItemStack stack) {
//            return stack != null && !this.assembly.hasSpeedUpgrade() && stack.getItem() == ItemRegistry.asbSpeed;
//        }
//    }
//
//    public static class SlotFilterUpgrade
//            extends Slot
//    {
//        private final TileEntityTurretAssembly assembly;
//
//        public SlotFilterUpgrade(TileEntityTurretAssembly assembly, int id, int x, int y) {
//            super(assembly, id, x, y);
//            this.assembly = assembly;
//        }
//
//        @Override
//        public boolean isItemValid(ItemStack stack) {
//            return stack != null && !this.assembly.hasFilterUpgrade() && stack.getItem() == ItemRegistry.asbFilter;
//        }
//    }
}
