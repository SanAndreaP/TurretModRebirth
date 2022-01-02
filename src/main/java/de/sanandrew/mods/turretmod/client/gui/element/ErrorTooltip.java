package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ErrorTooltip
        extends Tooltip
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "error_tooltip");

    private long    timestampActivated;
    private boolean shown;

    private final int timeShowing;

    public ErrorTooltip(int[] mouseOverSize, int backgroundColor, int borderTopColor, int borderBottomColor, int[] padding, GuiElementInst content, int timeShowing) {
        super(mouseOverSize, backgroundColor, borderTopColor, borderBottomColor, padding, null, content);
        this.timeShowing = timeShowing;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        super.setup(gui, inst);

        this.setShown();
    }

    public static class Builder
            extends Tooltip.Builder
    {
        protected int timeShowing = 5000;

        public Builder(int[] mouseOverSize) {
            super(mouseOverSize);
        }

        public Builder timeShowing(int time) { this.timeShowing = time; return this; }

        @Override
        public ErrorTooltip get(IGui gui) {
            this.sanitize(gui);

            return new ErrorTooltip(this.mouseOverSize, this.backgroundColor, this.borderTopColor, this.borderBottomColor, this.padding, this.content, this.timeShowing);
        }

        @Override
        protected GuiElementInst loadContent(IGui gui, JsonObject data) {
            if( data.has("text") ) {
                Text.Builder txt = new Text.Builder(new TranslationTextComponent(JsonUtils.getStringVal(data.get("text"))));
                txt.color(data.has("color") ? MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"))) : 0xFFFF8080);
                txt.wrapWidth(data.has("wrapWidth") ? JsonUtils.getIntVal(data.get("wrapWidth")) : gui.getDefinition().width);

                return new GuiElementInst(txt.get(gui));
            }

            return super.loadContent(gui, data);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            JsonUtils.addDefaultJsonProperty(data, "backgroundColor", "0xF0100000");
            JsonUtils.addDefaultJsonProperty(data, "borderTopColor", "0x50FF0000");
            JsonUtils.addDefaultJsonProperty(data, "borderBottomColor", "0x507F0000");

            Tooltip.Builder tb = Tooltip.Builder.buildFromJson(gui, data, b -> (g, j) -> null);
            Builder b = IBuilder.copyValues(tb, new Builder(tb.mouseOverSize));

            GuiElementInst content = b.loadContent(gui, data);
            if( content != null ) {
                b.content(content);
            }

            JsonUtils.fetchInt(data.get("timeShowing"), b::timeShowing);

            return b;
        }

        public static ErrorTooltip fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }

    public void activate() {
        this.timestampActivated = System.currentTimeMillis();
        this.shown = true;
    }

    public void deactivate() {
        this.timestampActivated = System.currentTimeMillis() - this.timeShowing;
        this.shown = false;
    }

    @Override
    public void tick(IGui gui, GuiElementInst e) {
        super.tick(gui, e);

        this.setShown();
    }

    private void setShown() {
        this.shown = this.timestampActivated >= System.currentTimeMillis() - this.timeShowing;
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst e) {
        super.render(gui, stack, partTicks, x, y + 24, mouseX, mouseY + 24, e);
    }

    @Override
    public boolean isVisible() {
        return this.shown;
    }
}
