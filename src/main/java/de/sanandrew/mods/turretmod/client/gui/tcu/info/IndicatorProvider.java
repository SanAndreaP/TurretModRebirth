package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.IndicatorBar;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.IndicatorText;
import org.apache.commons.lang3.Range;

public abstract class IndicatorProvider
        extends IllustratedProvider
{
    protected static final int[] DEFAULT_INDICATOR_SIZE = new int[] { 120, 3 };

    protected GuiElementInst indicator;
    protected GuiElementInst label;

    protected double currValue = 0.0D;
    protected double maxValue = 0.0D;

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        super.loadJson(gui, data, w, h);

        this.indicator = this.loadIndicator(gui, MiscUtils.get(data.getAsJsonObject("indicator"), JsonObject::new));
        this.label = this.loadLabel(gui, MiscUtils.get(data.getAsJsonObject("label"), JsonObject::new));
    }

    protected GuiElementInst loadIndicator(IGui gui, JsonObject data) {
        JsonUtils.addDefaultJsonProperty(data, "size", this.getDefaultIndicatorSize());
        JsonUtils.addDefaultJsonProperty(data, "uv", this.getDefaultIndicatorUV());
        JsonUtils.addDefaultJsonProperty(data, "uvBackground", this.getDefaultIndicatorBgUV());

        IndicatorBar indElem = IndicatorBar.Builder.fromJson(gui, data);

        return new GuiElementInst(JsonUtils.getIntArray(data.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0}, Range.is(2)), indElem).initialize(gui);
    }

    protected GuiElementInst loadLabel(IGui gui, JsonObject data) {
        JsonUtils.addDefaultJsonProperty(data, "text", this.getDefaultLabelText());
        JsonUtils.addDefaultJsonProperty(data, "color", "#" + Integer.toHexString(this.getDefaultLabelColor()));

        IndicatorText txtElem = IndicatorText.Builder.fromJson(gui, data);

        return new GuiElementInst(JsonUtils.getIntArray(data.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0}, Range.is(2)), txtElem).initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        super.setup(gui, turret, w, h);

        this.calcValues(turret);

        this.indicator.get(IndicatorBar.class).setPercentageSupplier(() -> this.maxValue != 0 ? this.currValue / this.maxValue : 0.0F);
        this.label.get(IndicatorText.class).setValueSuppliers(() -> this.getNumberFormat(this.currValue), () -> this.getNumberFormat(this.maxValue));

        this.indicator.get().setup(gui, this.indicator);
        this.label.get().setup(gui, this.tooltip);
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
        this.calcValues(turret);

        this.indicator.get().tick(gui, this.indicator);
        this.label.get().tick(gui, this.indicator);
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderContent(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.indicator.pos[0] + 18, y + this.indicator.pos[1] + 11, mouseX, mouseY, partTicks, this.indicator);
        GuiDefinition.renderElement(gui, stack, x + this.label.pos[0] + 18, y + this.label.pos[1] + 2, mouseX, mouseY, partTicks, this.label);
    }

    protected abstract void calcValues(ITurretEntity turret);

    protected int[] getDefaultIndicatorSize() { return DEFAULT_INDICATOR_SIZE; }

    protected abstract int[] getDefaultIndicatorUV();

    protected int[] getDefaultIndicatorBgUV() { int[] defUV = this.getDefaultIndicatorUV(); return new int[] { defUV[0], defUV[1] - DEFAULT_INDICATOR_SIZE[1] }; }

    protected abstract String getDefaultLabelText();

    protected abstract int getDefaultLabelColor();

    protected abstract String getNumberFormat(double value);
}
