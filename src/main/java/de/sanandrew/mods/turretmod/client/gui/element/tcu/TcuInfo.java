package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.client.init.TcuClientRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;
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
        extends ScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_info");

    private int elemHeight;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.elemHeight = JsonUtils.getIntVal(data.get("elementHeight"), 16);

        JsonUtils.addJsonProperty(data, "rasterized", true);

        super.bakeData(gui, data, inst);
    }

    @Override
    public GuiElementInst[] getElements(IGui gui, JsonObject data) {
        final List<ITcuInfoProvider> providers = TcuClientRegistry.getProviders();
        final JsonObject elemData = data.getAsJsonObject("elementData");
        final int w = this.areaSize[0];
        final int h = this.elemHeight;

        GuiElementInst[] elem = IntStream.range(0, providers.size())
                                         .mapToObj(i -> new GuiElementInst(new int[] { 0, h * i}, new TcuInfoValue(providers.get(i), w, h), elemData))
                                         .toArray(GuiElementInst[]::new);
        Arrays.stream(elem).forEach(e -> e.initialize(gui));

        return elem;
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, data);

        for( GuiElementInst c : this.getChildren() ) {
            c.get(TcuInfoValue.class).renderOutside(gui, stack, partTicks, x + c.pos[0], y + c.pos[1] - this.sData.minY, mouseX, mouseY);
        }
    }
}
