/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.item.upgrades.UpgradeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class TcuUpgradesContainer
        extends TcuContainer
{
    public TcuUpgradesContainer(int windowId, PlayerInventory playerInventory, ITurretEntity turret, ResourceLocation currPage, boolean isRemote, boolean initial) {
        super(windowId, playerInventory, turret, currPage, isRemote, initial);
        IUpgradeProcessor proc = turret.getUpgradeProcessor();

        checkContainerSize(proc, UpgradeProcessor.SLOTS);

        for( int i = 0; i < UpgradeProcessor.SLOTS; ++i ) {
            this.addSlot(new SlotUpgrade(proc, i, 8 + 18 * (i % 9), 41 + 18 * (i / 9)));
        }

        for( int i = 0; i < 27; ++i ) {
            this.addSlot(new Slot(playerInventory, 9 + i, 8 + 18 * (i % 9), 126 + 18 * (i / 9)));
        }

        for( int i = 0; i < 9; ++i ) {
            this.addSlot(new Slot(playerInventory, i, 8 + 18 * i, 184));
        }
    }

    @Override
    protected boolean moveItemStackTo(@Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        return InventoryUtils.mergeItemStack(this, stack, beginSlot, endSlot, reverse);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotId) {
        ItemStack origStack = ItemStack.EMPTY;
        Slot      slot      = this.slots.get(slotId);

        if( slot != null && slot.hasItem() ) {
            ItemStack slotStack = slot.getItem();
            origStack = slotStack.copy();

            if( slotId < UpgradeProcessor.SLOTS ) { // if clicked stack is from TileEntity
                if( !InventoryUtils.mergeItemStack(this, slotStack, UpgradeProcessor.SLOTS, UpgradeProcessor.SLOTS + 36, true, true,
                                                   (ois, nis, s) -> this.onSlotChange(nis, s, slotId)) )
                { // merge into player inventory
                    return ItemStack.EMPTY;
                }
            } else if( !InventoryUtils.mergeItemStack(this, slotStack, 0, UpgradeProcessor.SLOTS, false, false,
                                                      (ois, nis, s) -> this.onSlotChange(nis, s, slotId)) )
            { // if clicked stack is from player and also merge to input slots is sucessful
                return ItemStack.EMPTY;
            }

            if( InventoryUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    @SuppressWarnings("java:S2184")
    private void onSlotChange(ItemStack newStack, Slot slot, int currSlotId) {
        int newSlotId = slot.index;

        if( currSlotId < UpgradeProcessor.SLOTS && MiscUtils.between(UpgradeProcessor.SLOTS, newSlotId, UpgradeProcessor.SLOTS + 35)
            && UpgradeRegistry.INSTANCE.get(newStack).isValid() )
        {
            ((UpgradeProcessor) this.turret.getUpgradeProcessor()).terminate(currSlotId, newStack);
        }
    }

    private static class SlotUpgrade
            extends Slot
    {
        public SlotUpgrade(IUpgradeProcessor proc, int id, int x, int y) {
            super(proc, id, x, y);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return this.container.canPlaceItem(this.index, stack);
        }
    }
}
