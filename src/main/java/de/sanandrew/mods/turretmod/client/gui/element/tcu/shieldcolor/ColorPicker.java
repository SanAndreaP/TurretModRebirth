package de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.function.Procedure;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

@SuppressWarnings("unused")
public class ColorPicker
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("color_picker");

    private Slider hue;
    private Slider sat;
    private Slider lum;
    private Slider alp;

    private boolean visible = true;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        this.hue = Slider.load(gui, data.getAsJsonObject("hueSlider"), 360.0F, 1.0F);
        this.sat = Slider.load(gui, data.getAsJsonObject("saturationSlider"), 1.0F, 100.0F);
        this.lum = Slider.load(gui, data.getAsJsonObject("luminanceSlider"), 1.0F, 100.0F);
        this.alp = Slider.load(gui, data.getAsJsonObject("alphaSlider"), 1.0F, 100.0F);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.hue.update(gui);
        this.sat.update(gui);
        this.lum.update(gui);
        this.alp.update(gui);
    }

    @Override
    public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        return this.hue.mouseClicked(gui, mouseX, mouseY, mouseButton)
               || this.sat.mouseClicked(gui, mouseX, mouseY, mouseButton)
               || this.lum.mouseClicked(gui, mouseX, mouseY, mouseButton)
               || this.alp.mouseClicked(gui, mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyTyped(IGui gui, char typedChar, int keyCode) throws IOException {
        return this.hue.keyTyped(gui, typedChar, keyCode)
               || this.sat.keyTyped(gui, typedChar, keyCode)
               || this.lum.keyTyped(gui, typedChar, keyCode)
               || this.alp.keyTyped(gui, typedChar, keyCode);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        int c1, c2;
        float h = this.hue.getValue();
        float s = this.sat.getValue();
        float l = this.lum.getValue();
        for( int i = 0, max = this.hue.size[0]; i < max; i++ ) {
            c1 = ColorObj.fromHSLA((i / (float) max) * 360.0F, 1.0F, 0.5F, 1.0F).getColorInt();
            GuiUtils.drawGradientRect(x + i + this.hue.pos[0], y + this.hue.pos[1], 1, this.hue.size[1], c1, c1, true);
        }

        c1 = ColorObj.fromHSLA(h, 0.0F, l, 1.0F).getColorInt();
        c2 = ColorObj.fromHSLA(h, 1.0F, l, 1.0F).getColorInt();
        GuiUtils.drawGradientRect(x + this.sat.pos[0], y + this.sat.pos[1], this.sat.size[0], this.sat.size[1], c1, c2, false);

        c1 = ColorObj.fromHSLA(h, s, 0.0F, 1.0F).getColorInt();
        c2 = ColorObj.fromHSLA(h, s, 0.5F, 1.0F).getColorInt();
        GuiUtils.drawGradientRect(x + this.lum.pos[0], y + this.lum.pos[1], this.lum.size[0] / 2, this.lum.size[1], c1, c2, false);
        c1 = ColorObj.fromHSLA(h, s, 1.0F, 1.0F).getColorInt();
        GuiUtils.drawGradientRect(x + this.lum.pos[0] + this.lum.size[0] / 2, y + this.lum.pos[1], this.lum.size[0] / 2, this.lum.size[1], c2, c1, false);

        c1 = ColorObj.fromHSLA(h, s, l, 0.0F).getColorInt();
        c2 = ColorObj.fromHSLA(h, s, l, 1.0F).getColorInt();
        GuiUtils.drawGradientRect(x + this.alp.pos[0], y + this.alp.pos[1], this.alp.size[0], this.alp.size[1], c1, c2, false);

        this.hue.render(gui, partTicks, x, y, mouseX, mouseY);
        this.sat.render(gui, partTicks, x, y, mouseX, mouseY);
        this.lum.render(gui, partTicks, x, y, mouseX, mouseY);
        this.alp.render(gui, partTicks, x, y, mouseX, mouseY);
    }

    public int getColor() {
        return ColorObj.fromHSLA(this.hue.getValue(), this.sat.getValue(), this.lum.getValue(), this.alp.getValue()).getColorInt();
    }

    public void setColor(int clr) {
        ColorObj newClr = new ColorObj(clr);
        float[] hsl = newClr.calcHSL();

        this.hue.setValue(hsl[0]);
        this.sat.setValue(hsl[1]);
        this.lum.setValue(hsl[2]);
        this.alp.setValue(newClr.fAlpha());
    }

    public void setOnChangeCallback(Procedure callback) {
        this.hue.setCallback(callback);
        this.sat.setCallback(callback);
        this.lum.setCallback(callback);
        this.alp.setCallback(callback);
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
