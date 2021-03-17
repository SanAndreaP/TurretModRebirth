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
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.ElectrolyteInventory;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.ElectrolyteProcess;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.ElectrolyteSyncData;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
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

public class ContainerElectrolyteGenerator
        extends Container
{
    private final ElectrolyteInventory inventory;
    public final  ElectrolyteSyncData      data;
    private final List<ElectrolyteProcess> processesView;

    public ContainerElectrolyteGenerator(int id, PlayerInventory playerInventory, ElectrolyteInventory electrolyteInventory,
                                         ElectrolyteSyncData syncData, NonNullList<ElectrolyteProcess> processes)
    {
        super(ContainerRegistry.ELECTROLYTE_GENERATOR, id);

        this.processesView = Collections.unmodifiableList(processes);

        this.inventory = electrolyteInventory;
        this.data = syncData;

        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new SlotInput(i, 8 + i*18, 17));
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new SlotProcessing(i, 8 + i*18));
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

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
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

    public static class Factory
            implements IContainerFactory<ContainerElectrolyteGenerator>
    {
        public static final Factory INSTANCE = new Factory();

        @Override
        public ContainerElectrolyteGenerator create(int windowId, PlayerInventory inv, PacketBuffer data) {
            TileEntity te = Objects.requireNonNull(inv.player.world.getTileEntity(data.readBlockPos()));
            return new ContainerElectrolyteGenerator(windowId, inv, new ElectrolyteInventory(() -> inv.player.world),
                                                     new ElectrolyteSyncData(), ((TileEntityElectrolyteGenerator) te).processes);
        }
    }

    class SlotInput
            extends SlotItemHandler
    {
        private final int index;

        public SlotInput(int index, int xPosition, int yPosition) {
            super(ContainerElectrolyteGenerator.this.inventory, index, xPosition, yPosition);
            this.index = index;
        }

        @Override
        public boolean canTakeStack(PlayerEntity playerIn) {
            return !ContainerElectrolyteGenerator.this.inventory.extractInsertItem(this.index, 1, true).isEmpty();
        }

        @Nonnull
        @Override
        public ItemStack decrStackSize(int amount) {
            return ContainerElectrolyteGenerator.this.inventory.extractInsertItem(this.index, amount, false);
        }
    }

    class SlotProcessing
            extends Slot
    {
        private final int index;

        SlotProcessing(int id, int x) {
            super(ContainerElectrolyteGenerator.this.inventory, id, x, 43);
            this.index = id;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(@Nonnull PlayerEntity player) {
            return false;
        }

        @Override
        @Nonnull
        public ItemStack getStack() {
            return ContainerElectrolyteGenerator.this.processesView.get(this.index).processStack;
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
