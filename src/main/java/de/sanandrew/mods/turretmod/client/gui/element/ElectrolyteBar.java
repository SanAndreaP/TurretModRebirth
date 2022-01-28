package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ElectrolyteBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "electrolyte_bar");

    private final int slot;
    private int       energyBarWidth;

    public ElectrolyteBar(ResourceLocation txLocation, int[] size, int[] textureSize, int[] uv, float[] scale, ColorObj color, int slot) {
        super(txLocation, size, textureSize, uv, scale, color);

        this.slot = slot;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        if( !(gui instanceof IElectrolyteInfo) ) {
            throw new UnsupportedOperationException("Cannot use electrolyte_bar on a GUI which doesn't implement IElectrolyteInfo");
        }

        super.setup(gui, inst);

        this.setEnergyBarWidth((IElectrolyteInfo) gui);
    }

    @Override
    public void tick(IGui gui, GuiElementInst e) {
        this.setEnergyBarWidth((IElectrolyteInfo) gui);
    }

    private void setEnergyBarWidth(IElectrolyteInfo gel) {
        double energyPerc = gel.getProgress(this.slot) / (double) gel.getMaxProgress(this.slot);

        this.energyBarWidth = Math.max(0, Math.min(this.size[0], MathHelper.ceil((1.0F - energyPerc) * this.size[0])));
    }

    @Override
    protected void drawRect(IGui gui, MatrixStack stack) {
        AbstractGui.blit(stack, 0, 0, this.uv[0], this.uv[1], this.energyBarWidth, this.size[1],
                         this.textureSize[0], this.textureSize[1]);
    }

    @Override
    public int getWidth() {
        return this.energyBarWidth;
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    public interface IElectrolyteInfo
    {
        int getProgress(int slot);

        int getMaxProgress(int slot);
    }

    public static class Builder
            extends Texture.Builder
    {
        public final int slot;

        public Builder(int[] size, int slot) {
            super(size);
            this.slot = slot;
        }

        @Override
        public ElectrolyteBar get(IGui gui) {
            this.sanitize(gui);

            return new ElectrolyteBar(this.texture, this.size, this.textureSize, this.uv, this.scale, this.color, this.slot);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 16, 3 });
            JsonUtils.addDefaultJsonProperty(data, "uv", new int[] { 176, 59 });

            Texture.Builder tb = Texture.Builder.buildFromJson(gui, data);

            return IBuilder.copyValues(tb, new Builder(tb.size, JsonUtils.getIntVal(data.get("slot"))));
        }

        public static ElectrolyteBar fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
