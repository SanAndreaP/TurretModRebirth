package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import net.minecraft.client.gui.AbstractGui;
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

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, inst);

        if( IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size, this.size) ) {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            AbstractGui.fill(stack, x, y, x + this.size, y + this.size, 0x80FFFFFF);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
        }
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
