package de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.function.Procedure;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.Range;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.function.BiFunction;

class Slider
{
    int[] size;
    int[] pos;

    private static boolean prevMouseDownAll;
    private        boolean prevMouseDown;

    private float          value;
    private GuiElementInst valueTxt;
    private GuiElementInst marker;
    private float          valueBase;
    private Procedure      callback;
    private BiFunction<Slider, Float, String> toTextConverter;

    static Slider load(IGui gui, JsonObject data, final float valueBase, final float scale) {
        final Slider sd = new Slider();

        sd.valueBase = valueBase;

        sd.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        sd.pos = JsonUtils.getIntArray(data.get("pos"), Range.is(2));

        JsonObject tfData = MiscUtils.defIfNull(data.getAsJsonObject("textfield"), JsonObject::new);
        if( JsonUtils.getBoolVal(tfData.get("visible"), true) ) {
            BiFunction<Slider, String, Float> toValueConverter = (sl, s) -> {
                try {
                    return Math.max(0.0F, Math.min(sl.size[0], Float.parseFloat(s) / scale / valueBase) * sl.size[0]);
                } catch( NumberFormatException ignored ) { }

                return null;
            };

            JsonUtils.addDefaultJsonProperty(tfData, "size", new int[] { 40, sd.size[1] });

            sd.toTextConverter = (sl, f) -> String.format("%.1f", f / (float) sl.size[0] * valueBase * scale);

            TextField tf = new TextField();
            sd.valueTxt = new GuiElementInst(JsonUtils.getIntArray(tfData.get("offset"), new int[] { 3, 0 }, Range.is(2)), tf, tfData).initialize(gui);

            tf.bakeData(gui, sd.valueTxt.data, sd.valueTxt);
            tf.setValidator(s -> toValueConverter.apply(sd, s) != null);
            tf.setResponder(s -> {
                sd.value = toValueConverter.apply(sd, s);
                if( sd.callback != null ) {
                    sd.callback.work();
                }
            });
        }
        JsonObject mrkData = MiscUtils.defIfNull(data.getAsJsonObject("marker"), JsonObject::new);
        sd.marker = new GuiElementInst(JsonUtils.getIntArray(mrkData.get("offset"), new int[] { -2, -2 }), new Marker(), mrkData).initialize(gui);
        sd.marker.get().bakeData(gui, sd.marker.data, sd.marker);

        return sd;
    }

    boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        return this.valueTxt != null && this.valueTxt.get().mouseClicked(gui, mouseX, mouseY, mouseButton);
    }

    boolean keyTyped(IGui gui, char typedChar, int keyCode) throws IOException {
        return this.valueTxt != null && this.valueTxt.get().keyTyped(gui, typedChar, keyCode);
    }

    void update(IGui gui) {
        if( this.valueTxt != null ) {
            this.valueTxt.get().update(gui, this.valueTxt.data);
        }
    }

    void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY) {
        if( Mouse.isButtonDown(0) ) {
            if( !prevMouseDownAll && IGuiElement.isHovering(gui, x + this.pos[0], y + this.pos[1], mouseX, mouseY, this.size[0], this.size[1]) ) {
                prevMouseDownAll = true;
                this.prevMouseDown = true;
            }
            if( this.prevMouseDown ) {
                this.setSliderValue(mouseX - gui.getScreenPosX() - x - this.pos[0]);
            }
        } else {
            prevMouseDownAll = false;
            this.prevMouseDown = false;
        }

        GuiDefinition.renderElement(gui, x + this.pos[0] + Math.round(this.value) + this.marker.pos[0],
                                    y + this.pos[1] + this.marker.pos[1], mouseX, mouseY, partTicks, this.marker);

        if( this.valueTxt != null ) {
            GuiDefinition.renderElement(gui, x + this.pos[0] + this.size[0] + this.valueTxt.pos[0], y + this.pos[1] + this.valueTxt.pos[1],
                                        mouseX, mouseY, partTicks, this.valueTxt);
        }
    }

    private void setSliderValue(int value) {
        this.value = Math.max(0, Math.min(this.size[0], value));
        if( this.valueTxt != null && this.toTextConverter != null ) {
            this.valueTxt.get(TextField.class).setText(this.toTextConverter.apply(this, this.value));
        }
        if( this.callback != null ) {
            this.callback.work();
        }
    }

    void setValue(float val) {
        this.value = val * this.size[0] / this.valueBase;
        if( this.valueTxt != null && this.toTextConverter != null ) {
            this.valueTxt.get(TextField.class).setText(this.toTextConverter.apply(this, this.value));
        }
    }

    float getValue() {
        return this.value / this.size[0] * this.valueBase;
    }

    void setCallback(Procedure callback) {
        this.callback = callback;
    }

    private static final class Marker
            extends Texture
    {
        private boolean invertMask;

        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            super.bakeData(gui, data, inst);

            this.invertMask = JsonUtils.getBoolVal(data.get("invertMask"), true);
        }

        @Override
        protected void drawRect(IGui gui) {
            if( this.invertMask ) {
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                super.drawRect(gui);
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_DST_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            } else {
                super.drawRect(gui);
            }
        }
    }
}
