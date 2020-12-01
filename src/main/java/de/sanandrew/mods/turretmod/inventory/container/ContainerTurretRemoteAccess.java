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
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.item.ItemAmmoCartridge;
import de.sanandrew.mods.turretmod.item.ItemRepairKit;
import de.sanandrew.mods.turretmod.util.TmrUtils;
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
    private static final int SLOT_IN_REPAIR_KIT = 0;
    private static final int SLOT_IN_AMMO  = 1;
    private static final int SLOT_OUT_AMMO = 0;

    private final ITurretInst       turretInst;
    private final EntityPlayer      player;
    private final IInventory invInput = new InventoryInput(this);
    private final IInventory invOutput = new InventoryOutput();

    public ContainerTurretRemoteAccess(InventoryPlayer playerInv, ITurretInst turretInst) {
        this.turretInst = turretInst;
        this.player = playerInv.player;

        this.addSlotToContainer(new SlotInput(this.invInput, SLOT_IN_REPAIR_KIT, 26, 40, s -> s.getItem() instanceof ItemRepairKit));
        this.addSlotToContainer(new SlotInput(this.invInput, SLOT_IN_AMMO, 134, 40, s -> s.getItem() instanceof ItemAmmo
                                                                                         || s.getItem() instanceof ItemAmmoCartridge));
        this.addSlotToContainer(new SlotOutput(this.invOutput, SLOT_OUT_AMMO, 134, 76));

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
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if( this.player.world.isRemote ) {
            super.onCraftMatrixChanged(inventoryIn);
            return;
        }

        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        ITurretInst turretInst = ContainerTurretRemoteAccess.this.turretInst;

        boolean syncContainer = false;

        if( turretInst.applyRepairKit(this.invInput.getStackInSlot(SLOT_IN_REPAIR_KIT)) ) {
            this.invInput.decrStackSize(SLOT_IN_REPAIR_KIT, 1);
            syncContainer = true;
        }

        ItemStack ammoInput = this.invInput.getStackInSlot(SLOT_IN_AMMO).copy();
        if( ItemStackUtils.isValid(ammoInput) ) {
            ITargetProcessor tgtProc = turretInst.getTargetProcessor();
            boolean syncTurret = false;
            if( ammoInput.getItem() instanceof ItemAmmo ) {
                ITargetProcessor.ApplyType ammoApplyType = tgtProc.getAmmoApplyType(ammoInput);

                if( ammoApplyType == ITargetProcessor.ApplyType.REPLACE ) {
                    ItemStack ammoIntern = tgtProc.getAmmoStack();
                    ItemStack ammoOutput = this.invOutput.getStackInSlot(SLOT_OUT_AMMO);

                    ammoIntern.setCount(tgtProc.getAmmoCount());

                    boolean success = false;
                    if( !ItemStackUtils.isValid(ammoOutput) ) {
                        this.invOutput.setInventorySlotContents(SLOT_OUT_AMMO, ammoIntern);

                        success = true;
                    } else if( ItemStackUtils.areEqual(ammoIntern, ammoOutput) ) {
                        ammoOutput.grow(ammoIntern.getCount());
                        this.invOutput.setInventorySlotContents(SLOT_OUT_AMMO, ammoOutput);

                        success = true;
                    }

                    if( success ) {
                        int amt = ammoInput.getCount();
                        ammoInput.setCount(1);
                        ((TargetProcessor) tgtProc).setAmmoStackInternal(ammoInput, amt);

                        this.invInput.setInventorySlotContents(SLOT_IN_AMMO, ItemStack.EMPTY);

                        syncTurret = true;
                        syncContainer = true;
                    }
                } else if( ammoApplyType == ITargetProcessor.ApplyType.ADD ) {
                    if( tgtProc.addAmmo(ammoInput) ) {
                        this.invInput.setInventorySlotContents(SLOT_IN_AMMO, ammoInput);

                        syncTurret = true;
                        syncContainer = true;
                    }
                }
            } else if( ammoInput.getItem() instanceof ItemAmmoCartridge ) {
                AmmoCartridgeInventory inv = ItemAmmoCartridge.getInventory(ammoInput);
                if( inv != null && !inv.isEmpty() ) {
                    if( ItemAmmoCartridge.extractAmmoStacks(ammoInput, tgtProc, false) ) {
                        if( inv.isEmpty() ) {
                            this.invOutput.setInventorySlotContents(SLOT_OUT_AMMO, ammoInput);
                            this.invInput.setInventorySlotContents(SLOT_IN_AMMO, ItemStack.EMPTY);
                        } else {
                            this.invInput.setInventorySlotContents(SLOT_IN_AMMO, ammoInput);
                        }

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
            playerMP.sendSlotContents(this, SLOT_IN_REPAIR_KIT, this.invInput.getStackInSlot(SLOT_IN_REPAIR_KIT));
            playerMP.sendSlotContents(this, SLOT_IN_AMMO, this.invInput.getStackInSlot(SLOT_IN_AMMO));
            playerMP.sendSlotContents(this, 2 + SLOT_OUT_AMMO, this.invOutput.getStackInSlot(SLOT_OUT_AMMO));
        }

        super.onCraftMatrixChanged(inventoryIn);
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
            ItemStack stack = ContainerTurretRemoteAccess.this.turretInst.getTargetProcessor().getAmmoStack();
            stack.setCount(ContainerTurretRemoteAccess.this.turretInst.getTargetProcessor().getAmmoCount());
            return stack;
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
