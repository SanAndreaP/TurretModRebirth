package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerCartridge
        extends Container
{
    private final IInventory cartridge;

    public ContainerCartridge(InventoryPlayer playerInv, IInventory cartridge, EntityPlayer player) {
        this.cartridge = cartridge;
        cartridge.openInventory(player);

        for( int row = 0; row < 3; ++row ) {
            for( int col = 0; col < 9; ++col ) {
                this.addSlotToContainer(new SlotCartridge(cartridge, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        for( int row = 0; row < 3; ++row ) {
            for( int col = 0; col < 9; ++col ) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for( int col = 0; col < 9; ++col ) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.cartridge.isUsableByPlayer(playerIn);
    }


    @Override
    protected boolean mergeItemStack(@Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        return TmrUtils.mergeItemStack(this, stack, beginSlot, endSlot, reverse);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack origStack = ItemStackUtils.getEmpty();
        Slot slot = this.inventorySlots.get(slotId);

        if( slot != null && slot.getHasStack() ) {
            ItemStack slotStack = slot.getStack();
            origStack = slotStack.copy();

            if( slotId < 27 ) { // if clicked stack is from TileEntity
                if( !super.mergeItemStack(slotStack, 27, 63, true) ) {
                    return ItemStackUtils.getEmpty();
                }
            } else if( !this.mergeItemStack(slotStack, 0, 27, false) ) { // if clicked stack is from player and also merge to input slots is sucessful
                return ItemStackUtils.getEmpty();
            }

            if( TmrUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStackUtils.getEmpty();
            }
        }

        return origStack;
    }

    private static final class SlotCartridge
            extends Slot
    {

        SlotCartridge(IInventory inv, int index, int xPosition, int yPosition) {
            super(inv, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }
}
