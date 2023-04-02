/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.renderer.turret.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.XorShiftRandom;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.event.ClientTickHandler;
import de.sanandrew.mods.turretmod.client.renderer.TmrRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LayerTurretShieldLightning<E extends LivingEntity & ITurretEntity, M extends EntityModel<E>>
        extends LayerRenderer<E, M>
{
    private static final WeakHashMap<ITurretEntity, Queue<RenderLightning>> LIGHTNING_RENDERS = new WeakHashMap<>();

    public LayerTurretShieldLightning(IEntityRenderer<E, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(@Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int packedLight, E turretInst,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
//        if( turretInst.isInGui() ) {
//            return;
//        }

        LIGHTNING_RENDERS.forEach((key, value) -> value.removeIf(RenderLightning::finished));
        LIGHTNING_RENDERS.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        if( turretInst.isActive() ) {
            if( MiscUtils.RNG.randomInt(150) == 0 && !Minecraft.getInstance().isPaused() ) {
                LIGHTNING_RENDERS.computeIfAbsent(turretInst, inst -> new ConcurrentLinkedQueue<>()).add(new RenderLightning());
            }
        }

        //TODO: check for correct scaling and translation...
//        stack.push();
//        stack.scale(0.01F, 0.01F, 0.01F);
//        stack.translate(0.0F, -9*16, 0.0F);
        LIGHTNING_RENDERS.entrySet().stream().filter(entry -> entry.getKey() == turretInst).forEach(entry -> entry.getValue().forEach(val -> val.doRender(buffer, partialTicks)));
//        stack.pop();
    }

    private static class RenderLightning
    {
        private static final int MAX_TICKS_VISIBLE = 20;

        private final int ticksVisible;
        private final long seed;

        RenderLightning() {
            this.ticksVisible = ClientTickHandler.ticksInGame + MAX_TICKS_VISIBLE;
            this.seed = MiscUtils.RNG.randomLong();
        }

        boolean finished() {
            return this.ticksVisible <= ClientTickHandler.ticksInGame;
        }

        void doRender(IRenderTypeBuffer builder, float partTicks) {
//            RenderSystem.disableTexture();
//            RenderSystem.disableLighting();
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

//            Tessellator tess = Tessellator.getInstance();
            this.renderLightning(builder, partTicks);

//            RenderSystem.disableBlend();
//            RenderSystem.enableLighting();
//            RenderSystem.enableTexture();
        }

        void renderLightning(IRenderTypeBuffer builder, float partTicks) {
            double[] xPos = new double[8];
            double[] zPos = new double[8];
            double   maxX = 0.0D;
            double   maxZ = 0.0D;
            XorShiftRandom rngMain = new XorShiftRandom(this.seed);
//            BufferBuilder buf = tess.getBuffer();

            for( int i = 7; i >= 0; i-- ) {
                xPos[i] = maxX;
                zPos[i] = maxZ;
                maxX += rngMain.randomInt(11) - 5;
                maxZ += rngMain.randomInt(11) - 5;
            }

            for( int j = 0; j < 4; j++ ) {
                XorShiftRandom rngBranch = new XorShiftRandom(this.seed);

                for( int k = 0; k < 3; k++ ) {
                    int maxLvl = 7;
                    int minLvl = rngBranch.randomInt(7);

                    double minX = xPos[maxLvl] - maxX;
                    double minZ = zPos[maxLvl] - maxZ;

                    for( int level = maxLvl; level >= minLvl; level-- ) {
                        double xBranch = minX;
                        double zBranch = minZ;

                        if( k == 0 ) {
                            minX += rngBranch.randomInt(10) - 5;
                            minZ += rngBranch.randomInt(10) - 5;
                        } else {
                            minX += rngBranch.randomInt(31) - 15;
                            minZ += rngBranch.randomInt(31) - 15;
                        }

                        IVertexBuilder twigBuffer = builder.getBuffer(TmrRenderTypes.TMR_LIGHTNING);
//                        buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
//                        builder.

                        double maxDither = 0.1D + j * 0.2D;

                        if( k == 0 ) {
                            maxDither *= level * 0.1D + 1.0D;
                        }

                        double minDither = 0.1D + j * 0.2D;

                        if( k == 0 ) {
                            minDither *= (level - 1) * 0.1D + 1.0D;
                        }

                        for( int k1 = 0; k1 < 5; k1++ ) {
                            double xTwigMax = doTwigDither(maxDither, k1, 1, 2);
                            double zTwigMax = doTwigDither(maxDither, k1, 2, 3);

                            double xTwigMin = doTwigDither(minDither, k1, 1, 2);
                            double zTwigMin = doTwigDither(minDither, k1, 2, 3);

                            float lum = 0.5F;
                            float alpha = (this.ticksVisible - ClientTickHandler.ticksInGame - partTicks) / MAX_TICKS_VISIBLE;
                            twigBuffer.vertex(xTwigMin + minX,    level * 16,       zTwigMin + minZ)   .color(0.9F * lum, 0.8F * lum, 0.1F * lum, alpha).uv2(0, 0xF0).endVertex();
                            twigBuffer.vertex(xTwigMax + xBranch, (level + 1) * 16, zTwigMax + zBranch).color(0.9F * lum, 0.8F * lum, 0.1F * lum, alpha).uv2(0, 0xF0).endVertex();
                        }

//                        tess.draw;
                    }
                }
            }
        }
    }

    private static double doTwigDither(double dither, int level, int minDitherLevel, int maxDitherLevel) {
        double twigVal = 0.5 - dither;
        if( level == minDitherLevel || level == maxDitherLevel ) {
            twigVal += dither * 2.0D;
        }

        return twigVal;
    }
}
