package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.client.init.TcuClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;
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

    //TODO: debug shit, remove when finished
    @SuppressWarnings("all")
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        this.posX = x;
        this.posY = y;
        if (this.prevLmbDown && !Minecraft.getInstance().mouseHandler.isLeftPressed()) {
            this.prevLmbDown = false;
        }

        GuiElementInst btn = this.scrollBtn[this.countAll > this.countSub ? 0 : 1];
        int scrollY = btn.pos[1] + (int)Math.round(this.scroll * (double)(this.scrollHeight - ((Texture)btn.get(Texture.class)).size[1]));
        btn.get().render(gui, stack, partTicks, btn.pos[0], scrollY, mouseX, mouseY, btn.data);
//        GuiUtils.enableScissor(gui.getScreenPosX() + x, gui.getScreenPosY() + y, this.areaSize[0], this.areaSize[1]);
//        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, data);


        GuiElementInst[] var11 = this.getChildren();
        int var12 = var11.length;

        for(int var13 = 0; var13 < var12; ++var13) {
            GuiElementInst inst = var11[var13];
            GuiDefinition.renderElement(gui, stack, x + inst.pos[0], y - this.sData.minY + inst.pos[1], mouseX, mouseY, partTicks, inst, true);
        }

//        RenderSystem.disableScissor();
    }
}
