package de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class ColorPicker
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("color_picker");

    private int[] size;
    private int lumSliderDist;
    private int        lumSliderWidth;
    private TextField  colorTxt;
    private HueSlice[] hues;

    private int hue;
    private int sat;
    private int lum;
    private boolean prevMouseDownHueSat;
    private boolean prevMouseDownLum;

    private boolean visible = true;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        JsonObject lumBarData = MiscUtils.defIfNull(data.getAsJsonObject("luminanceSlider"), JsonObject::new);
        this.lumSliderDist = JsonUtils.getIntVal(lumBarData.get("gapBetweenHue"), 5);
        this.lumSliderWidth = JsonUtils.getIntVal(lumBarData.get("width"), 8);
        GuiElementInst txtField = gui.getDefinition().getElementById("colorText");
        this.colorTxt = txtField != null ? txtField.get(TextField.class) : null;

        this.calcHues();
    }

    private void calcHues() {
        final int[] mainHues = new int[] { 0xFF0000, 0xFFFF00, 0x00FF00, 0x00FFFF, 0x0000FF, 0xFF00FF, 0xFF0000 };

        int ySeq = this.size[0] / (mainHues.length - 1);
        List<HueSlice> hueList = new ArrayList<>();

        for( int i = 0; i < mainHues.length - 1; i++ ) {
            int currClr = mainHues[i];
            int nextClr = mainHues[i + 1];

            int diff = currClr ^ nextClr;
            int diffBitShift = (int) (Math.log10(diff & -diff) / Math.log10(2));
            int start = ySeq * i;
            int clrShift = Math.round(0xFF * (1 / (float) ySeq)) << diffBitShift;

            for( int x = 0; x < ySeq; x++ ) {
                hueList.add(new HueSlice(start + x, 1, currClr));
                if( currClr > nextClr ) {
                    currClr -= clrShift;
                } else {
                    currClr += clrShift;
                }
            }
        }

        this.hues = hueList.toArray(new HueSlice[0]);
    }

    @Override
    public void update(IGui gui, JsonObject data) {

    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        if( Mouse.isButtonDown(0) ) {
            if( IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size[0], this.size[1]) || this.prevMouseDownHueSat ) {
                this.hue = Math.max(Math.min(mouseX - gui.getScreenPosX() - x, this.size[0]), 0);
                this.sat = this.size[1] - Math.max(Math.min(mouseY - gui.getScreenPosY() - y, this.size[1]), 0);

                this.prevMouseDownHueSat = true;
            } else if( IGuiElement.isHovering(gui, x + this.size[0] + this.lumSliderDist, y, mouseX, mouseY, this.lumSliderWidth, this.size[1])
                       || this.prevMouseDownLum )
            {
                this.lum = this.size[1] - Math.max(Math.min(mouseY - gui.getScreenPosY() - y, this.size[1]), 0);

                this.prevMouseDownLum = true;
            }
        } else {
            this.prevMouseDownHueSat = false;
            this.prevMouseDownLum = false;
        }

        GuiUtils.drawGradientRect(x + this.size[0] + this.lumSliderDist, y, this.lumSliderWidth, this.size[1], this.getLuminanceColor(), 0xFF000000, true);
        for( HueSlice hs : this.hues ) {
            GuiUtils.drawGradientRect(x + hs.x(), y, hs.width(), this.size[1], 0xFF000000 | hs.color(), this.getSaturationColor(), true);
        }
    }

    @Override
    public int getWidth() {
        return this.size[0];
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private float getLuminanceValue() {
        return this.lum / (float) this.size[1];
    }

    private float getHueValue() {
        return this.hue / (float) this.size[0] * 360.0F;
    }

    private float getSaturationValue() {
        return this.sat / (float) this.size[1];
    }

    private int getSaturationColor() {
        int satBit = (int) (0xFF * this.getLuminanceValue());
        return 0xFF000000 | satBit | (satBit << 8) | (satBit << 16);
    }

    private int getLuminanceColor() {
        return ColorObj.fromHSLA(this.getHueValue(), this.getSaturationValue(), 0.5F, 1.0F).getColorInt();
    }

    private static class HueSlice
            extends Tuple
    {
        private static final long serialVersionUID = -3500046251131955628L;

        private HueSlice(int x, int width, int color) {
            super(x, width, color);
        }

        private int x() {
            return this.getValue(0);
        }

        private int width() {
            return this.getValue(1);
        }

        private int color() {
            return this.getValue(2);
        }
    }
}
