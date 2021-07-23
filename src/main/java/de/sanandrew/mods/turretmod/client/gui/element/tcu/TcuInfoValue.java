package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuInfoPage;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import java.util.function.Supplier;

public final class TcuInfoValue
        implements IGuiElement
{
    private final ITcuInfoProvider provider;
    private final int              w;
    private final int              h;

    private GuiElementInst icon;
    private GuiElementInst progBar;
    private GuiElementInst valLbl;

    TcuInfoValue(ITcuInfoProvider provider, int w, int h) {
        this.provider = provider;
        this.w = w;
        this.h = h;
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst elem) {
        JsonObject valData = MiscUtils.get(data.getAsJsonObject(this.provider.getName()),
                                           () -> MiscUtils.get(data.getAsJsonObject("default"), JsonObject::new));
        if( this.provider.useStandardRenderer() ) {

            this.setIcon(gui, valData);
            this.setProgressBar(gui, valData);
            this.setValueLabel(gui, valData);
        }

        if( this.provider.useCustomRenderer() ) {
            this.provider.customBake(gui, valData, this.w, this.h);
        }
    }

    private void setIcon(IGui gui, JsonObject data) {
        ITcuInfoProvider.ITexture icon = this.provider.buildIcon();
        JsonObject icData = MiscUtils.get(data.getAsJsonObject("icon"), JsonObject::new);
        int[] pos = cData(this.w, this.h, icData, icon);

        this.icon = new GuiElementInst(pos, new Texture(), JsonUtils.deepCopy(icData));
        this.icon.initialize(gui);
        this.icon.get().bakeData(gui, this.icon.data, this.icon);
    }

    private void setProgressBar(IGui gui, JsonObject data) {
        ITcuInfoProvider.ITexture pb = this.provider.buildProgressBar();
        if( pb != null ) {
            JsonObject pbData = MiscUtils.get(data.getAsJsonObject("progressBar"), JsonObject::new);
            int[] pos = cData(this.w, this.h, pbData, pb);

            JsonUtils.addDefaultJsonProperty(pbData, "uvBackground", pb.getBackgroundUV(this.w, this.h));

            this.progBar = new GuiElementInst(pos, new TcuInfoProgressBar(this.provider), pbData);
            this.progBar.initialize(gui);

            this.progBar.get().bakeData(gui, this.progBar.data, this.progBar);
        }
    }

    private void setValueLabel(IGui gui, JsonObject data) {
        ITextComponent valText = this.provider.getValueStr();
        if( valText != null ) {
            JsonObject txtData = MiscUtils.get(data.getAsJsonObject("valueLabel"), JsonObject::new);
            int[] pos = JsonUtils.getIntArray(txtData.get("offset"), new int[] {18, 2});

            this.valLbl = new GuiElementInst(pos, new ValueText(this.provider), txtData);
            this.valLbl.initialize(gui);

            this.valLbl.get().bakeData(gui, this.valLbl.data, this.valLbl);
        }
    }

    private static int[] cData(int w, int h, JsonObject data, ITcuInfoProvider.ITexture t) {
        JsonUtils.addDefaultJsonProperty(data, "size", t.getSize(w, h));
        JsonUtils.addDefaultJsonProperty(data, "uv", t.getUV(w, h));
        JsonUtils.addDefaultJsonProperty(data, "textureSize", t.getTextureSize());
        MiscUtils.accept(t.getTexture(), tx -> JsonUtils.addDefaultJsonProperty(data, "texture", tx.toString()));

        return off(data, () -> t.getOffset(w, h));
    }

    public static int[] off(JsonObject data, Supplier<int[]> t) {
        return MiscUtils.get(JsonUtils.getIntArray(data.get("offset"), (int[]) null), t);
    }

    @Override
    public void tick(IGui gui, JsonObject data) {
        if( gui instanceof TcuInfoPage ) {
            this.provider.tick(((TcuInfoPage) gui).getTurret());
        }

        this.icon.get().tick(gui, this.icon.data);

        if( this.provider.useStandardRenderer() ) {
            MiscUtils.accept(this.progBar, p -> p.get().tick(gui, p.data));
            MiscUtils.accept(this.valLbl, v -> v.get().tick(gui, v.data));
        }
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        GuiDefinition.renderElement(gui, stack, x + this.icon.pos[0], y + this.icon.pos[1], mouseX, mouseY, partTicks, this.icon);

        if( this.provider.useStandardRenderer() ) {
            MiscUtils.accept(this.progBar, p -> GuiDefinition.renderElement(gui, stack, x + p.pos[0], y + p.pos[1], mouseX, mouseY, partTicks, p));
            MiscUtils.accept(this.valLbl, v -> GuiDefinition.renderElement(gui, stack, x + v.pos[0], y + v.pos[1], mouseX, mouseY, partTicks, v));
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

    private static class ValueText
            extends Text
    {
        private final ITcuInfoProvider provider;

        public ValueText(ITcuInfoProvider provider) {
            this.provider = provider;
        }

        @Override
        public ITextComponent getBakedText(IGui gui, JsonObject data) {
            return StringTextComponent.EMPTY;
        }

        @Override
        public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
            ITextComponent valStr = this.provider.getValueStr();
            if( valStr == null ) {
                return originalText;
            }

            Color valClr = MiscUtils.apply(valStr.getStyle(), Style::getColor, null);
            if( valClr != null ) {
                this.colors.put("styleClr", valClr.getValue());
                this.setColor("styleClr");
            }

            return this.provider.getValueStr();
        }
    }
}
