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
import org.apache.commons.lang3.Range;

public abstract class IllustratedProvider
        implements ITcuInfoProvider
{
    protected static final int[] DEFAULT_ICON_SIZE = new int[] { 16, 16 };

    protected GuiElementInst icon;
    protected GuiElementInst tooltip;

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        JsonObject iconData = MiscUtils.get(data.getAsJsonObject("icon"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(iconData, "size", this.getDefaultIconSize());
        JsonUtils.addDefaultJsonProperty(iconData, "uv", this.getDefaultIconUV());

        Texture iconElem = Texture.Builder.fromJson(gui, iconData);
        this.icon = new GuiElementInst(JsonUtils.getIntArray(iconData.get(OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), iconElem).initialize(gui);


        JsonObject ttipData = MiscUtils.get(data.getAsJsonObject("tooltip"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(ttipData, "size", this.getDefaultTooltipHoverSize());
        JsonUtils.addDefaultJsonProperty(ttipData, "text", this.getDefaultTooltipText());

        Tooltip ttipElem = Tooltip.Builder.fromJson(gui, ttipData);
        this.tooltip = new GuiElementInst(JsonUtils.getIntArray(ttipData.get(OFFSET_JSON_ELEM), this.icon.pos, Range.is(2)), ttipElem).initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        this.icon.get().setup(gui, this.icon);
        this.tooltip.get().setup(gui, this.tooltip);
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        GuiDefinition.renderElement(gui, stack, x + this.icon.pos[0], y + this.icon.pos[1], mouseX, mouseY, partTicks, this.icon);
    }

    @Override
    public void renderOutside(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        GuiDefinition.renderElement(gui, stack, x + this.tooltip.pos[0], y + this.tooltip.pos[1], mouseX, mouseY, partTicks, this.tooltip);
    }

    protected int[] getDefaultIconSize() { return DEFAULT_ICON_SIZE; }

    protected abstract int[] getDefaultIconUV();

    protected int[] getDefaultTooltipHoverSize() { return DEFAULT_ICON_SIZE; }

    protected abstract String getDefaultTooltipText();
}
