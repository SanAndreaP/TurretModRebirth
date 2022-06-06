package de.sanandrew.mods.turretmod.client.renderer.turret.label;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRenderer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SuppressWarnings("java:S5993")
public abstract class ValueBar
        implements ILabelRenderer
{
    private final ResourceLocation id;

    public ValueBar(ResourceLocation id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean isVisible(ITurretEntity turret) {
        return true;
    }

    @Override
    public int getMinWidth(ILabelRegistry registry, ITurretEntity turret) {
        FontRenderer fr = registry.getFontRenderer();
        return fr.width(this.getLabelTxt()) + 12 + fr.width(this.getValueTxt(turret));
    }

    @Override
    public int getHeight(ILabelRegistry registry, ITurretEntity turret) {
        return registry.getFontRenderer().lineHeight + 3;
    }

    protected abstract ITextComponent getLabelTxt();

    protected abstract ITextComponent getValueTxt(ITurretEntity turret);

    protected abstract ColorObj getFgColor(float opacity);

    protected abstract ColorObj getBgColor(float opacity);

    protected abstract float getValue(ITurretEntity turret);

    protected abstract float getMaxValue(ITurretEntity turret);

    @Override
    public void render(ILabelRegistry registry, ITurretEntity turret, WorldRenderer context, MatrixStack mat, float totalWidth, float totalHeight, float partialTicks, float opacity) {
        int h = getHeight(registry, turret);

        int clrFrg = getFgColor(opacity).getColorInt();
        int clrBkg = getBgColor(opacity).getColorInt();

        float val = getValue(turret);
        float maxVal = getMaxValue(turret);

        Matrix4f pose = mat.last().pose();
        Tessellator   tess = Tessellator.getInstance();
        BufferBuilder buf  = tess.getBuilder();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        registry.quadPC(buf, pose, new Vector2f(0, h - 2.0F), new Vector2f(totalWidth, 2), clrBkg);
        registry.quadPC(buf, pose, new Vector2f(0, h - 2.0F), new Vector2f(totalWidth * Math.min(val / maxVal, 1.0F), 2), clrFrg);

        tess.end();

        RenderSystem.enableTexture();

        ITextComponent ammoValue = this.getValueTxt(turret);
        registry.drawFont(this.getLabelTxt(), 0, 0, clrFrg, mat);
        registry.drawFont(ammoValue, totalWidth - registry.getFontRenderer().width(ammoValue), 0, clrFrg, mat);

        RenderSystem.disableTexture();
    }
}
