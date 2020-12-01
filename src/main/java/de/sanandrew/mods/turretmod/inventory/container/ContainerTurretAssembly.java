/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.turretmod.inventory.AssemblyInventory;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerTurretAssembly
        extends Container
{
    private final IInventory inventory;
    private final TileEntityTurretAssembly tile;

    public ContainerTurretAssembly(IInventory playerInv, TileEntityTurretAssembly assembly) {
        this.tile = assembly;
        this.inventory = this.tile.getInventory();

        IInventory tileInv = assembly.getInventory();
        this.addSlotToContainer(new SlotOutput(tileInv, AssemblyInventory.SLOT_OUTPUT, 163, 10));
        this.addSlotToContainer(new SlotOutput(tileInv, AssemblyInventory.SLOT_OUTPUT_CARTRIDGE, 181, 10));
        this.addSlotToContainer(new SlotAutoUpgrade());
        this.addSlotToContainer(new SlotSpeedUpgrade());
        this.addSlotToContainer(new SlotFilterUpgrade());
        this.addSlotToContainer(new SlotRedstoneUpgrade());

        for( int i = 0; i < 2; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new SlotIngredients(j + i * 9 + AssemblyInventory.RESOURCE_SLOT_FIRST, 36 + j * 18, 100 + i * 18));
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
        return this.inventory.isUsableByPlayer(player);
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

            int invSize = this.inventory.getSizeInventory();
            if( slotId < invSize ) { // if clicked stack is from TileEntity
                if( !super.mergeItemStack(slotStack, invSize, invSize + 36, true) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_AUTO ) {
                if( !this.mergeItemStack(slotStack, AssemblyInventory.SLOT_UPGRADE_AUTO, AssemblyInventory.SLOT_UPGRADE_AUTO + 1, false) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_SPEED ) {
                if( !this.mergeItemStack(slotStack, AssemblyInventory.SLOT_UPGRADE_SPEED, AssemblyInventory.SLOT_UPGRADE_SPEED + 1, false) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_FILTER ) {
                if( !this.mergeItemStack(slotStack, AssemblyInventory.SLOT_UPGRADE_FILTER, AssemblyInventory.SLOT_UPGRADE_FILTER + 1, false) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_REDSTONE ) {
                if( !this.mergeItemStack(slotStack, AssemblyInventory.SLOT_UPGRADE_REDSTONE, AssemblyInventory.SLOT_UPGRADE_REDSTONE + 1, false) ) {
                    return ItemStack.EMPTY;
                }
            } else if( !this.mergeItemStack(slotStack, AssemblyInventory.RESOURCE_SLOT_FIRST, invSize, false) ) { // if clicked stack is from player and also merge to input slots is sucessful
                return ItemStack.EMPTY;
            }

            if( TmrUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    private class SlotIngredients
            extends Slot
    {
        private SlotIngredients(int id, int x, int y) {
            super(ContainerTurretAssembly.this.inventory, id, x, y);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return super.isItemValid(stack) && ContainerTurretAssembly.this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }

    private class SlotAutoUpgrade
            extends Slot
    {
        private SlotAutoUpgrade() {
            super(ContainerTurretAssembly.this.inventory, AssemblyInventory.SLOT_UPGRADE_AUTO, 14, 100);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return !ContainerTurretAssembly.this.tile.hasAutoUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_AUTO;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }

    private class SlotSpeedUpgrade
            extends Slot
    {
        private SlotSpeedUpgrade() {
            super(ContainerTurretAssembly.this.inventory, AssemblyInventory.SLOT_UPGRADE_SPEED, 14, 118);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return !ContainerTurretAssembly.this.tile.hasSpeedUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_SPEED;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }

    private class SlotRedstoneUpgrade
            extends Slot
    {
        private SlotRedstoneUpgrade() {
            super(ContainerTurretAssembly.this.inventory, AssemblyInventory.SLOT_UPGRADE_REDSTONE, 202, 118);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return !ContainerTurretAssembly.this.tile.hasRedstoneUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_REDSTONE;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }

    private class SlotFilterUpgrade
            extends Slot
    {
        private SlotFilterUpgrade() {
            super(ContainerTurretAssembly.this.inventory, AssemblyInventory.SLOT_UPGRADE_FILTER, 202, 100);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return !ContainerTurretAssembly.this.tile.hasFilterUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_FILTER;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }
}
