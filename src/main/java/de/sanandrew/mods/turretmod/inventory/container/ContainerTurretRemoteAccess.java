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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerTurretRemoteAccess
        extends Container
{
    private final ITurretInst turretInst;
    private final Inventory inv = new Inventory();

    public ContainerTurretRemoteAccess(IInventory playerInv, ITurretInst turretInst) {
        this.turretInst = turretInst;

        this.addSlotToContainer(new Slot(this.inv, 0, 26, 40));
        this.addSlotToContainer(new Slot(this.inv, 1, 134, 40));
        this.addSlotToContainer(new SlotOutput(this.inv, 2, 134, 76));

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 183));
        }

        //noinspection ConstantConditions
        this.addSlotToContainer(new Slot(null, -1, 116, 58) {
            @Override
            public ItemStack getStack() {
                return ContainerTurretRemoteAccess.this.turretInst.getTargetProcessor().getAmmoStack();
            }

            @Override
            public void putStack(ItemStack stack) { }

            @Override
            public void onSlotChanged() { }

            @Override
            public int getSlotStackLimit() {
                return this.getStack().getCount();
            }

            @Override
            public ItemStack decrStackSize(int amount) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return false;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
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

            if( slotId >= 3 ) {
                if( !this.mergeItemStack(slotStack, 0, 2, false) ) {
                    return ItemStack.EMPTY;
                }
            } else {
                if( !super.mergeItemStack(slotStack, 3, 39, true) ) {
                    return ItemStack.EMPTY;
                }
            }

            if( TmrUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        this.inv.closeInventory(playerIn);
        super.onContainerClosed(playerIn);
    }

    private class Inventory
            extends InventoryBasic
    {
        private static final int SIZE = 3;

        public Inventory() {
            super("Remote Access", true, SIZE);
        }

        @Override
        public void closeInventory(EntityPlayer player) {
            if( !player.world.isRemote ) {
                for( int i = 0; i < SIZE; i++ ) {
                    ItemStack stack = this.getStackInSlot(i);
                    if( ItemStackUtils.isValid(stack) ) {
                        player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ, stack.copy()));
                    }
                }
            }
        }
    }

//    private class SlotRepkitInput
//            extends Slot
//    {
//
//    }
}
