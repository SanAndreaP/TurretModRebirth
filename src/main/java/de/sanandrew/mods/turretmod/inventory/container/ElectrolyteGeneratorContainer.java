/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteInventory;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteProcess;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteSyncData;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteGeneratorTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ElectrolyteGeneratorContainer
        extends Container
{
    private final ElectrolyteInventory inventory;
    public final  ElectrolyteSyncData      data;
    private final List<ElectrolyteProcess> processesView;

    public ElectrolyteGeneratorContainer(int id, PlayerInventory playerInventory, ElectrolyteInventory electrolyteInventory,
                                         ElectrolyteSyncData syncData, NonNullList<ElectrolyteProcess> processes)
    {
        super(ContainerRegistry.ELECTROLYTE_GENERATOR, id);

        this.processesView = Collections.unmodifiableList(processes);

        this.inventory = electrolyteInventory;
        this.data = syncData;

        for( int i = 0; i < ElectrolyteInventory.INPUT_SLOT_COUNT; i++ ) {
            this.addSlot(new InputSlot(i, 8 + i * 18, 17));
        }
        for( int i = 0; i < ElectrolyteInventory.OUTPUT_SLOT_COUNT; i++ ) {
            this.addSlot(new SlotItemHandler(this.inventory, i+9, 44 + i*18, 76));
        }
        for( int i = 0; i < ElectrolyteInventory.INPUT_SLOT_COUNT; i++ ) {
            this.addSlot(new ProcessingSlot(i, 8 + i * 18));
        }

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }

        this.addDataSlots(this.data);
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity playerIn) {
        return this.inventory.stillValid(playerIn);
    }

    private boolean moveItemStackToInput(@Nonnull ItemStack stack) {
        boolean slotChanged = false;
        int start = 0;
        Slot slot;

        while( start < 9 ) {
            slot = this.slots.get(start);

            if( !ItemStackUtils.isValid(slot.getItem()) && slot.mayPlace(stack) ) {
                slot.set(stack.copy());
                slot.getItem().setCount(1);
                slot.setChanged();
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
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotId) {
        ItemStack origStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if( slot != null && slot.hasItem() ) {
            // 2x input slots, because of processing slots
            final int maxTESlotId = ElectrolyteInventory.INPUT_SLOT_COUNT * 2 + ElectrolyteInventory.OUTPUT_SLOT_COUNT - 1;

            ItemStack slotStack = slot.getItem();
            origStack = slotStack.copy();

            //TODO: check, if shift-click puts invalid items in the input slots
            if( slotId <= maxTESlotId ) { // if clicked stack is from TileEntity
                if( !this.moveItemStackTo(slotStack, maxTESlotId, maxTESlotId + 36, true) ) {
                    return ItemStack.EMPTY;
                }
            } else if( !this.moveItemStackToInput(slotStack) ) { // if clicked stack is from player and also merge to input slots is sucessful
                return ItemStack.EMPTY;
            }

            if( InventoryUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    public static class Factory
            implements IContainerFactory<ElectrolyteGeneratorContainer>
    {
        public static final Factory INSTANCE = new Factory();

        @Override
        public ElectrolyteGeneratorContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            TileEntity te = Objects.requireNonNull(inv.player.level.getBlockEntity(data.readBlockPos()));
            return new ElectrolyteGeneratorContainer(windowId, inv, new ElectrolyteInventory(() -> inv.player.level),
                                                     new ElectrolyteSyncData(), ((ElectrolyteGeneratorTileEntity) te).processes);
        }
    }

    private class InputSlot
            extends SlotItemHandler
    {
        private final int index;

        public InputSlot(int index, int xPosition, int yPosition) {
            super(ElectrolyteGeneratorContainer.this.inventory, index, xPosition, yPosition);
            this.index = index;
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return ElectrolyteGeneratorContainer.this.inventory.isItemValid(this.index, stack);
        }

        @Override
        public boolean mayPickup(PlayerEntity playerIn) {
            return !ElectrolyteGeneratorContainer.this.inventory.extractInsertItem(this.index, 1, true).isEmpty();
        }

        @Nonnull
        @Override
        public ItemStack remove(int amount) {
            return ElectrolyteGeneratorContainer.this.inventory.extractInsertItem(this.index, amount, false);
        }
    }

    private class ProcessingSlot
            extends Slot
    {
        private final int index;

        ProcessingSlot(int id, int x) {
            super(ElectrolyteGeneratorContainer.this.inventory, id, x, 43);
            this.index = id;
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(@Nonnull PlayerEntity player) {
            return false;
        }

        @Override
        @Nonnull
        public ItemStack getItem() {
            return ElectrolyteGeneratorContainer.this.processesView.get(this.index).processStack;
        }

        @Override
        public void set(@Nonnull ItemStack stack) { }

        @Override
        public void onQuickCraft(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) { }

        @Override
        public int getMaxStackSize(@Nonnull ItemStack stack) {
            return 1;
        }

        @Override
        @Nonnull
        public ItemStack remove(int amount) {
            return ItemStack.EMPTY;
        }
    }
}
