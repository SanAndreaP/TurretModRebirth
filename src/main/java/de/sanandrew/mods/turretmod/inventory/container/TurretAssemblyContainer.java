/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyInventory;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblySyncData;
import de.sanandrew.mods.turretmod.tileentity.assembly.TurretAssemblyEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TurretAssemblyContainer
        extends Container
{
    private final IInventory           inventory;
    public final TurretAssemblyEntity tile;
    public final AssemblySyncData data;

    public TurretAssemblyContainer(int windowId, PlayerInventory playerInv, TurretAssemblyEntity assembly, AssemblySyncData syncData) {
        super(ContainerRegistry.ASSEMBLY, windowId);
        this.tile = assembly;
        this.inventory = this.tile.getInventory();
        this.data = syncData;

        IItemHandler tileInv = assembly.getInventory();
        this.addSlot(new SlotItemHandler(tileInv, AssemblyInventory.SLOT_OUTPUT, 163, 10));
        this.addSlot(new SlotItemHandler(tileInv, AssemblyInventory.SLOT_OUTPUT_CARTRIDGE, 181, 10));
        this.addSlot(new SlotAutoUpgrade());
        this.addSlot(new SlotSpeedUpgrade());
        this.addSlot(new SlotFilterUpgrade());
        this.addSlot(new SlotRedstoneUpgrade());

        for( int i = 0; i < 2; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlot(new SlotIngredients(AssemblyInventory.getInsertSlotId(j + i * 9), 36 + j * 18, 91 + i * 18));
            }
        }

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 36 + j * 18, 140 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new Slot(playerInv, i, 36 + i * 18, 198));
        }

        this.addDataSlots(this.data);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.inventory.stillValid(player);
    }

    @Override
    protected boolean moveItemStackTo(@Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        return InventoryUtils.mergeItemStack(this, stack, beginSlot, endSlot, reverse);
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(PlayerEntity player, int slotId) {
        ItemStack origStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if( slot != null && slot.hasItem() ) {
            ItemStack slotStack = slot.getItem();
            origStack = slotStack.copy();

            int invSize = this.inventory.getContainerSize();
            if( slotId < invSize ) { // if clicked stack is from TileEntity
                if( !super.moveItemStackTo(slotStack, invSize, invSize + 36, true) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_AUTO ) {
                if( !this.moveItemStackTo(slotStack, AssemblyInventory.SLOT_UPGRADE_AUTO, AssemblyInventory.SLOT_UPGRADE_AUTO + 1, false) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_SPEED ) {
                if( !this.moveItemStackTo(slotStack, AssemblyInventory.SLOT_UPGRADE_SPEED, AssemblyInventory.SLOT_UPGRADE_SPEED + 1, false) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_FILTER ) {
                if( !this.moveItemStackTo(slotStack, AssemblyInventory.SLOT_UPGRADE_FILTER, AssemblyInventory.SLOT_UPGRADE_FILTER + 1, false) ) {
                    return ItemStack.EMPTY;
                }
            } else if( origStack.getItem() == ItemRegistry.ASSEMBLY_UPG_REDSTONE ) {
                if( !this.moveItemStackTo(slotStack, AssemblyInventory.SLOT_UPGRADE_REDSTONE, AssemblyInventory.SLOT_UPGRADE_REDSTONE + 1, false) ) {
                    return ItemStack.EMPTY;
                }
            } else if( !this.moveItemStackTo(slotStack, AssemblyInventory.getInsertSlotId(0), invSize, false) ) { // if clicked stack is from player and also merge to input slots is sucessful
                return ItemStack.EMPTY;
            }

            if( InventoryUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    private class SlotIngredients
            extends Slot
    {
        private SlotIngredients(int id, int x, int y) {
            super(TurretAssemblyContainer.this.inventory, id, x, y);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return super.mayPlace(stack) && TurretAssemblyContainer.this.inventory.canPlaceItem(this.getSlotIndex(), stack);
        }
    }

    private class SlotAutoUpgrade
            extends Slot
    {
        private SlotAutoUpgrade() {
            super(TurretAssemblyContainer.this.inventory, AssemblyInventory.SLOT_UPGRADE_AUTO, 14, 91);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return !TurretAssemblyContainer.this.tile.hasAutoUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_AUTO;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private class SlotSpeedUpgrade
            extends Slot
    {
        private SlotSpeedUpgrade() {
            super(TurretAssemblyContainer.this.inventory, AssemblyInventory.SLOT_UPGRADE_SPEED, 14, 109);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return !TurretAssemblyContainer.this.tile.hasSpeedUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_SPEED;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private class SlotRedstoneUpgrade
            extends Slot
    {
        private SlotRedstoneUpgrade() {
            super(TurretAssemblyContainer.this.inventory, AssemblyInventory.SLOT_UPGRADE_REDSTONE, 202, 109);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return !TurretAssemblyContainer.this.tile.hasRedstoneUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_REDSTONE;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private class SlotFilterUpgrade
            extends Slot
    {
        private SlotFilterUpgrade() {
            super(TurretAssemblyContainer.this.inventory, AssemblyInventory.SLOT_UPGRADE_FILTER, 202, 91);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return !TurretAssemblyContainer.this.tile.hasFilterUpgrade() && stack.getItem() == ItemRegistry.ASSEMBLY_UPG_FILTER;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    public static class Factory
            implements IContainerFactory<TurretAssemblyContainer>
    {
        public static final TurretAssemblyContainer.Factory INSTANCE = new TurretAssemblyContainer.Factory();

        @Override
        public TurretAssemblyContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            TileEntity te = Objects.requireNonNull(inv.player.level.getBlockEntity(data.readBlockPos()));
            return new TurretAssemblyContainer(windowId, inv, (TurretAssemblyEntity) te, new AssemblySyncData());
        }
    }
}
