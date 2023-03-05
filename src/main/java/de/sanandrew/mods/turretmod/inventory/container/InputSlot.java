package de.sanandrew.mods.turretmod.inventory.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Function;

class InputSlot
        extends Slot
{
    private final Function<ItemStack, Boolean> checker;
    private final Runnable                     onChanged;

    InputSlot(IInventory inv, int id, int x, int y, Function<ItemStack, Boolean> checker, Runnable onChanged) {
        super(inv, id, x, y);

        this.checker = checker;
        this.onChanged = onChanged;
    }

    @Override
    public void setChanged() {
        this.onChanged.run();

        super.setChanged();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return this.checker.apply(stack);
    }
}
