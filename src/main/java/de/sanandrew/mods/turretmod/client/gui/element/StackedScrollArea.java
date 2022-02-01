package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.common.collect.Range;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollButton;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class StackedScrollArea
        extends ScrollArea
{
    public StackedScrollArea(int[] areaSize, int scrollHeight, boolean rasterized, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, IGui gui) {
        super(areaSize, scrollHeight, rasterized, maxScrollDelta, scrollbarPos, scrollButton, gui);
    }

    @Override
    public void update(IGui gui) {
        MutableInt           updatePosY    = new MutableInt(0);
        List<GuiElementInst> elementsIntrn = new ArrayList<>();
        elementsIntrn.addAll(this.elements.asMapOfRanges().values());
        elementsIntrn.addAll(this.prebuiltElements);

        if( !this.prebuiltElements.isEmpty() ) {
            this.prebuiltElements.forEach(e -> e.get().setup(gui, e));
        }

        this.elements.clear();
        this.prebuiltElements.clear();

        elementsIntrn = this.sortAndFilter(elementsIntrn);

        elementsIntrn.forEach(e -> {
            int newPosY = e.pos[1] + updatePosY.getValue();
            int elemHeight = e.get().getHeight();
            this.elements.put(Range.closed(newPosY, newPosY + elemHeight), e);
            updatePosY.add(e.pos[1] + elemHeight);
        });

        this.update();
    }

    protected List<GuiElementInst> sortAndFilter(List<GuiElementInst> elements) {
        return elements;
    }

    @Override
    protected void renderElements(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        final int correctedY = y + this.sData.minY;
        MutableInt updatePosY = new MutableInt(0);

        this.doWorkV(e -> {
            int newPosY = correctedY + e.pos[1] + updatePosY.getValue();
            GuiDefinition.renderElement(gui, stack, x + e.pos[0], newPosY, mouseX, mouseY, partTicks, e, true);
            updatePosY.add(e.pos[1] + e.get().getHeight());
        });
    }

    public static class Builder
            extends ScrollArea.Builder
    {
        public Builder(int[] areaSize) {
            super(areaSize);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data, b -> ((StackedScrollArea.Builder) b)::loadElements);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, Function<ScrollArea.Builder, BiFunction<IGui, JsonElement, GuiElementInst[]>> loadElementsFunc) {
            ScrollArea.Builder sab = ScrollArea.Builder.buildFromJson(gui, data, loadElementsFunc);
            return IBuilder.copyValues(sab, new Builder(sab.areaSize));
        }
    }
}
