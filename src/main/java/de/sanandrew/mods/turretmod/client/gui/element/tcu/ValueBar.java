package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.util.function.DoubleSupplier;

//TODO: put in SanLib
@SuppressWarnings("ProtectedMemberInFinalClass")
public final class ValueBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation("value_bar");

    private DoubleSupplier percSupplier = () -> 0.0D;

    private double currPerc = 0D;

    protected int[] uvBg;

    public ValueBar(ResourceLocation txLocation, int[] size, int[] textureSize, int[] uv, int[] uvBg, float[] scale, ColorObj color) {
        super(txLocation, size, textureSize, uv, scale, color);

        this.uvBg = uvBg;
    }

    public void setPercentageSupplier(@Nonnull DoubleSupplier supplier) {
        this.percSupplier = supplier;
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        this.currPerc = this.percSupplier.getAsDouble();
    }

    @Override
    protected void drawRect(IGui gui, MatrixStack stack) {
        if( this.uvBg != null ) {
            AbstractGui.blit(stack, 0, 0, this.uvBg[0], this.uvBg[1], this.size[0], this.size[1], this.textureSize[0], this.textureSize[1]);
        }

        int barX = Math.max(0, Math.min(this.size[0], MathHelper.ceil(this.currPerc * this.size[0])));
        AbstractGui.blit(stack, 0, 0, this.uv[0], this.uv[1], barX, this.size[1], this.textureSize[0], this.textureSize[1]);
    }

    @SuppressWarnings("unused")
    public static class Builder
            extends Texture.Builder
    {
        protected int[] uvBg;

        public Builder(int[] size) {
            super(size);
        }

        public Builder uvBackground(int[] uv) { this.uvBg = uv; return this; }

        public Builder uvBackground(int u, int v) { return this.uvBackground(new int[] { u, v }); }

        @Override
        public ValueBar get(IGui gui) {
            this.sanitize(gui);

            return new ValueBar(this.texture, this.size, this.textureSize, this.uv, this.uvBg, this.scale, this.color);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Texture.Builder tb = Texture.Builder.buildFromJson(gui, data);
            Builder b = IBuilder.copyValues(tb, new Builder(tb.size));

            JsonUtils.fetchIntArray(data.get("uvBackground"), b::uvBackground, Range.is(2));

            return b;
        }

        public static ValueBar fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
