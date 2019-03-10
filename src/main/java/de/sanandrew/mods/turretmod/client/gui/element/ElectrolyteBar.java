package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;

@SuppressWarnings({"Duplicates"})
public class ElectrolyteBar
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("electrolyte_bar");

    private BakedData data;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( !(gui instanceof IGuiElectrolyte) ) {
            throw new RuntimeException("Cannot use electrolyte_bar on a GUI which doesn't implement IGuiElectrolyte");
        }
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.slot = JsonUtils.getIntVal(data.get("slot"));
            this.data.location = new ResourceLocation(JsonUtils.getStringVal(data.get("location"), "sapturretmod:textures/gui/potatogen.png"));
            this.data.size = JsonUtils.getIntArray(data.get("size"), new int[] {16, 3}, Range.is(2));
            this.data.uv = JsonUtils.getIntArray(data.get("uv"), new int[] {176, 59}, Range.is(2));
            this.data.textureSize = JsonUtils.getIntArray(data.get("textureSize"), new int[] {256, 256}, Range.is(2));
            this.data.scale = JsonUtils.getDoubleArray(data.get("scale"), new double[] {1.0D, 1.0D}, Range.is(2));
            this.data.color = new ColorObj(MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"), "0xFFFFFFFF")));
            this.data.forceAlpha = JsonUtils.getBoolVal(data.get("forceAlpha"), false);
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        IGuiElectrolyte gel = (IGuiElectrolyte) gui;

        gui.get().mc.renderEngine.bindTexture(this.data.location);
        GlStateManager.pushMatrix();
        if( this.data.forceAlpha ) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        GlStateManager.translate(x, y, 0.0D);
        GlStateManager.scale(this.data.scale[0], this.data.scale[1], 1.0D);
        GlStateManager.color(this.data.color.fRed(), this.data.color.fGreen(), this.data.color.fBlue(), this.data.color.fAlpha());

        double energyPerc = gel.getProcess(this.data.slot) / (double) gel.getMaxProcess(this.data.slot);
        int energyBarX = Math.max(0, Math.min(this.data.size[0], MathHelper.ceil((1.0F - energyPerc) * this.data.size[0])));

        Gui.drawModalRectWithCustomSizedTexture(0, 0, this.data.uv[0], this.data.uv[1], energyBarX, this.data.size[1], this.data.textureSize[0], this.data.textureSize[1]);

        GlStateManager.popMatrix();
    }

    @Override
    public int getHeight() {
        return this.data == null ? 0 : this.data.size[1];
    }

    static final class BakedData
    {
        public int slot;
        private ResourceLocation location;
        int[] size;
        private int[] textureSize;
        private int[] uv;
        private double[] scale;
        private ColorObj color;
        private boolean forceAlpha;
    }

    public interface IGuiElectrolyte
    {
        int getProcess(int slot);

        int getMaxProcess(int slot);
    }
}
