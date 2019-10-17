/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.layer;

import de.sanandrew.mods.sanlib.lib.XorShiftRandom;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.event.ClientTickHandler;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import org.lwjgl.opengl.GL11;

import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LayerTurretShieldLightning<E extends EntityLiving & ITurretInst>
        implements LayerRenderer<E>
{
    private static final WeakHashMap<ITurretInst, Queue<RenderLightning>> LIGHTNING_RENDERS = new WeakHashMap<>();

    @Override
    public void doRenderLayer(E turretInst, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        LIGHTNING_RENDERS.forEach((key, value) -> value.removeIf(RenderLightning::finished));
        LIGHTNING_RENDERS.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        if( turretInst.isActive() ) {
            if( MiscUtils.RNG.randomInt(150) == 0 && !Minecraft.getMinecraft().isGamePaused() ) {
                LIGHTNING_RENDERS.computeIfAbsent(turretInst, inst -> new ConcurrentLinkedQueue<>()).add(new RenderLightning());
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.01D, 0.01D, 0.01D);
        GlStateManager.translate(0.0F, -9*16, 0.0F);
        float[] prevBright = ClientProxy.forceGlow();
        LIGHTNING_RENDERS.entrySet().stream().filter(entry -> entry.getKey() == turretInst).forEach(entry -> entry.getValue().forEach(val -> val.doRender(partialTicks)));
        ClientProxy.resetGlow(prevBright);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
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

        void doRender(float partTicks) {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

            Tessellator tess = Tessellator.getInstance();
            this.renderLightning(tess, partTicks);

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }

        void renderLightning(Tessellator tess, float partTicks) {
            double xPos[] = new double[8];
            double zPos[] = new double[8];
            double maxX = 0.0D;
            double maxZ = 0.0D;
            XorShiftRandom rngMain = new XorShiftRandom(this.seed);
            BufferBuilder buf = tess.getBuffer();

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

                        buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                        double maxDither = 0.1D + j * 0.2D;

                        if( k == 0 ) {
                            maxDither *= level * 0.1D + 1.0D;
                        }

                        double minDither = 0.1D + j * 0.2D;

                        if( k == 0 ) {
                            minDither *= (level - 1) * 0.1D + 1.0D;
                        }

                        for( int k1 = 0; k1 < 5; k1++ ) {
                            double xTwigMax = 0.5D - maxDither;
                            double zTwigMax = 0.5D - maxDither;

                            if( k1 == 1 || k1 == 2 ) {
                                xTwigMax += maxDither * 2.0D;
                            }

                            if( k1 == 2 || k1 == 3 ) {
                                zTwigMax += maxDither * 2.0D;
                            }

                            double xTwigMin = 0.5D - minDither;
                            double zTwigMin = 0.5D - minDither;

                            if( k1 == 1 || k1 == 2 ) {
                                xTwigMin += minDither * 2.0D;
                            }

                            if( k1 == 2 || k1 == 3 ) {
                                zTwigMin += minDither * 2.0D;
                            }

                            float lum = 0.5F;
                            float alpha = (this.ticksVisible - ClientTickHandler.ticksInGame - partTicks) / MAX_TICKS_VISIBLE;
                            buf.pos(xTwigMin + minX,    level * 16,       zTwigMin + minZ)   .color(0.9F * lum, 0.2F * lum, 0.2F * lum, alpha).endVertex();
                            buf.pos(xTwigMax + xBranch, (level + 1) * 16, zTwigMax + zBranch).color(0.9F * lum, 0.2F * lum, 0.2F * lum, alpha).endVertex();
                        }

                        tess.draw();
                    }
                }
            }
        }
    }
}
