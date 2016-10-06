/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.sanlib.lib.client.ColorObj;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import de.sanandrew.mods.turretmod.client.render.ForcefieldCube;
import de.sanandrew.mods.turretmod.client.util.ForcefieldProvider;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RenderForcefieldHandler
{
    public static final RenderForcefieldHandler INSTANCE = new RenderForcefieldHandler();

    private List<ForcefieldFadeOut> fadeOutFields = new ArrayList<>();
    private Map<Integer, ForcefieldProvider> fieldProviders = new HashMap<>();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
        if( renderEntity == null ) {
            return;
        }

        final float partialTicks = event.getPartialTicks();
        double renderX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * partialTicks;
        double renderY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * partialTicks;
        double renderZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * partialTicks;

        List<ForcefieldCube> cubes = new ArrayList<>();

        int worldTicks = (int) (Minecraft.getMinecraft().theWorld.getTotalWorldTime() % Integer.MAX_VALUE);

        Iterator<Map.Entry<Integer, ForcefieldProvider>> it = fieldProviders.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<Integer, ForcefieldProvider> entry = it.next();
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entry.getKey());
            ForcefieldProvider ffProvider = entry.getValue();

            if( entity == null ) {
                it.remove();
                continue;
            }

            if( entity.isDead || !entity.isEntityAlive() || !ffProvider.hasShieldActive() || !Minecraft.getMinecraft().theWorld.loadedEntityList.contains(entity) ) {
                this.fadeOutFields.add(new ForcefieldFadeOut(entity.posX, entity.posY, entity.posZ, ffProvider.getShieldColor(), ffProvider.getShieldBoundingBox()));
                it.remove();
            } else {
                double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
                double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
                double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

                ForcefieldCube cube = new ForcefieldCube(new Vec3d(entityX - renderX, entityY - renderY, entityZ - renderZ), ffProvider.getShieldBoundingBox(), ffProvider.getShieldColor());

                if( TmrConfiguration.calcForcefieldIntf ) {
                    for( ForcefieldCube intfCube : cubes ) {
                        cube.interfere(intfCube, false);
                        intfCube.interfere(cube, true);
                    }
                }

                cubes.add(cube);
            }
        }

        Iterator<ForcefieldFadeOut> fadeOutIt = this.fadeOutFields.iterator();
        while( fadeOutIt.hasNext() ) {
            ForcefieldFadeOut shield = fadeOutIt.next();
            if( shield.color.alpha() <= 0 ) {
                fadeOutIt.remove();
            } else {
                ForcefieldCube cube = new ForcefieldCube(new Vec3d(shield.posX - renderX, shield.posY - renderY, shield.posZ - renderZ), shield.shieldBB, shield.color);

                cubes.add(cube);

                shield.color.setAlpha(shield.color.alpha() - 3);
            }
        }

        Tessellator tess = Tessellator.getInstance();
        for( int pass = 1; pass <= 5; pass++ ) {
            float transformTexAmount = worldTicks % 400 + event.getPartialTicks();
            float texTranslateX = 0.0F;
            float texTranslateY = 0.0F;

            switch( pass ) {
                case 1:
                    texTranslateX = transformTexAmount * -0.01F;
                    texTranslateY = transformTexAmount * 0.01F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P1.getResource());
                    break;
                case 2:
                    texTranslateX = transformTexAmount * 0.005F;
                    texTranslateY = transformTexAmount * 0.005F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P2.getResource());
                    break;
                case 3:
                    texTranslateX = transformTexAmount * -0.005F;
                    texTranslateY = transformTexAmount * 0.005F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P1.getResource());
                    break;
                case 4:
                    texTranslateX = transformTexAmount * 0.0025F;
                    texTranslateY = transformTexAmount * 0.0025F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P2.getResource());
                    break;
                case 5:
                    texTranslateX = transformTexAmount * 0.00F;
                    texTranslateY = transformTexAmount * 0.00F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P3.getResource());
                    break;
            }

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.translate(texTranslateX, texTranslateY, 0.0F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);

            for( ForcefieldCube cube : cubes ) {
                tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

                cube.draw(tess);

                GL14.glBlendColor(1.0f, 1.0f, 1.0f, cube.boxColor.fAlpha() * 0.5F);
                GlStateManager.depthMask(false);
                GlStateManager.disableCull();
                tess.draw();
                GlStateManager.enableCull();
                GlStateManager.depthMask(true);
                GL14.glBlendColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }

    public void addForcefieldRenderer(Entity entity, ForcefieldProvider provider) {
        this.fieldProviders.put(entity.getEntityId(), provider);
    }

    private static class ForcefieldFadeOut
    {
        public ColorObj color;
        public final AxisAlignedBB shieldBB;

        public double posX;
        public double posY;
        public double posZ;

        public ForcefieldFadeOut(double posX, double posY, double posZ, ColorObj origRGBA, AxisAlignedBB origShieldBB) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.color = origRGBA;
            this.shieldBB = origShieldBB;
        }
    }
}
