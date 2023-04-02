/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.turretmod.api.ammo.IAmmunition;
import dev.sanandrea.mods.turretmod.api.tcu.TcuContainer;
import dev.sanandrea.mods.turretmod.api.turret.ITargetProcessor;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.entity.turret.TargetProcessor;
import dev.sanandrea.mods.turretmod.inventory.AmmoCartridgeInventory;
import dev.sanandrea.mods.turretmod.item.ammo.AmmoCartridgeItem;
import dev.sanandrea.mods.turretmod.item.ammo.AmmoItem;
import dev.sanandrea.mods.turretmod.item.ammo.AmmunitionRegistry;
import dev.sanandrea.mods.turretmod.item.repairkits.RepairKitItem;
import dev.sanandrea.mods.turretmod.network.SyncTurretStatePacket;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class TcuRemoteAccessContainer
        extends TcuContainer
{
    private static final int SLOT_IN_REPAIR_KIT = 0;
    private static final int SLOT_IN_AMMO       = 1;
    private static final int SLOT_OUT_AMMO      = 2;

    private final IInventory raInventory = new Inventory(3) {
        @Override
        public int getMaxStackSize() {
            return Integer.MAX_VALUE;
        }
    };

    public TcuRemoteAccessContainer(int windowId, PlayerInventory playerInventory, ITurretEntity turret, ResourceLocation currPage, boolean isRemote, boolean initial) {
        super(windowId, playerInventory, turret, currPage, isRemote, initial);

        this.addSlot(new InputSlot(this.raInventory, SLOT_IN_REPAIR_KIT, 26, 41,
                                   s -> s.getItem() instanceof RepairKitItem,
                                   () -> this.slotsChanged(this.raInventory)));
        this.addSlot(new InputSlot(this.raInventory, SLOT_IN_AMMO, 134, 41,
                                   s -> s.getItem() instanceof AmmoItem || s.getItem() instanceof AmmoCartridgeItem,
                                   () -> this.slotsChanged(this.raInventory)));
        this.addSlot(new AmmoSlot(116, 59));
        this.addSlot(new OutputSlot(this.raInventory, SLOT_OUT_AMMO, 134, 77,
                                    () -> this.slotsChanged(this.raInventory)));

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 126 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 184));
        }
    }

    @Override
    protected boolean moveItemStackTo(@Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        return InventoryUtils.mergeItemStack(this, stack, beginSlot, endSlot, reverse);
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotId) {
        ItemStack origStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if( slot != null && slot.hasItem() ) {
            ItemStack slotStack = slot.getItem();
            origStack = slotStack.copy();

            if( this.moveStack(slotId, slotStack, player) ) {
                return ItemStack.EMPTY;
            }

            if( InventoryUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
            }
        }

        return origStack;
    }

    private boolean moveStack(int slotId, ItemStack slotStack, PlayerEntity player) {
        if( slotId > 3 ) {
            return !this.moveItemStackTo(slotStack, 0, 2, false);
        } else if( slotId == SLOT_OUT_AMMO ) {
            return !AmmoCartridgeItem.putAmmoInPlayerCartridge(slotStack, player)
                   && !super.moveItemStackTo(slotStack, 3, 39, true);
        } else {
            return !super.moveItemStackTo(slotStack, 3, 39, true);
        }
    }

    @Override
    public void slotsChanged(@Nonnull IInventory inventory) {
        if( this.turret.get().level.isClientSide ) {
            super.slotsChanged(inventory);
            return;
        }

        ServerPlayerEntity playerMP = (ServerPlayerEntity) this.playerInventory.player;

        boolean syncContainer = false;

        if( this.turret.applyRepairKit(this.raInventory.getItem(SLOT_IN_REPAIR_KIT)) ) {
            this.raInventory.removeItem(SLOT_IN_REPAIR_KIT, 1);
            syncContainer = true;
        }

        ItemStack ammoInput = this.raInventory.getItem(SLOT_IN_AMMO).copy();
        if( ItemStackUtils.isValid(ammoInput) ) {
            TargetProcessor processor  = (TargetProcessor) this.turret.getTargetProcessor();
            boolean         syncTurret = false;
            if( applyAmmo(ammoInput, processor) || applyCartridge(ammoInput, processor) ) {
                syncTurret = true;
                syncContainer = true;
            }

            if( syncTurret ) {
                this.turret.syncState(SyncTurretStatePacket.AMMO);
            }
        }

        if( syncContainer ) {
            playerMP.slotChanged(this, SLOT_IN_REPAIR_KIT, this.raInventory.getItem(SLOT_IN_REPAIR_KIT));
            playerMP.slotChanged(this, SLOT_IN_AMMO, this.raInventory.getItem(SLOT_IN_AMMO));
            playerMP.slotChanged(this, SLOT_OUT_AMMO, this.raInventory.getItem(SLOT_OUT_AMMO));
        }

        super.slotsChanged(inventory);
    }

    private boolean applyAmmo(ItemStack input, TargetProcessor processor) {
        if( !(input.getItem() instanceof AmmoItem) ) {
            return false;
        }

        ITargetProcessor.ApplyType ammoApplyType = processor.getAmmoApplyType(input);

        if( ammoApplyType == ITargetProcessor.ApplyType.REPLACE ) {
            if( this.extractAmmo(processor) ) {
                int amt = input.getCount();
                input.setCount(1);
                processor.setAmmoStackInternal(input, amt);

                this.raInventory.setItem(SLOT_IN_AMMO, ItemStack.EMPTY);

                return true;
            }
        } else if( ammoApplyType == ITargetProcessor.ApplyType.ADD && processor.addAmmo(input) ) {
            this.raInventory.setItem(SLOT_IN_AMMO, input);

            return true;
        }

        return false;
    }

    private boolean applyCartridge(ItemStack input, TargetProcessor processor) {
        if( !(input.getItem() instanceof AmmoCartridgeItem) ) {
            return false;
        }

        AmmoCartridgeInventory inv = AmmoCartridgeItem.getInventory(input);
        if( inv != null && !inv.isEmpty() ) {
            if( this.grabAmmo(processor, inv, input) ) {
                return true;
            } else if( processor.getAmmoApplyType(inv.getAmmoTypeItem()) == ITargetProcessor.ApplyType.REPLACE
                       && this.extractAmmo(processor) )
            {
                processor.setAmmoStackInternal(ItemStack.EMPTY, 0);
                this.grabAmmo(processor, inv, input);

                return true;
            }
        }

        return false;
    }

    private boolean extractAmmo(ITargetProcessor processor) {
        ItemStack ammoTurret = processor.getAmmoStack();
        ItemStack ammoOutput = this.raInventory.getItem(SLOT_OUT_AMMO);
        IAmmunition type = AmmunitionRegistry.INSTANCE.get(ammoTurret);

        if( type.isValid() ) {
            ammoTurret.setCount(processor.getAmmoCount() / type.getCapacity());

            if( !ItemStackUtils.isValid(ammoOutput) ) {
                this.raInventory.setItem(SLOT_OUT_AMMO, ammoTurret);

                return true;
            } else if( ItemStackUtils.areEqual(ammoTurret, ammoOutput) ) {
                ammoOutput.grow(ammoTurret.getCount());
                this.raInventory.setItem(SLOT_OUT_AMMO, ammoOutput);

                return true;
            }
        }

        return false;
    }

    private boolean grabAmmo(ITargetProcessor processor, AmmoCartridgeInventory cartridgeInv, ItemStack cartridge) {
        if( AmmoCartridgeItem.extractAmmoStacks(cartridge, processor, false) ) {
            if( cartridgeInv.isEmpty() && !ItemStackUtils.isValid(this.raInventory.getItem(SLOT_OUT_AMMO)) ) {
                this.raInventory.setItem(SLOT_OUT_AMMO, cartridge);
                this.raInventory.setItem(SLOT_IN_AMMO, ItemStack.EMPTY);
            } else {
                this.raInventory.setItem(SLOT_IN_AMMO, cartridge);
            }

            return true;
        }

        return false;
    }

    @Override
    public void removed(PlayerEntity player) {
        if( !player.level.isClientSide ) {
            dropStacksOnClose(this.raInventory, player);
        }

        super.removed(player);
    }

    private static void dropStacksOnClose(IInventory inv, PlayerEntity player) {
        for( int i = 0, max = inv.getContainerSize(); i < max; i++ ) {
            ItemStack stack = inv.getItem(i);
            if( ItemStackUtils.isValid(stack) ) {
                player.level.addFreshEntity(new ItemEntity(player.level, player.position().x, player.position().y, player.position().z, stack.copy()));
            }
        }
    }

    private class AmmoSlot
            extends Slot
    {
        private AmmoSlot(int x, int y) {
            super(TcuRemoteAccessContainer.this.raInventory, -1, x, y);
        }

        @Nonnull
        @Override
        public ItemStack getItem() {
            ITargetProcessor processor = TcuRemoteAccessContainer.this.turret.getTargetProcessor();
            ItemStack stack = processor.getAmmoStack();
            IAmmunition type = AmmunitionRegistry.INSTANCE.get(stack);

            if( type.isValid() ) {
                stack.setCount(processor.getAmmoCount() / type.getCapacity());
                return stack;
            }

            return ItemStack.EMPTY;
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
        public void set(@Nonnull ItemStack stack) { /* no-op */ }


        @Nonnull
        @Override
        public ItemStack remove(int slot) {
            return ItemStack.EMPTY;
        }
    }
}
