/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.turretmod.inventory.AssemblyFilterInventory;
import dev.sanandrea.mods.turretmod.inventory.ContainerRegistry;
import dev.sanandrea.mods.turretmod.item.AssemblyUpgradeItem;
import dev.sanandrea.mods.turretmod.item.ItemRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.IContainerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssemblyFilterContainer
        extends Container
{
    private final Hand                    hand;
    private final AssemblyFilterInventory filterInv;
    private final int heldItemHash;

    public AssemblyFilterContainer(int windowId, PlayerInventory playerInv, Hand hand) {
        super(ContainerRegistry.ASSEMBLY_FILTER, windowId);

        ItemStack heldItem = playerInv.player.getItemInHand(hand);

        this.hand = hand;
        this.filterInv = new AssemblyFilterInventory(AssemblyUpgradeItem.Filter.getFilterStacks(heldItem));
        this.heldItemHash = heldItem.hashCode();

        for( int i = 0; i < 2; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlot(new SlotFilter(this.filterInv, j + i * 9, 8 + j * 18, 17 + i * 18));
            }
        }

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 66 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 124));
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return this.filterInv.stillValid(player) && this.heldItemHash == player.getItemInHand(this.hand).hashCode();
    }

    @Override
    public void removed(PlayerEntity player) {
        if( !player.level.isClientSide ) {
            ItemStack filterStack = player.getItemInHand(this.hand);
            if( ItemStackUtils.isItem(filterStack, ItemRegistry.ASSEMBLY_UPG_FILTER) ) {
                AssemblyUpgradeItem.Filter.setFilterStacks(filterStack, this.filterInv.invStacks);
                player.setItemInHand(this.hand, filterStack.copy());
                player.inventoryMenu.broadcastChanges();
            }
        }

        super.removed(player);
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotId) {
        Slot slot = this.slots.get(slotId);

        if( slot != null && slot.hasItem() ) {
            ItemStack slotStack = slot.getItem();
            ItemStack origStack = slotStack.copy();

            if( slotId < 18 ) {
                slot.set(ItemStack.EMPTY);
            } else {
                for( int i = 0; i < 18; i++ ) {
                    Slot fltSlot = this.slots.get(i);
                    if( !fltSlot.hasItem() ) {
                        origStack.setCount(1);
                        fltSlot.set(origStack);
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public static class Factory
            implements IContainerFactory<AssemblyFilterContainer>
    {
        public static final AssemblyFilterContainer.Factory INSTANCE = new AssemblyFilterContainer.Factory();

        @Override
        public AssemblyFilterContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            return new AssemblyFilterContainer(windowId, inv, data.readBoolean() ? Hand.OFF_HAND : Hand.MAIN_HAND);
        }
    }

    public static class Provider
            implements INamedContainerProvider
    {
        @Nonnull
        private final Hand hand;
        private final ITextComponent title;

        public Provider(@Nonnull Hand hand, ITextComponent title) {
            this.hand = hand;
            this.title = title;
        }

        @Nonnull
        @Override
        public ITextComponent getDisplayName() {
            return this.title;
        }

        @Nullable
        @Override
        public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
            return new AssemblyFilterContainer(id, playerInventory, hand);
        }
    }

    static class SlotFilter
            extends Slot
    {
        SlotFilter(AssemblyFilterInventory filter, int id, int x, int y) {
            super(filter, id, x, y);
        }

        @Override
        public boolean mayPickup(@Nonnull PlayerEntity player) {
            this.set(ItemStack.EMPTY);
            return false;
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            if( ItemStackUtils.isValid(stack) ) {
                this.set(stack.copy());
            }
            return false;
        }
    }
}
