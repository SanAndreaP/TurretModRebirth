/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import de.sanandrew.mods.turretmod.inventory.TurretCrateInventory;
import de.sanandrew.mods.turretmod.item.ammo.AmmoCartridgeItem;
import de.sanandrew.mods.turretmod.tileentity.TurretCrateEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.IContainerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TurretCrateContainer
        extends Container
{
    private final TurretCrateEntity crate;

    private final SlotAmmo ammoSlot;

    public TurretCrateContainer(int windowId, IInventory playerInv, TurretCrateEntity crate) {
        super(ContainerRegistry.TURRET_CRATE, windowId);

        this.crate = crate;

        IInventory tileInv = this.crate.getInventory();

        this.addSlot(new OutputSlot(tileInv, 0, 8, 18));

        for( int i = 0; i < TurretCrateInventory.SIZE_UPGRADE_STORAGE; i++ ) {
            int row = i / 9;
            int col = i % 9;
            this.addSlot(new OutputSlot(tileInv, i + 1, 8 + col * 18, 44 + row * 18));
        }

        this.addSlot(this.ammoSlot = new SlotAmmo(tileInv, TurretCrateInventory.SLOT_AMMO, 62, 18));

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 129 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 187));
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return this.crate.getInventory().stillValid(player);
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

            if( slotId < TurretCrateInventory.SLOT_AMMO ) { // if clicked stack is from TileEntity
                if( !super.moveItemStackTo(slotStack, TurretCrateInventory.SLOT_AMMO + 1, TurretCrateInventory.SLOT_AMMO + 1 + 36, true) ) { // merge into player inventory
                    return ItemStack.EMPTY;
                }
            } else if( slotId == TurretCrateInventory.SLOT_AMMO ) {// if clicked stack is from ammo slot
                if( !AmmoCartridgeItem.putAmmoInPlayerCartridge(slotStack, player)
                    && !super.moveItemStackTo(slotStack, TurretCrateInventory.SLOT_AMMO + 1, TurretCrateInventory.SLOT_AMMO + 1 + 36, true) )
                {
                    return ItemStack.EMPTY;
                }
            }

            if( InventoryUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    public TurretCrateEntity getEntity() {
        return this.crate;
    }

    public SlotAmmo getAmmoSlot() {
        return this.ammoSlot;
    }

    public static class Factory
            implements IContainerFactory<TurretCrateContainer>
    {
        public static final TurretCrateContainer.Factory INSTANCE = new TurretCrateContainer.Factory();

        @Override
        public TurretCrateContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            TileEntity te = Objects.requireNonNull(inv.player.level.getBlockEntity(data.readBlockPos()));
            return new TurretCrateContainer(windowId, inv, (TurretCrateEntity) te);
        }
    }

    public static class OutputSlot
            extends Slot
    {
        OutputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return false;
        }
    }

    public static final class SlotAmmo
            extends OutputSlot
    {
        public boolean isRendering = false;

        SlotAmmo(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Nonnull
        @Override
        public ItemStack getItem() {
            if( this.isRendering ) {
                ItemStack stack = super.getItem().copy();
                stack.setCount(1);
                return stack;
            } else {
                return super.getItem();
            }
        }
    }
}
