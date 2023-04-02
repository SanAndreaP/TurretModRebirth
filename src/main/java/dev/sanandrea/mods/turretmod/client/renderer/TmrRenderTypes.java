/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.OptionalDouble;

public class TmrRenderTypes
        extends RenderType
{
    public static final RenderType TMR_LIGHTNING = lightning();
    public static final RenderType ASSEMBLY_LASER = create("tmr_assembly_laser", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, true,
                                                           RenderType.State.builder().setWriteMaskState(COLOR_DEPTH_WRITE).setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                                                                   .setOutputState(ITEM_ENTITY_TARGET).setShadeModelState(SMOOTH_SHADE).createCompositeState(false));

    @SuppressWarnings({"ConstantConditions", "java:S4449"})
    private TmrRenderTypes() {
        super("", null, 0, 0, false, false, null, null);

        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static RenderType tmrLine(final float width) {
        return create(String.format("tmr_lines_%.5f", width), DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
                                 RenderType.State.builder().setLineState(new LineState(OptionalDouble.of(width)))
                                                 .setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                                                 .setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
    }

//    @Nonnull
//    private static RenderType tmrLightning() {
//        return RenderType.create("tmr_lightning", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true,
//                                 RenderType.State.builder().setWriteMaskState(COLOR_DEPTH_WRITE)
//                                                 .setTransparencyState(LIGHTNING_TRANSPARENCY)
//                                                 .setShadeModelState(SMOOTH_SHADE)
//                                                 .setLightmapState(NO_LIGHTMAP)
//                                                 .createCompositeState(false));
//    }

    public static RenderType tmrShield(ResourceLocation texture, boolean cull, float txX, float txY) {
        return create("tmr_shield", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, true,
                      RenderType.State.builder().setTextureState(new TextureState(texture, false, false))
                                      .setTexturingState(getMovingTexturingState(txX, txY))
                                      .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                                      .setDiffuseLightingState(NO_DIFFUSE_LIGHTING)
                                      .setAlphaState(DEFAULT_ALPHA)
                                      .setLightmapState(NO_LIGHTMAP)
                                      .setOverlayState(NO_OVERLAY)
                                      .setFogState(NO_FOG)
                                      .setWriteMaskState(COLOR_WRITE)
                                      .createCompositeState(true));
    }

    @SuppressWarnings("deprecation")
    private static TexturingState getMovingTexturingState(float txMultiX, float txMultiY) {
        return new TexturingState("tmr_moving_texture", () -> {
                RenderSystem.matrixMode(GL11.GL_TEXTURE);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                float f = Util.getMillis() / 150.0F;
                RenderSystem.translatef(f * txMultiX, f * txMultiY, 0.0F);
                RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            }, () -> {
                RenderSystem.matrixMode(GL11.GL_TEXTURE);
                RenderSystem.popMatrix();
                RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            });
    }
}
