/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.renderer.turret;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.renderer.TmrRenderTypes;
import de.sanandrew.mods.turretmod.client.renderer.turret.layer.LayerTurretGlow;
import de.sanandrew.mods.turretmod.client.renderer.turret.layer.LayerTurretUpgrades;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.OptionalDouble;
import java.util.function.Supplier;

public class TurretRenderBase<T extends LivingEntity & ITurretEntity, M extends EntityModel<T>>
        extends LivingRenderer<T, M>
{
    public static final RenderType STEM_BLACK = TmrRenderTypes.line(5.0F);
    public static final RenderType STEM_WHITE = TmrRenderTypes.line(3.0F);


    public TurretRenderBase(EntityRendererManager manager, Supplier<M> modelFactory) {
        super(manager, modelFactory.get(), 0.5F);

        this.addTurretLayers();
    }

    protected void addTurretLayers() {
        this.addLayer(new LayerTurretUpgrades<>(this));
        this.addLayer(new LayerTurretGlow<>(this));
    }

    @Override
    public void render(@Nonnull T turretInst, float entityYaw, float partialTicks, @Nonnull MatrixStack stack,
                       @Nonnull IRenderTypeBuffer buffer, int packedLight)
    {
        super.render(turretInst, entityYaw, partialTicks, stack, buffer, packedLight);

//        renderTurretRange(turretInst, stack);
        renderTurretRangeBuf(turretInst, stack, buffer);
    }

    @Override
    protected boolean shouldShowName(@Nonnull T entity) {
        return false;
    }

    /**
     * This is returning the glow layer texture for {@link LayerTurretGlow}, since the {@link TurretRenderer} handles the regular model rendering
     */
    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull T entity) {
        return entity.getDelegate().getBaseTexture(entity);
    }


    protected static void renderTurretRangeBuf(ITurretEntity turret, MatrixStack stack, IRenderTypeBuffer buffer) {
        final int alpha = 128;
        if( turret.shouldShowRange() ) {
            AxisAlignedBB aabb = turret.getTargetProcessor().getAdjustedRange(false);
            Matrix4f pose = stack.last().pose();

            IVertexBuilder vb = buffer.getBuffer(STEM_BLACK);
            vb.vertex(pose, 0, (float) aabb.minY, 0).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
            vb.vertex(pose, 0, (float) aabb.maxY, 0).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();

            vb = buffer.getBuffer(STEM_WHITE);
            vb.vertex(pose, 0, (float) aabb.minY, 0).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
            vb.vertex(pose, 0, (float) aabb.maxY, 0).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
        }
    }

    protected static void renderTurretRange(ITurretEntity turret, MatrixStack stack) {
        final int alpha = 128;
        if( turret.shouldShowRange() ) {
            RenderSystem.disableTexture();
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            Tessellator   tess = Tessellator.getInstance();
            BufferBuilder buf  = tess.getBuilder();

            AxisAlignedBB aabb = turret.getTargetProcessor().getAdjustedRange(false);

            stack.pushPose();
            Matrix4f pose = stack.last().pose();

            GlStateManager._lineWidth(5.0F);
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
            buf.vertex(pose, 0, (float) aabb.minY, 0).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
            buf.vertex(pose, 0, (float) aabb.maxY, 0).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
            tess.end();
            GlStateManager._lineWidth(3.0F);
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
            buf.vertex(pose, 0, (float) aabb.minY, 0).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
            buf.vertex(pose, 0, (float) aabb.maxY, 0).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
            tess.end();

            for( float cx = (float) aabb.minX; cx <= aabb.maxX; cx += 0.5F ) {
                GlStateManager._lineWidth(0.5F);
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
                buf.vertex(pose, cx, (float) aabb.minY, (float) aabb.minZ).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, cx, (float) aabb.minY, (float) aabb.maxZ).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, cx, (float) aabb.maxY, (float) aabb.maxZ).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, cx, (float) aabb.maxY, (float) aabb.minZ).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                tess.end();
                GlStateManager._lineWidth(0.1F);
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
                buf.vertex(pose, cx, (float) aabb.minY, (float) aabb.minZ).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, cx, (float) aabb.minY, (float) aabb.maxZ).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, cx, (float) aabb.maxY, (float) aabb.maxZ).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, cx, (float) aabb.maxY, (float) aabb.minZ).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                tess.end();
            }
            for( float cy = (float) aabb.minY; cy <= aabb.maxY; cy += 0.5F ) {
                GlStateManager._lineWidth(0.5F);
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
                buf.vertex(pose, (float) aabb.minX, cy, (float) aabb.minZ).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.minX, cy, (float) aabb.maxZ).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.maxX, cy, (float) aabb.maxZ).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.maxX, cy, (float) aabb.minZ).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                tess.end();
                GlStateManager._lineWidth(0.1F);
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
                buf.vertex(pose, (float) aabb.minX, cy, (float) aabb.minZ).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.minX, cy, (float) aabb.maxZ).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.maxX, cy, (float) aabb.maxZ).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.maxX, cy, (float) aabb.minZ).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                tess.end();
            }
            for( float cz = (float) aabb.minZ; cz <= aabb.maxZ; cz += 0.5F ) {
                GlStateManager._lineWidth(0.5F);
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
                buf.vertex(pose, (float) aabb.minX, (float) aabb.minY, cz).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.minX, (float) aabb.maxY, cz).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.maxX, (float) aabb.maxY, cz).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.maxX, (float) aabb.minY, cz).color(0, 0, 0, alpha).uv2(0, 0xF0).endVertex();
                tess.end();
                GlStateManager._lineWidth(0.1F);
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR_LIGHTMAP);
                buf.vertex(pose, (float) aabb.minX, (float) aabb.minY, cz).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.minX, (float) aabb.maxY, cz).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.maxX, (float) aabb.maxY, cz).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                buf.vertex(pose, (float) aabb.maxX, (float) aabb.minY, cz).color(255, 255, 255, alpha).uv2(0, 0xF0).endVertex();
                tess.end();
            }

            stack.popPose();

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.enableTexture();
        }
    }
}
