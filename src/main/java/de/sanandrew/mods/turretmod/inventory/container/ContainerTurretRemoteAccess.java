/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import de.sanandrew.mods.turretmod.item.ItemRepairKit;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ContainerTurretRemoteAccess
        extends Container
{
    private static final int SLOT_INV_IN_REPAIR_KIT = 0;
    private static final int SLOT_CNT_IN_REPAIR_KIT = 0;
    private static final int SLOT_INV_IN_AMMO  = 1;
    private static final int SLOT_CNT_IN_AMMO  = 1;
    private static final int SLOT_INV_OUT_AMMO = 0;
    private static final int SLOT_CNT_OUT_AMMO = 2;

    private final ITurretInst       turretInst;
    private final EntityPlayer      player;
    private final IInventory invInput = new InventoryInput(this);
    private final IInventory invOutput = new InventoryOutput();

    public ContainerTurretRemoteAccess(InventoryPlayer playerInv, ITurretInst turretInst) {
        this.turretInst = turretInst;
        this.player = playerInv.player;

        this.addSlotToContainer(new SlotInput(this.invInput, SLOT_INV_IN_REPAIR_KIT, 26, 40, s -> s.getItem() instanceof ItemRepairKit));
        this.addSlotToContainer(new SlotInput(this.invInput, SLOT_INV_IN_AMMO, 134, 40, s -> s.getItem() instanceof ItemAmmo
                                                                                             || s.getItem() instanceof ItemAmmoCartridge));
        this.addSlotToContainer(new SlotOutput(this.invOutput, SLOT_INV_OUT_AMMO, 134, 76));

        for( int i = 0; i < 3; i++ ) {
            for( int j = 0; j < 9; j++ ) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }

        for( int i = 0; i < 9; i++ ) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 183));
        }

        this.addSlotToContainer(new SlotAmmo(116, 58));
    }

    @Override
    protected boolean mergeItemStack(@Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        return InventoryUtils.mergeItemStack(this, stack, beginSlot, endSlot, reverse);
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
            } else if( slotId == SLOT_CNT_OUT_AMMO ) {// if clicked stack is from ammo slot
                if( !ItemAmmoCartridge.putAmmoInPlayerCartridge(slotStack, player)
                    && !super.mergeItemStack(slotStack, 3, 39, true) )
                {
                    return ItemStack.EMPTY;
                }
            } else {
                if( !super.mergeItemStack(slotStack, 3, 39, true) ) {
                    return ItemStack.EMPTY;
                }
            }

            if( InventoryUtils.finishTransfer(player, origStack, slot, slotStack) ) {
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
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if( this.player.world.isRemote ) {
            super.onCraftMatrixChanged(inventoryIn);
            return;
        }

        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        ITurretInst turretInst = ContainerTurretRemoteAccess.this.turretInst;

        boolean syncContainer = false;

        if( turretInst.applyRepairKit(this.invInput.getStackInSlot(SLOT_INV_IN_REPAIR_KIT)) ) {
            this.invInput.decrStackSize(SLOT_INV_IN_REPAIR_KIT, 1);
            syncContainer = true;
        }

        ItemStack ammoInput = this.invInput.getStackInSlot(SLOT_INV_IN_AMMO).copy();
        if( ItemStackUtils.isValid(ammoInput) ) {
            ITargetProcessor processor = turretInst.getTargetProcessor();
            boolean syncTurret = false;
            if( ammoInput.getItem() instanceof ItemAmmo ) {
                ITargetProcessor.ApplyType ammoApplyType = processor.getAmmoApplyType(ammoInput);

                if( ammoApplyType == ITargetProcessor.ApplyType.REPLACE ) {
                    if( this.extractAmmo(processor) ) {
                        int amt = ammoInput.getCount();
                        ammoInput.setCount(1);
                        ((TargetProcessor) processor).setAmmoStackInternal(ammoInput, amt);

                        this.invInput.setInventorySlotContents(SLOT_INV_IN_AMMO, ItemStack.EMPTY);

                        syncTurret = true;
                        syncContainer = true;
                    }
                } else if( ammoApplyType == ITargetProcessor.ApplyType.ADD ) {
                    if( processor.addAmmo(ammoInput) ) {
                        this.invInput.setInventorySlotContents(SLOT_INV_IN_AMMO, ammoInput);

                        syncTurret = true;
                        syncContainer = true;
                    }
                }
            } else if( ammoInput.getItem() instanceof ItemAmmoCartridge ) {
                AmmoCartridgeInventory inv = ItemAmmoCartridge.getInventory(ammoInput);
                if( inv != null && !inv.isEmpty() ) {
                    if( this.grabAmmo(processor, inv, ammoInput) ) {
                        syncTurret = true;
                        syncContainer = true;
                    } else if( processor.getAmmoApplyType(inv.getAmmoTypeItem()) == ITargetProcessor.ApplyType.REPLACE
                               && this.extractAmmo(processor) )
                    {
                        ((TargetProcessor) processor).setAmmoStackInternal(ItemStack.EMPTY, 0);
                        this.grabAmmo(processor, inv, ammoInput);

                        syncTurret = true;
                        syncContainer = true;
                    }
                }
            }

            if( syncTurret ) {
                this.turretInst.updateState();
            }
        }

        if( syncContainer ) {
            playerMP.sendSlotContents(this, SLOT_CNT_IN_REPAIR_KIT, this.invInput.getStackInSlot(SLOT_INV_IN_REPAIR_KIT));
            playerMP.sendSlotContents(this, SLOT_CNT_IN_AMMO, this.invInput.getStackInSlot(SLOT_INV_IN_AMMO));
            playerMP.sendSlotContents(this, SLOT_CNT_OUT_AMMO, this.invOutput.getStackInSlot(SLOT_INV_OUT_AMMO));
        }

        super.onCraftMatrixChanged(inventoryIn);
    }

    private boolean extractAmmo(ITargetProcessor processor) {
        ItemStack ammoTurret = processor.getAmmoStack();
        ItemStack ammoOutput = this.invOutput.getStackInSlot(SLOT_INV_OUT_AMMO);
        IAmmunition type = AmmunitionRegistry.INSTANCE.getObject(ammoTurret);

        if( type.isValid() ) {
            ammoTurret.setCount(processor.getAmmoCount() / type.getAmmoCapacity());

            if( !ItemStackUtils.isValid(ammoOutput) ) {
                this.invOutput.setInventorySlotContents(SLOT_INV_OUT_AMMO, ammoTurret);

                return true;
            } else if( ItemStackUtils.areEqual(ammoTurret, ammoOutput) ) {
                ammoOutput.grow(ammoTurret.getCount());
                this.invOutput.setInventorySlotContents(SLOT_INV_OUT_AMMO, ammoOutput);

                return true;
            }
        }

        return false;
    }

    private boolean grabAmmo(ITargetProcessor processor, AmmoCartridgeInventory cartridgeInv, ItemStack cartridge) {
        if( ItemAmmoCartridge.extractAmmoStacks(cartridge, processor, false) ) {
            if( cartridgeInv.isEmpty() && !ItemStackUtils.isValid(this.invOutput.getStackInSlot(SLOT_INV_OUT_AMMO)) ) {
                this.invOutput.setInventorySlotContents(SLOT_INV_OUT_AMMO, cartridge);
                this.invInput.setInventorySlotContents(SLOT_INV_IN_AMMO, ItemStack.EMPTY);
            } else {
                this.invInput.setInventorySlotContents(SLOT_INV_IN_AMMO, cartridge);
            }

            return true;
        }

        return false;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        if( !player.world.isRemote ) {
            dropStacksOnClose(this.invInput, player);
            dropStacksOnClose(this.invOutput, player);
        }

        super.onContainerClosed(player);
    }

    private static void dropStacksOnClose(IInventory inv, EntityPlayer player) {
        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            ItemStack stack = inv.getStackInSlot(i);
            if( ItemStackUtils.isValid(stack) ) {
                player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ, stack.copy()));
            }
        }
    }

    private static class InventoryInput
            extends InventoryBasic
    {
        private final Container parent;
        private InventoryInput(Container parent) {
            super("Remote Access Input", true, 2);

            this.parent = parent;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
            super.setInventorySlotContents(index, stack);
            this.parent.onCraftMatrixChanged(this);
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            ItemStack remainder = super.decrStackSize(index, count);

            if( ItemStackUtils.isValid(remainder) ) {
                this.parent.onCraftMatrixChanged(this);
            }

            return remainder;
        }
    }

    private static class InventoryOutput
            extends InventoryBasic
    {
        private InventoryOutput() {
            super("Remote Access Output", true, 1);
        }

        @Override
        public int getInventoryStackLimit() {
            return Integer.MAX_VALUE;
        }
    }

    private static class SlotInput
            extends Slot
    {
        private final Function<ItemStack, Boolean> checker;

        private SlotInput(IInventory inv, int id, int x, int y, Function<ItemStack, Boolean> checker) {
            super(inv, id, x, y);

            this.checker = checker;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return this.checker.apply(stack);
        }
    }

    private class SlotAmmo
            extends Slot
    {
        @SuppressWarnings("ConstantConditions")
        private SlotAmmo(int x, int y) {
            super(null, -1, x, y);
        }

        @Override
        public ItemStack getStack() {
            ITargetProcessor processor = ContainerTurretRemoteAccess.this.turretInst.getTargetProcessor();
            ItemStack stack = processor.getAmmoStack();
            IAmmunition type = AmmunitionRegistry.INSTANCE.getObject(stack);
            if( type.isValid() ) {
                stack.setCount(processor.getAmmoCount() / type.getAmmoCapacity());
                return stack;
            }

            return ItemStack.EMPTY;
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
    }
}
