package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuInfoPage;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import java.util.Map;
import java.util.function.Supplier;

public final class TcuInfoValue
        extends ElementParent<Integer>
{
    private final ITcuInfoProvider provider;
    private final int              w;
    private final int              h;

    private GuiElementInst icon;
    private GuiElementInst progBar;
    private GuiElementInst valLbl;
    private GuiElementInst ttip;

    TcuInfoValue(ITcuInfoProvider provider, int w, int h) {
        this.provider = provider;
        this.w = w;
        this.h = h;
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        super.bakeData(gui, data, inst);

        this.setTooltip(gui, MiscUtils.get(data.getAsJsonObject(this.provider.getName()),
                                           () -> MiscUtils.get(data.getAsJsonObject("default"), JsonObject::new)));
        this.ttip.get().bakeData(gui, this.ttip.data, this.ttip);
    }

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<Integer, GuiElementInst> children) {
        JsonObject valData = MiscUtils.get(data.getAsJsonObject(this.provider.getName()),
                                           () -> MiscUtils.get(data.getAsJsonObject("default"), JsonObject::new));

        this.setIcon(gui, valData);
        children.put(0, this.icon);

        if( this.provider.useStandardRenderer() ) {
            this.setProgressBar(gui, valData);
            if( this.progBar != null ) {
                children.put(1, this.progBar);
            }

            this.setValueLabel(gui, valData);
            if( this.valLbl != null ) {
                children.put(2, this.valLbl);
            }
        }

        if( this.provider.useCustomRenderer() ) {
            GuiElementInst[] cstChildren = this.provider.buildCustomElements(gui, valData, this.w, this.h);
            for( int i = 0, max = cstChildren.length; i < max; i++ ) {
                children.put(3 + i, cstChildren[i]);
            }
        }
    }

    private void setIcon(IGui gui, JsonObject data) {
        ITcuInfoProvider.ITexture ico = this.provider.buildIcon();
        JsonObject icData = MiscUtils.get(data.getAsJsonObject("icon"), JsonObject::new);
        int[] pos = cData(this.w, this.h, icData, ico);

        this.icon = new GuiElementInst(pos, new Texture(), JsonUtils.deepCopy(icData)).initialize(gui);
    }

    private void setTooltip(IGui gui, JsonObject data) {
        ITextComponent lblTxt = this.provider.getLabel();
        JsonObject ttipData = MiscUtils.get(data.getAsJsonObject("tooltip"), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(ttipData, "size", this.icon.get(Texture.class).size);

        JsonObject txtData = MiscUtils.get(ttipData.getAsJsonObject("text"), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(txtData, "color", "0xFFFFFFFF");

        final GuiElementInst txtElem = new GuiElementInst(new Text() {
            @Override
            public ITextComponent getBakedText(IGui gui, JsonObject data) {
                return lblTxt;
            }
        }, txtData).initialize(gui);

        this.ttip = new GuiElementInst(this.icon.pos, new Tooltip() {
            @Override
            public GuiElementInst getContent(IGui gui, JsonObject data) {
                return txtElem;
            }
        }, ttipData).initialize(gui);
    }

    private void setProgressBar(IGui gui, JsonObject data) {
        ITcuInfoProvider.ITexture pb = this.provider.buildProgressBar();
        if( pb != null ) {
            JsonObject pbData = MiscUtils.get(data.getAsJsonObject("progressBar"), JsonObject::new);
            int[] pos = cData(this.w, this.h, pbData, pb);

            JsonUtils.addDefaultJsonProperty(pbData, "uvBackground", pb.getBackgroundUV(this.w, this.h));

            this.progBar = new GuiElementInst(pos, new TcuInfoProgressBar(this.provider), pbData).initialize(gui);
        }
    }

    private void setValueLabel(IGui gui, JsonObject data) {
        ITextComponent valText = this.provider.getValueStr();
        if( valText != null ) {
            JsonObject txtData = MiscUtils.get(data.getAsJsonObject("valueLabel"), JsonObject::new);
            int[] pos = JsonUtils.getIntArray(txtData.get("offset"), new int[] {18, 2});

            this.valLbl = new GuiElementInst(pos, new ValueText(this.provider), txtData).initialize(gui);
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
            this.provider.tick(gui, ((TcuInfoPage) gui).getTurret());
        }

        this.ttip.get().tick(gui, this.ttip.data);

        super.tick(gui, data);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, data);

        if( this.provider.useCustomRenderer() ) {
            this.provider.render(gui, stack, partTicks, x, y, mouseX, mouseY, this.w, this.h);
        }
    }

    public void renderOutside(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY) {
        GuiDefinition.renderElement(gui, stack, x + this.ttip.pos[0], y + this.ttip.pos[1], mouseX, mouseY, partTicks, this.ttip, true);

        if( this.provider.useCustomRenderer() ) {
            this.provider.renderOutside(gui, stack, partTicks, x, y, mouseX, mouseY, this.w, this.h);
        }
    }

    @Override
    public void onClose(IGui gui) {
        super.onClose(gui);

        if( gui instanceof TcuInfoPage ) {
            this.provider.onClose(gui, ((TcuInfoPage) gui).getTurret());
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

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double mouseScroll) {
        return this.provider.mouseScrolled(gui, mouseX, mouseY, mouseScroll)
               || super.mouseScrolled(gui, mouseX, mouseY, mouseScroll);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.provider.mouseClicked(gui, mouseX, mouseY, button)
               || super.mouseClicked(gui, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        return this.provider.mouseReleased(gui, mouseX, mouseY, button)
               || super.mouseReleased(gui, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.provider.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY)
               || super.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.provider.keyPressed(gui, keyCode, scanCode, modifiers)
               || super.keyPressed(gui, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.provider.keyReleased(gui, keyCode, scanCode, modifiers)
               || super.keyReleased(gui, keyCode, scanCode, modifiers);
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
