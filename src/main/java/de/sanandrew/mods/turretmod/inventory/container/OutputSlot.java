package de.sanandrew.mods.turretmod.inventory.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class OutputSlot
        extends Slot
{
    private final Runnable onChanged;

    OutputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        this(inventoryIn, index, xPosition, yPosition, () -> { });
    }

    OutputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, @Nonnull Runnable onChanged) {
        super(inventoryIn, index, xPosition, yPosition);
        this.onChanged = onChanged;
    }

    @Override
    public void setChanged() {
        this.onChanged.run();

        super.setChanged();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }
}
