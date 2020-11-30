/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.inventory.TurretCrateInventory;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerTurretCrate
        extends Container
{
    private TileEntityTurretCrate tile;

    private SlotAmmo ammoSlot;

    public ContainerTurretCrate(IInventory playerInv, TileEntityTurretCrate crate) {
        this.tile = crate;

        IInventory tileInv = this.tile.getInventory();

        this.addSlotToContainer(new SlotOutput(tileInv, 0, 8, 18));

        for( int i = 0; i < TurretCrateInventory.SIZE_UPGRADE_STORAGE; i++ ) {
            int row = i / 9;
            int col = i % 9;
            this.addSlotToContainer(new SlotOutput(tileInv, i + 1, 8 + col * 18, 44 + row * 18));
        }

        this.addSlotToContainer(this.ammoSlot = new SlotAmmo(tileInv, TurretCrateInventory.SLOT_AMMO, 62, 18));

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 129 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 187));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tile.getInventory().isUsableByPlayer(playerIn);
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

            if( slotId < TurretCrateInventory.SLOT_AMMO ) { // if clicked stack is from TileEntity
                if( !super.mergeItemStack(slotStack, TurretCrateInventory.SLOT_AMMO + 1, TurretCrateInventory.SLOT_AMMO + 1 + 36, true) ) { // merge into player inventory
                    return ItemStack.EMPTY;
                }
            } else if( slotId == TurretCrateInventory.SLOT_AMMO ) {// if clicked stack is from ammo slot
                if( !ItemAmmoCartridge.putAmmoInPlayerCartridge(slotStack, player)
                    && !super.mergeItemStack(slotStack, TurretCrateInventory.SLOT_AMMO + 1, TurretCrateInventory.SLOT_AMMO + 1 + 36, true) )
                {
                    return ItemStack.EMPTY;
                }
            }

            if( TmrUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    public SlotAmmo getAmmoSlot() {
        return this.ammoSlot;
    }

    public static final class SlotAmmo
            extends SlotOutput
    {
        public boolean isRendering = false;

        SlotAmmo(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public ItemStack getStack() {
            if( this.isRendering ) {
                ItemStack stack = super.getStack().copy();
                stack.setCount(1);
                return stack;
            } else {
                return super.getStack();
            }
        }
    }
}
