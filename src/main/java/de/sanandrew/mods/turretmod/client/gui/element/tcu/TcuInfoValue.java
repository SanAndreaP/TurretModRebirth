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
import net.minecraft.util.text.ITextComponent;

final class TcuInfoValue
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
        if( this.provider.useStandardRenderer() ) {
            JsonObject valData = MiscUtils.get(data.getAsJsonObject(this.provider.getName()),
                                               () -> MiscUtils.get(data.getAsJsonObject("default"), JsonObject::new));

            this.setIcon(gui, valData);
            this.setProgressBar(gui, valData);
        }
    }

    private void setIcon(IGui gui, JsonObject data) {
        ITcuInfoProvider.ITexture icon = this.provider.buildIcon();
        JsonObject icData = MiscUtils.get(data.getAsJsonObject("icon"), JsonObject::new);
        int[] icMg = MiscUtils.get(JsonUtils.getIntArray(icData.get("margins"), (int[]) null), icon::getMargins);

        this.icon = new GuiElementInst(new int[] { icMg[3], icMg[0] }, new Texture(), data.getAsJsonObject("icon"));
        this.icon.initialize(gui);

        MiscUtils.accept(icon.getTexture(), t -> JsonUtils.addDefaultJsonProperty(this.icon.data, "texture", t.toString()));
        JsonUtils.addDefaultJsonProperty(this.icon.data, "size",
                                         new int[] { this.h - icMg[1] - icMg[3], this.h - icMg[0] - icMg[2] });
        JsonUtils.addDefaultJsonProperty(this.icon.data, "uv", icon.getUV(this.w, this.h));
        JsonUtils.addDefaultJsonProperty(this.icon.data, "textureSize", icon.getTextureSize());

        this.icon.get().bakeData(gui, this.icon.data, this.icon);
    }

    private void setProgressBar(IGui gui, JsonObject data) {
        ITcuInfoProvider.ITexture pb = this.provider.buildProgressBar();
        if( pb != null ) {
            JsonObject pbData = MiscUtils.get(data.getAsJsonObject("progressBar"), JsonObject::new);
            int[] pbMg = MiscUtils.get(JsonUtils.getIntArray(pbData.get("margins"), (int[]) null), pb::getMargins);

            this.progBar = new GuiElementInst(new int[] { this.h + pbMg[3] + 3, this.h - 6 - pbMg[2] }, new TcuInfoProgressBar(this.provider), pbData);
            this.progBar.initialize(gui);

            MiscUtils.accept(pb.getTexture(), t -> JsonUtils.addDefaultJsonProperty(this.progBar.data, "texture", t.toString()));
            JsonUtils.addDefaultJsonProperty(this.progBar.data, "size",
                                             new int[] { this.w - this.h - pbMg[1] - pbMg[3] - 6, JsonUtils.getIntVal(data.get("height"), 3) });
            JsonUtils.addDefaultJsonProperty(this.progBar.data, "uv", pb.getUV(this.w, this.h));
            JsonUtils.addDefaultJsonProperty(this.progBar.data, "uvBackground", pb.getBackgroundUV(this.w, this.h));
            JsonUtils.addDefaultJsonProperty(this.progBar.data, "textureSize", pb.getTextureSize());

            this.progBar.get().bakeData(gui, this.progBar.data, this.progBar);
        }
    }

    private void setValueLabel(IGui gui, JsonObject data) {
        ITextComponent valText = this.provider.getValueStr();
        if( valText != null ) {
            JsonObject txtData = MiscUtils.get(data.getAsJsonObject("valueLabel"), JsonObject::new);
            int[] txMg = JsonUtils.getIntArray(txtData.get("margins"), new int[4]);

            this.valLbl = new GuiElementInst(new int[] { this.h + txMg[3] + 3, txMg[0] }, new Text() {
                @Override
                public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
                    return valText;
                }
            }, txtData);
        }
    }

    @Override
    public void tick(IGui gui, JsonObject data) {
        if( gui instanceof TcuInfoPage ) {
            this.provider.tick(((TcuInfoPage) gui).getTurret());
        }

        this.icon.get().tick(gui, this.icon.data);

        if( this.provider.useStandardRenderer() ) {
            MiscUtils.accept(this.progBar, p -> p.get().tick(gui, p.data));
        }
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        GuiDefinition.renderElement(gui, stack, x + this.icon.pos[0], y + this.icon.pos[1], mouseX, mouseY, partTicks, this.icon);

        if( this.provider.useStandardRenderer() ) {
            MiscUtils.accept(this.progBar, p -> GuiDefinition.renderElement(gui, stack, x + p.pos[0], y + p.pos[1], mouseX, mouseY, partTicks, p));
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
