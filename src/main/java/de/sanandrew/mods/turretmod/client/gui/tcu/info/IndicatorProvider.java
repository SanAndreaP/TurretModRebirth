package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.IndicatorBar;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.IndicatorText;
import org.apache.commons.lang3.Range;

public abstract class IndicatorProvider
        implements ITcuInfoProvider
{
    protected static final int[] DEFAULT_ICON_SIZE = new int[] { 16, 16 };
    protected static final int[] DEFAULT_INDICATOR_SIZE = new int[] { 120, 3 };

    protected GuiElementInst icon;
    protected GuiElementInst indicator;
    protected GuiElementInst tooltip;
    protected GuiElementInst label;

    protected double currValue = 0.0D;
    protected double maxValue = 0.0D;

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        JsonObject iconData = MiscUtils.get(data.getAsJsonObject("icon"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(iconData, "size", this.getDefaultIconSize());
        JsonUtils.addDefaultJsonProperty(iconData, "uv", this.getDefaultIconUV());

        Texture iconElem = Texture.Builder.fromJson(gui, iconData);
        this.icon = new GuiElementInst(JsonUtils.getIntArray(iconData.get(OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), iconElem).initialize(gui);


        JsonObject indData = MiscUtils.get(data.getAsJsonObject("indicator"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(indData, "size", this.getDefaultIndicatorSize());
        JsonUtils.addDefaultJsonProperty(indData, "uv", this.getDefaultIndicatorUV());
        JsonUtils.addDefaultJsonProperty(indData, "uvBackground", this.getDefaultIndicatorBgUV());

        IndicatorBar indElem = IndicatorBar.Builder.fromJson(gui, indData);
        this.indicator = new GuiElementInst(JsonUtils.getIntArray(indData.get(OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), indElem).initialize(gui);


        JsonObject ttipData = MiscUtils.get(data.getAsJsonObject("tooltip"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(ttipData, "size", this.getDefaultTooltipHoverSize());
        JsonUtils.addDefaultJsonProperty(ttipData, "text", this.getDefaultTooltipText());

        Tooltip ttipElem = Tooltip.Builder.fromJson(gui, ttipData);
        this.tooltip = new GuiElementInst(JsonUtils.getIntArray(ttipData.get(OFFSET_JSON_ELEM), this.icon.pos, Range.is(2)), ttipElem).initialize(gui);


        JsonObject txtData = MiscUtils.get(data.getAsJsonObject("label"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(txtData, "text", this.getDefaultLabelText());
        JsonUtils.addDefaultJsonProperty(txtData, "color", "#" + Integer.toHexString(this.getDefaultLabelColor()));

        IndicatorText txtElem = IndicatorText.Builder.fromJson(gui, txtData);
        this.label = new GuiElementInst(JsonUtils.getIntArray(txtData.get(OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), txtElem).initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        this.calcValues(turret);

        this.indicator.get(IndicatorBar.class).setPercentageSupplier(() -> this.maxValue != 0 ? this.currValue / this.maxValue : 0.0F);
        this.label.get(IndicatorText.class).setValueSuppliers(() -> this.getNumberFormat(this.currValue), () -> this.getNumberFormat(this.maxValue));

        this.icon.get().setup(gui, this.icon);
        this.indicator.get().setup(gui, this.indicator);
        this.tooltip.get().setup(gui, this.tooltip);
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
        GuiDefinition.renderElement(gui, stack, x + this.icon.pos[0], y + this.icon.pos[1], mouseX, mouseY, partTicks, this.icon);
        GuiDefinition.renderElement(gui, stack, x + this.indicator.pos[0] + 18, y + this.indicator.pos[1] + 11, mouseX, mouseY, partTicks, this.indicator);
        GuiDefinition.renderElement(gui, stack, x + this.label.pos[0] + 18, y + this.label.pos[1] + 2, mouseX, mouseY, partTicks, this.label);
    }

    @Override
    public void renderOutside(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        GuiDefinition.renderElement(gui, stack, x + this.tooltip.pos[0], y + this.tooltip.pos[1], mouseX, mouseY, partTicks, this.tooltip);
    }

    protected abstract void calcValues(ITurretEntity turret);

    protected int[] getDefaultIconSize() { return DEFAULT_ICON_SIZE; }

    protected abstract int[] getDefaultIconUV();

    protected int[] getDefaultIndicatorSize() { return DEFAULT_INDICATOR_SIZE; }

    protected abstract int[] getDefaultIndicatorUV();

    protected int[] getDefaultIndicatorBgUV() { int[] defUV = this.getDefaultIndicatorUV(); return new int[] { defUV[0], defUV[1] - DEFAULT_INDICATOR_SIZE[1] }; }

    protected int[] getDefaultTooltipHoverSize() { return DEFAULT_ICON_SIZE; }

    protected abstract String getDefaultTooltipText();

    protected abstract String getDefaultLabelText();

    protected abstract int getDefaultLabelColor();

    protected abstract String getNumberFormat(double value);
}
