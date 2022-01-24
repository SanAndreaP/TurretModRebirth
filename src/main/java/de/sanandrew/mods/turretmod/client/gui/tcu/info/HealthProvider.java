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
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfoProgressBar;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfoProgressText;
import de.sanandrew.mods.turretmod.init.Lang;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

//TODO: render personal shield
public class HealthProvider
        implements ITcuInfoProvider
{

    protected GuiElementInst icon;
    protected GuiElementInst pb;
    protected GuiElementInst ttip;
    protected GuiElementInst txt;

    private float currHealth = 0.0F;
    private float maxHealth = 0.0F;

    @Nonnull
    @Override
    public String getName() {
        return "health";
    }

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        JsonObject iconData = MiscUtils.get(data.getAsJsonObject("icon"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(iconData, "size", new int[] {16, 16});
        JsonUtils.addDefaultJsonProperty(iconData, "uv", new int[] {88, 16});

        Texture iconElem = Texture.Builder.fromJson(gui, iconData);
        this.icon = new GuiElementInst(JsonUtils.getIntArray(iconData.get(OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), iconElem).initialize(gui);


        JsonObject pbData = MiscUtils.get(data.getAsJsonObject("progressBar"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(pbData, "size", new int[] {140, 3});
        JsonUtils.addDefaultJsonProperty(pbData, "uv", new int[] {0, 149});
        JsonUtils.addDefaultJsonProperty(pbData, "uvBackground", new int[] {0, 146});

        TcuInfoProgressBar pbElem = TcuInfoProgressBar.Builder.fromJson(gui, pbData);
        this.pb = new GuiElementInst(JsonUtils.getIntArray(pbData.get(OFFSET_JSON_ELEM), new int[] { 0, 0}, Range.is(2)), pbElem).initialize(gui);


        JsonObject ttipData = MiscUtils.get(data.getAsJsonObject("tooltip"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(ttipData, "size", new int[] {16, 16});
        JsonUtils.addDefaultJsonProperty(ttipData, "text", Lang.TCU_TEXT.get("info.health.tooltip"));

        Tooltip ttipElem = Tooltip.Builder.fromJson(gui, ttipData);
        this.ttip = new GuiElementInst(JsonUtils.getIntArray(ttipData.get(OFFSET_JSON_ELEM), this.icon.pos, Range.is(2)), ttipElem).initialize(gui);


        JsonObject txtData = MiscUtils.get(data.getAsJsonObject("text"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(txtData, "text", Lang.TCU_TEXT.get("info.health.value"));
        JsonUtils.addDefaultJsonProperty(txtData, "color", "0xFFFF0000");

        TcuInfoProgressText txtElem = TcuInfoProgressText.Builder.fromJson(gui, txtData);
        this.txt = new GuiElementInst(JsonUtils.getIntArray(txtData.get(OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), txtElem).initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        this.calcValues(turret);

        this.pb.get(TcuInfoProgressBar.class).setPercentageFunc(() -> this.maxHealth != 0 ? this.currHealth / this.maxHealth : 0.0F);
        this.txt.get(TcuInfoProgressText.class).setValueFunc(() -> String.format("%.0f", this.currHealth / 2), () -> String.format("%.0f", this.maxHealth / 2));

        this.icon.get().setup(gui, this.icon);
        this.pb.get().setup(gui, this.pb);
        this.ttip.get().setup(gui, this.ttip);
        this.txt.get().setup(gui, this.ttip);
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
        this.calcValues(turret);

        this.pb.get().tick(gui, this.pb);
        this.txt.get().tick(gui, this.pb);
    }

    protected void calcValues(ITurretEntity turret) {
        this.currHealth = turret.get().getHealth();
        this.maxHealth = turret.get().getMaxHealth();
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        GuiDefinition.renderElement(gui, stack, x + this.icon.pos[0], y + this.icon.pos[1], mouseX, mouseY, partTicks, this.icon);
        GuiDefinition.renderElement(gui, stack, x + this.pb.pos[0] + 18, y + this.pb.pos[1] + 11, mouseX, mouseY, partTicks, this.pb);
        GuiDefinition.renderElement(gui, stack, x + this.txt.pos[0] + 18, y + this.txt.pos[1] + 2, mouseX, mouseY, partTicks, this.txt);
    }

    @Override
    public void renderOutside(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        GuiDefinition.renderElement(gui, stack, x + this.ttip.pos[0], y + this.ttip.pos[1], mouseX, mouseY, partTicks, this.ttip);
    }
}
