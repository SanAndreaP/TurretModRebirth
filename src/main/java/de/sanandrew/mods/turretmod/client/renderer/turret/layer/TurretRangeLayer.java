/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.renderer.turret.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.renderer.TmrRenderTypes;
import de.sanandrew.mods.turretmod.client.renderer.turret.TurretRenderBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class TurretRangeLayer<E extends LivingEntity & ITurretEntity, M extends EntityModel<E>>
        extends LayerRenderer<E, M>
{
    public static final RenderType STEM_BLACK = TmrRenderTypes.tmrLine(5.0F);
    public static final RenderType STEM_WHITE = TmrRenderTypes.tmrLine(3.0F);
    public static final RenderType FRAME_BLACK = TmrRenderTypes.tmrLine(3.0F);
    public static final RenderType FRAME_WHITE = TmrRenderTypes.tmrLine(1.0F);

    public TurretRangeLayer(TurretRenderBase<E, M> turretRenderer) {
        super(turretRenderer);
    }

    @Override
    public void render(@Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int packedLight, E turret,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if( turret.shouldShowRange() ) {
            AxisAlignedBB  aabb = turret.getTargetProcessor().getAdjustedRange(false);
            Matrix4f       pose = stack.last().pose();
            IVertexBuilder vb;

            vb = buffer.getBuffer(STEM_BLACK);
            vb.vertex(pose, 0, (float) aabb.minY, 0).color(0, 0, 0, 128).uv2(0, 0xF0).endVertex();
            vb.vertex(pose, 0, (float) aabb.maxY, 0).color(0, 0, 0, 128).uv2(0, 0xF0).endVertex();

            vb = buffer.getBuffer(STEM_WHITE);
            vb.vertex(pose, 0, (float) aabb.minY, 0).color(255, 255, 255, 128).uv2(0, 0xF0).endVertex();
            vb.vertex(pose, 0, (float) aabb.maxY, 0).color(255, 255, 255, 128).uv2(0, 0xF0).endVertex();


            PlayerEntity player = Minecraft.getInstance().player;
            float        dst    = 0.5F;
            if( player != null ) {
                double aabbSize = aabb.getSize();
                double tDst = turret.get().distanceTo(player);
                if( tDst > aabbSize ) {
                    dst += 0.5F * Math.floor(tDst / aabbSize * 2.0D) - 0.5F;
                }
            }

            vb = buffer.getBuffer(FRAME_BLACK);
            for( float c = (float) aabb.minX; c <= aabb.maxX; c += dst ) { renderRangeYZ(vb, pose, aabb, c, 0, 0, 0); }
            for( float c = (float) aabb.minY; c <= aabb.maxY; c += dst ) { renderRangeXZ(vb, pose, aabb, c, 0, 0, 0); }
            for( float c = (float) aabb.minZ; c <= aabb.maxZ; c += dst ) { renderRangeXY(vb, pose, aabb, c, 0, 0, 0); }

            vb = buffer.getBuffer(FRAME_WHITE);
            for( float c = (float) aabb.minX; c <= aabb.maxX; c += dst ) { renderRangeYZ(vb, pose, aabb, c, 255, 255, 255); }
            for( float c = (float) aabb.minY; c <= aabb.maxY; c += dst ) { renderRangeXZ(vb, pose, aabb, c, 255, 255, 255); }
            for( float c = (float) aabb.minZ; c <= aabb.maxZ; c += dst ) { renderRangeXY(vb, pose, aabb, c, 255, 255, 255); }
        }
    }

    private static void renderRangeYZ(IVertexBuilder vb, Matrix4f pose, AxisAlignedBB aabb, float c, int r, int g, int b) {
        float ma = (float) aabb.minY;
        float xa = (float) aabb.maxY;
        float mb = (float) aabb.minZ;
        float xb = (float) aabb.maxZ;

        vert(r, g, b, () -> vb.vertex(pose, c, ma, mb), () -> vb.vertex(pose, c, ma, xb), () -> vb.vertex(pose, c, xa, mb), () -> vb.vertex(pose, c, xa, xb));
    }

    private static void renderRangeXZ(IVertexBuilder vb, Matrix4f pose, AxisAlignedBB aabb, float c, int r, int g, int b) {
        float ma = (float) aabb.minX;
        float xa = (float) aabb.maxX;
        float mb = (float) aabb.minZ;
        float xb = (float) aabb.maxZ;

        vert(r, g, b, () -> vb.vertex(pose, ma, c, mb), () -> vb.vertex(pose, ma, c, xb), () -> vb.vertex(pose, xa, c, mb), () -> vb.vertex(pose, xa, c, xb));
    }

    private static void renderRangeXY(IVertexBuilder vb, Matrix4f pose, AxisAlignedBB aabb, float c, int r, int g, int b) {
        float ma = (float) aabb.minX;
        float xa = (float) aabb.maxX;
        float mb = (float) aabb.minY;
        float xb = (float) aabb.maxY;

        vert(r, g, b, () -> vb.vertex(pose, ma, mb, c), () -> vb.vertex(pose, ma, xb, c), () -> vb.vertex(pose, xa, mb, c), () -> vb.vertex(pose, xa, xb, c));
    }

    @SuppressWarnings("DuplicatedCode")
    private static void vert(int r, int g, int b,
                             Supplier<IVertexBuilder> oo, Supplier<IVertexBuilder> oi,
                             Supplier<IVertexBuilder> io, Supplier<IVertexBuilder> ii)
    {
        oo.get().color(r, g, b, 128).uv2(0, 0xF0).endVertex();
        oi.get().color(r, g, b, 128).uv2(0, 0xF0).endVertex();

        io.get().color(r, g, b, 128).uv2(0, 0xF0).endVertex();
        ii.get().color(r, g, b, 128).uv2(0, 0xF0).endVertex();

        oo.get().color(r, g, b, 128).uv2(0, 0xF0).endVertex();
        io.get().color(r, g, b, 128).uv2(0, 0xF0).endVertex();

        oi.get().color(r, g, b, 128).uv2(0, 0xF0).endVertex();
        ii.get().color(r, g, b, 128).uv2(0, 0xF0).endVertex();
    }
}
