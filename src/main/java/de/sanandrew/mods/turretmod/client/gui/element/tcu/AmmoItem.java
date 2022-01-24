package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class AmmoItem
        extends Item
{
    private Supplier<ItemStack> itemSupplier = () -> ItemStack.EMPTY;

    private ItemStack currItem = ItemStack.EMPTY;

    public AmmoItem(float scale) {
        super(ItemStack.EMPTY, scale);
    }

    public void setItemSupplier(@Nonnull Supplier<ItemStack> supplier) {
        this.itemSupplier = supplier;
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        super.tick(gui, inst);

        this.currItem = this.itemSupplier.get();
    }

    @Override
    protected ItemStack getDynamicStack(IGui gui) {
        return this.currItem;
    }

    public static class Builder
            extends Item.Builder
    {
        public Builder() {
            super(ItemStack.EMPTY);
        }

        @Override
        public AmmoItem get(IGui gui) {
            this.sanitize(gui);

            return new AmmoItem(this.scale);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return IBuilder.copyValues(Item.Builder.buildFromJson(gui, data, (g, j) -> ItemStack.EMPTY), new Builder());
        }

        public static AmmoItem fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
