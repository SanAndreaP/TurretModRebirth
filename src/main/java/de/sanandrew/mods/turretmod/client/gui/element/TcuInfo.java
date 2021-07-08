package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuInfoPage;
import de.sanandrew.mods.turretmod.client.init.TcuClientRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

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
        final JsonObject elemData = data.getAsJsonObject("element");
        final int w = this.areaSize[0];
        final int h = this.elemHeight;

        return IntStream.range(0, providers.size())
                        .mapToObj(i -> new GuiElementInst(new int[] { 0, h * i}, new Value(providers.get(i), w, h, i), elemData))
                        .peek(e -> e.initialize(gui))
                        .toArray(GuiElementInst[]::new);
    }

    private static final class Value
            implements IGuiElement
    {
        private final ITcuInfoProvider provider;
        private final int w;
        private final int h;
        private final int index;

        private GuiElementInst icon;

        private Value(ITcuInfoProvider provider, int w, int h, int ind) {
            this.provider = provider;
            this.w = w;
            this.h = h;
            this.index = ind;
        }

        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst elem) {
            if( this.provider.useStandardRenderer() ) {
                JsonObject iconData = MiscUtils.get(data.getAsJsonObject("icon"), JsonObject::new);
                this.setIconTextureData(iconData);

                this.icon = new GuiElementInst(new int[] {0, 0}, new Texture(), iconData);
                this.icon.get().bakeData(gui, iconData, this.icon);
            }
        }

        private void setIconTextureData(JsonObject iconData) {
            ITcuInfoProvider.ITexture icon = this.provider.buildIcon();

            MiscUtils.accept(icon.getTexture(), t -> JsonUtils.addJsonProperty(iconData, "texture", t.toString()));
            JsonUtils.addJsonProperty(iconData, "size", icon.getSize(this.w, this.h));
            JsonUtils.addJsonProperty(iconData, "uv", icon.getUV(this.w, this.h));
            JsonUtils.addJsonProperty(iconData, "textureSize", this.provider.getTextureSize());
        }

        @Override
        public void tick(IGui gui, JsonObject data) {
            if( gui instanceof TcuInfoPage ) {
                this.provider.tick(((TcuInfoPage) gui).getTurret());
            }
        }

        @Override
        public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
            Screen.fill(stack, x, y, x + this.w, y + this.h, 0xFF000000 | (0xFF00000 >> this.index*4));

            if( this.provider.useStandardRenderer() ) {
                GuiDefinition.renderElement(gui, stack, x, y, mouseX, mouseY, partTicks, this.icon);
            }

            if( this.provider.useCustomRenderer() ) {
                this.provider.render(gui.get(), stack, partTicks, x, y, mouseX, mouseY, this.w, this.h);
            }
        }

        @Override
        public int getWidth() {
            return this.w;
        }

        @Override
        public int getHeight() {
            return this.h;
        }

        @Override
        public boolean isVisible() {
            return this.provider.isVisible();
        }
    }
}
