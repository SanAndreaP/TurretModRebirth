/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.ElectrolyteData;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.ElectrolyteInventory;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerElectrolyteGenerator
        extends Container
{
    private final ElectrolyteInventory inventory;
    public final ElectrolyteData data;
//    private final TileEntityElectrolyteGenerator tile;

//    public ContainerElectrolyteGenerator(IInventory playerInv, TileEntityElectrolyteGenerator generator) {
//        super();
//        this.tile = generator;
//
//        for( int i = 0; i < 9; i++ ) {
//            this.addSlotToContainer(new SlotItemHandler(generator.containerItemHandler, i, 8 + i*18, 17));
//        }
//        for( int i = 0; i < 9; i++ ) {
//            this.addSlotToContainer(new SlotProcessing(generator, i, 8 + i*18));
//        }
//        for( int i = 0; i < 5; i++ ) {
//            this.addSlotToContainer(new SlotItemHandler(generator.containerItemHandler, i+9, 44 + i*18, 76));
//        }
//
//        for( int i = 0; i < 3; i++ ) {
//            for( int j = 0; j < 9; j++ ) {
//                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
//            }
//        }
//
//        for( int i = 0; i < 9; i++ ) {
//            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 198));
//        }
//    }

    public ContainerElectrolyteGenerator(int id, PlayerInventory playerInventory, ElectrolyteInventory electrolyteInventory, ElectrolyteData syncData) {
        super(ContainerRegistry.ELECTROLYTE_GENERATOR, id);

        this.inventory = electrolyteInventory;
        this.data = syncData;

        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new SlotItemHandler(this.inventory, i, 8 + i*18, 17));
        }
        //TODO: reimplement processing slots
        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new SlotProcessing(this.inventory, i, 8 + i*18));
        }
        for( int i = 0; i < 5; i++ ) {
            this.addSlot(new SlotItemHandler(this.inventory, i+9, 44 + i*18, 76));
        }

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }

        trackIntArray(this.data);
    }

    public ContainerElectrolyteGenerator(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new ElectrolyteInventory(() -> playerInventory.player.world), new ElectrolyteData());
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.inventory.isUsableByPlayer(playerIn);
    }

    private boolean mergeItemStackInput(@Nonnull ItemStack stack) {
        boolean slotChanged = false;
        int start = 0;
        Slot slot;

        while( start < 9 ) {
            slot = this.inventorySlots.get(start);

            if( !ItemStackUtils.isValid(slot.getStack()) && slot.isItemValid(stack) ) {
                slot.putStack(stack.copy());
                slot.getStack().setCount(1);
                slot.onSlotChanged();
                stack.shrink(1);
                slotChanged = true;
                break;
            }

            start++;
        }

        return slotChanged;
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int slotId) {
        ItemStack origStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            origStack = slotStack.copy();

            if( slotId < 23 ) { // if clicked stack is from TileEntity
                if( !this.mergeItemStack(slotStack, 22, 58, true) ) {
                    return ItemStack.EMPTY;
                }
            } else if( !this.mergeItemStackInput(slotStack) ) { // if clicked stack is from player and also merge to input slots is sucessful
                return ItemStack.EMPTY;
            }

            if( InventoryUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    static class SlotProcessing
            extends Slot
    {
//        private static final IInventory EMPTY_INV = new InventoryBasic("[Null]", true, 0);
//        private final TileEntityElectrolyteGenerator generator;
        private final int index;

        SlotProcessing(ElectrolyteInventory inventory, int id, int x) {
            super(inventory, id, x, 43);
//            this.generator = generator;
            this.index = id;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(PlayerEntity player) {
            return false;
        }

        @Override
        @Nonnull
        public ItemStack getStack() {
            return ItemStack.EMPTY;
//            ElectrolyteProcess proc = this.generator.processes[this.index];
//            return proc == null ? ItemStack.EMPTY : proc.processStack;
        }

        @Override
        public void putStack(@Nonnull ItemStack stack) { }

        @Override
        public void onSlotChange(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) { }

        @Override
        public int getItemStackLimit(@Nonnull ItemStack stack) {
            return 1;
        }

        @Override
        @Nonnull
        public ItemStack decrStackSize(int amount) {
            return ItemStack.EMPTY;
        }
    }
}
