package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.common.collect.Range;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollButton;
import de.sanandrew.mods.sanlib.lib.client.gui.element.StackedScrollArea;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuInfoPage;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuScreen;
import de.sanandrew.mods.turretmod.client.init.TcuClientRegistry;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*

|        elementWidth = areaSize[0]         |
|-------------------------------------------|-
|                   name                  |O| elementHeight
|-----------------------------------------| |-
|                  health                 | |
|-----------------------------------------| |
|                   ammo                  | |
|-----------------------------------------| |
|                   etc.                  | |
--------------------------------------------|
 */

public class TcuInfo
        extends StackedScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_info");

    public TcuInfo(int[] areaSize, int scrollHeight, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, IGui gui, GuiElementInst[] elements) {
        super(areaSize, scrollHeight, true, maxScrollDelta, scrollbarPos, scrollButton, gui);

        Arrays.stream(elements).forEach(e -> this.add(e.initialize(gui)));
    }

    @Override
    protected List<GuiElementInst> sortAndFilter(List<GuiElementInst> elements) {
        return elements.stream().filter(GuiElementInst::isVisible).sorted(TcuClientRegistry::sortProviders).collect(Collectors.toList());
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst e) {
        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, e);

        MutableInt updatePosY = new MutableInt(0);
        this.doWorkV(ei -> {
            int newPosY = y + ei.pos[1] + updatePosY.getValue();
            if( newPosY + ei.get().getHeight() <= y + this.getHeight() ) {
                ei.get(TcuInfoValue.class).renderOutside(gui, stack, partTicks, x + ei.pos[0], newPosY, mouseX, mouseY);
            }
            updatePosY.add(ei.pos[1] + ei.get().getHeight());
        });
    }

    public static class Builder
            extends StackedScrollArea.Builder
    {
        protected int elementHeight = 16;

        public Builder(int[] areaSize) {
            super(areaSize);
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder elementHeight(int height) { this.elementHeight = height; return this; }

        @Override
        public TcuInfo get(IGui gui) {
            return new TcuInfo(this.areaSize, this.scrollHeight, this.maxScrollDelta, this.scrollbarPos, this.scrollButton, gui, this.elements);
        }

        @Nonnull
        @Override
        protected GuiElementInst[] loadElements(IGui gui, JsonElement je) {
            final List<ITcuInfoProvider> providers = TcuClientRegistry.getProviders();
            final JsonObject elemData = je.getAsJsonObject();
            final int w = this.areaSize[0];
            final int h = this.elementHeight;
            final ITurretEntity turret = ((TcuInfoPage) gui).getTurret();

            GuiElementInst[] elem = providers.stream().map(provider -> getInfoValue(gui, elemData, provider, turret, w, h))
                                             .toArray(GuiElementInst[]::new);
            Arrays.stream(elem).forEach(e -> e.initialize(gui));

            return elem;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            ScrollArea.Builder sab = ScrollArea.Builder.buildFromJson(gui, data, b -> (g, j) -> null);
            Builder b = IBuilder.copyValues(sab, new Builder(sab.areaSize));

            JsonUtils.fetchInt(data.get("elementHeight"), b::elementHeight);

            b.elements(b.loadElements(gui, MiscUtils.get(data.get("elementData"), JsonObject::new)));

            return b;
        }

        public static TcuInfo fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }

        private static GuiElementInst getInfoValue(IGui gui, JsonObject data, ITcuInfoProvider provider, ITurretEntity turret, int w, int h) {
            TcuInfoValue.Builder b = TcuInfoValue.Builder.buildFromJson(gui, data, provider, turret, w, h);

            return new GuiElementInst(new int[] { 0, 0 }, b.get(gui));
        }
    }
}
