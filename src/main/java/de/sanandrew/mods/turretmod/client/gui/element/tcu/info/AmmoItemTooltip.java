/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.element.tcu.info;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.StackPanel;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class AmmoItemTooltip
        extends Tooltip
{
    private Supplier<ItemStack> itemSupplier = () -> ItemStack.EMPTY;

    private ItemStack currItem = ItemStack.EMPTY;

    public AmmoItemTooltip(int[] mouseOverSize, int backgroundColor, int borderTopColor, int borderBottomColor, int[] padding, String visibleForId, GuiElementInst content) {
        super(mouseOverSize, backgroundColor, borderTopColor, borderBottomColor, padding, visibleForId, content);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.buildTooltip(gui, false);

        super.setup(gui, inst);
    }

    public void setItemSupplier(@Nonnull Supplier<ItemStack> supplier) {
        this.itemSupplier = supplier;
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        this.buildTooltip(gui, true);

        super.tick(gui, inst);
    }

    private void buildTooltip(IGui gui, boolean update) {
        ItemStack prevItem = this.currItem;
        this.currItem = this.itemSupplier.get();

        if( !ItemStackUtils.areEqual(prevItem, this.currItem) ) {
            ClientProxy.buildItemTooltip(gui, this.get(CONTENT).get(StackPanel.class), this.currItem, false, update);
        }
    }

    public static class Builder
            extends Tooltip.Builder
    {
        public Builder(int[] mouseOverSize) {
            super(mouseOverSize);
        }

        @Override
        public AmmoItemTooltip get(IGui gui) {
            this.sanitize(gui);

            return new AmmoItemTooltip(this.mouseOverSize, this.backgroundColor, this.borderTopColor, this.borderBottomColor, this.padding, this.visibleForId,
                                       this.content.initialize(gui));
        }

        @Override
        protected GuiElementInst loadContent(IGui gui, JsonObject data) {
            return new GuiElementInst(new StackPanel(null, false));
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Tooltip.Builder tb = Tooltip.Builder.buildFromJson(gui, data, null);
            Builder b = IBuilder.copyValues(tb, new Builder(tb.mouseOverSize));
            b.content(b.loadContent(gui, data));

            return b;
        }

        public static AmmoItemTooltip fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
