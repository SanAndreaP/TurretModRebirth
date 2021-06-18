package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.turretmod.inventory.AmmoCartridgeInventory;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import de.sanandrew.mods.turretmod.item.ammo.AmmoCartridgeItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.IContainerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class AmmoCartridgeContainer
        extends Container
{
    private final AmmoCartridgeInventory cartridge;

    public AmmoCartridgeContainer(int id, PlayerInventory playerInv, AmmoCartridgeInventory cartridge) {
        super(ContainerRegistry.AMMO_CARTRIGE, id);

        this.cartridge = cartridge;
        cartridge.startOpen(playerInv.player);

        for( int row = 0; row < 3; ++row ) {
            for( int col = 0; col < 9; ++col ) {
                this.addSlot(new SlotCartridge(cartridge, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        for( int row = 0; row < 3; ++row ) {
            for( int col = 0; col < 9; ++col ) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for( int col = 0; col < 9; ++col ) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return this.cartridge.stillValid(player);
    }

    @Override
    protected boolean moveItemStackTo(@Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        return InventoryUtils.mergeItemStack(this, stack, beginSlot, endSlot, reverse);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotId) {
        ItemStack origStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if( slot != null && slot.hasItem() ) {
            ItemStack slotStack = slot.getItem();
            origStack = slotStack.copy();

            if( slotId < 27 ) { // if clicked stack is from TileEntity
                if( !super.moveItemStackTo(slotStack, 27, 63, true) ) {
                    return ItemStack.EMPTY;
                }
            } else if( !this.moveItemStackTo(slotStack, 0, 27, false) ) { // if clicked stack is from player and also merge to input slots is sucessful
                return ItemStack.EMPTY;
            }

            if( InventoryUtils.finishTransfer(player, origStack, slot, slotStack) ) {
                return ItemStack.EMPTY;
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
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return this.container.canPlaceItem(this.getSlotIndex(), stack);
        }
    }

    public static class Factory
            implements IContainerFactory<AmmoCartridgeContainer>
    {
        public static final AmmoCartridgeContainer.Factory INSTANCE = new AmmoCartridgeContainer.Factory();

        @Override
        public AmmoCartridgeContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            return new AmmoCartridgeContainer(windowId, inv, new AmmoCartridgeInventory(inv, data.readVarInt()));
        }
    }

    public static class Provider
            implements INamedContainerProvider
    {
        @Nonnull
        private final ItemStack ammoCartridge;

        public Provider(@Nonnull ItemStack ammoCartridge) {
            this.ammoCartridge = ammoCartridge;
        }

        @Nonnull
        @Override
        public ITextComponent getDisplayName() {
            return this.ammoCartridge.getDisplayName();
        }

        @Nullable
        @Override
        public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
            return new AmmoCartridgeContainer(id, playerInventory, Objects.requireNonNull(AmmoCartridgeItem.getInventory(this.ammoCartridge)));
        }
    }
}
