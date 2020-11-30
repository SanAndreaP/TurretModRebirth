/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerTurretRemoteAccess
        extends Container
{
    private final ITurretInst turretInst;

    public ContainerTurretRemoteAccess(IInventory playerInv, ITurretInst turretInst) {
        this.turretInst = turretInst;

        this.addSlotToContainer(new SlotAmmo(0, 26, 58));
        this.addSlotToContainer(new SlotRepairKit(1, 26, 94));

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 183));
        }
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

            if( slotId >= 2 ) {
                if( this.inventorySlots.get(0).isItemValid(slotStack) ) {
                    //TODO: handle ammo insertion
                } else if( this.inventorySlots.get(1).isItemValid(slotStack) && this.turretInst.applyRepairKit(slotStack) ) {
                    slotStack.shrink(1);
                } else {
                    return ItemStack.EMPTY;
                }


//                if( !this.mergeItemStack(slotStack, 0, 2, false) ) {
//                    return ItemStack.EMPTY;
//                }
            }

            if( TmrUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    private static abstract class SlotEmpty
            extends Slot
    {
        @SuppressWarnings("ConstantConditions")
        private SlotEmpty(int id, int x, int y) {
            super(null, id, x, y);
        }

        @Override
        public void putStack(ItemStack stack) { }

        @Override
        public void onSlotChanged() { }

        @Override
        public ItemStack decrStackSize(int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return false;
        }

        abstract public ItemStack getStack();

        abstract public boolean isItemValid(ItemStack stack);

        abstract public int getSlotStackLimit();
    }

    private class SlotAmmo
            extends SlotEmpty
    {
        private SlotAmmo(int id, int x, int y) {
            super(id, x, y);
        }

        @Override
        public ItemStack getStack() {
            return ContainerTurretRemoteAccess.this.turretInst.getTargetProcessor().getAmmoStack();
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return ContainerTurretRemoteAccess.this.turretInst.getTargetProcessor().isAmmoApplicable(stack);
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }
    }

    private class SlotRepairKit
            extends SlotEmpty
    {
        private SlotRepairKit(int id, int x, int y) {
            super(id, x, y);
        }

        @Override
        public ItemStack getStack() {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            return RepairKitRegistry.INSTANCE.getObject(stack).isApplicable(ContainerTurretRemoteAccess.this.turretInst);
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }
}
