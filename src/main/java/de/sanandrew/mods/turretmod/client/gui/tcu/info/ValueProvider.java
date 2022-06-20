package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ProgressBar;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Range;

public abstract class ValueProvider
        extends IllustratedProvider
{
    protected static final int[] DEFAULT_INDICATOR_SIZE = new int[] { 120, 5 };

    protected GuiElementInst indicator;
    protected GuiElementInst background;
    protected GuiElementInst label;

    protected double currValue = 0.0D;
    protected double maxValue = 0.0D;

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        super.loadJson(gui, data, w, h);

        this.indicator = this.loadIndicator(gui, MiscUtils.get(data.getAsJsonObject("indicator"), JsonObject::new));
        this.background = this.loadIndicatorBackg(gui, MiscUtils.get(data.getAsJsonObject("indicatorBackground"), JsonObject::new));
        this.label = this.loadLabel(gui, MiscUtils.get(data.getAsJsonObject("label"), JsonObject::new));
    }

    protected GuiElementInst loadIndicator(IGui gui, JsonObject data) {
        JsonUtils.addDefaultJsonProperty(data, "size", this.getDefaultIndicatorSize());
        JsonUtils.addDefaultJsonProperty(data, "uv", this.getDefaultIndicatorUV());
        JsonUtils.addDefaultJsonProperty(data, "uvBackground", this.getDefaultIndicatorBgUV());

        ProgressBar indElem = ProgressBar.Builder.fromJson(gui, data);

        return new GuiElementInst(JsonUtils.getIntArray(data.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0 }, Range.is(2)), indElem).initialize(gui);
    }

    protected GuiElementInst loadIndicatorBackg(IGui gui, JsonObject data) {
        JsonUtils.addDefaultJsonProperty(data, "size", this.getDefaultIndicatorSize());
        JsonUtils.addDefaultJsonProperty(data, "uv", this.getDefaultIndicatorBgUV());

        Texture indElem = Texture.Builder.fromJson(gui, data);

        return new GuiElementInst(JsonUtils.getIntArray(data.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0 }, Range.is(2)), indElem).initialize(gui);
    }

    protected GuiElementInst loadLabel(IGui gui, JsonObject data) {
        JsonUtils.addDefaultJsonProperty(data, "text", this.getDefaultLabelText());
        JsonUtils.addDefaultJsonProperty(data, "bordered", true);
        if( !data.has("color") ) {
            JsonObject colors = new JsonObject();
            colors.add("default", new JsonPrimitive("#" + Integer.toHexString(this.getDefaultLabelColor())));
            colors.add("borderColor", new JsonPrimitive("#" + Integer.toHexString(this.getDefaultLabelBorderColor())));
            data.add("color", colors);
        }

        Text txtElem = Text.Builder.fromJson(gui, data);

        GuiElementInst inst = new GuiElementInst(JsonUtils.getIntArray(data.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0 }, Range.is(2)), txtElem);
        return inst.initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        super.setup(gui, turret, w, h);

        this.calcValues(turret);

        this.indicator.get(ProgressBar.class).setPercentFunc(p -> this.maxValue != 0 ? this.currValue / this.maxValue : 0.0F);
        this.label.get(Text.class)
                  .setTextFunc((g, o) -> {
                      String fVal = this.getNumberFormat(this.currValue);
                      String fMax = this.getNumberFormat(this.maxValue);

                      if( o instanceof TranslationTextComponent ) {
                          return new TranslationTextComponent(((TranslationTextComponent) o).getKey(), fVal, fMax);
                      }

                      return new StringTextComponent(String.format(o.getString(), fVal, fMax));
                  });

        this.indicator.get().setup(gui, this.indicator);
        this.background.get().setup(gui, this.background);
        this.label.get().setup(gui, this.tooltip);
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
        this.calcValues(turret);

        this.indicator.get().tick(gui, this.indicator);
        this.background.get().tick(gui, this.background);
        this.label.get().tick(gui, this.indicator);
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderContent(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.indicator.pos[0] + 18, y + this.indicator.pos[1] + 9, mouseX, mouseY, partTicks, this.indicator);
        GuiDefinition.renderElement(gui, stack, x + this.background.pos[0] + 18, y + this.background.pos[1] + 9, mouseX, mouseY, partTicks, this.background);
        GuiDefinition.renderElement(gui, stack, x + this.label.pos[0] + 19, y + this.label.pos[1] + 3, mouseX, mouseY, partTicks, this.label);
    }

    protected abstract void calcValues(ITurretEntity turret);

    protected int[] getDefaultIndicatorSize() { return DEFAULT_INDICATOR_SIZE; }

    protected abstract int[] getDefaultIndicatorUV();

    protected int[] getDefaultIndicatorBgUV() { int[] defUV = this.getDefaultIndicatorUV(); return new int[] { defUV[0], defUV[1] - DEFAULT_INDICATOR_SIZE[1] }; }

    protected abstract String getDefaultLabelText();

    protected abstract int getDefaultLabelColor();

    protected int getDefaultLabelBorderColor() {
        return 0xFF000000;
    }

    protected abstract String getNumberFormat(double value);
}
