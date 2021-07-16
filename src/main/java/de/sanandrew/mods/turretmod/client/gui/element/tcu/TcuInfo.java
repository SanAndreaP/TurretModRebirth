package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.client.init.TcuClientRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
    public void buildChildren(IGui gui, JsonObject data, Map<Object, GuiElementInst> listToBuild) {
        super.buildChildren(gui, data, listToBuild);
    }

    @Override
    public GuiElementInst[] getElements(IGui gui, JsonObject data) {
        final List<ITcuInfoProvider> providers = TcuClientRegistry.getProviders();
        final JsonObject elemData = data.getAsJsonObject("elementData");
        final int w = this.areaSize[0];
        final int h = this.elemHeight;

        return IntStream.range(0, providers.size())
                        .mapToObj(i -> new GuiElementInst(new int[] { 0, h * i}, new TcuInfoValue(providers.get(i), w, h), elemData))
                        .peek(e -> e.initialize(gui))
                        .toArray(GuiElementInst[]::new);
    }

}
