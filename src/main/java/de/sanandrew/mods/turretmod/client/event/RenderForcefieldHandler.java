/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.turret.IForcefieldProvider;
import de.sanandrew.mods.turretmod.client.render.ForcefieldCube;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderForcefieldHandler
{
    public static final RenderForcefieldHandler INSTANCE = new RenderForcefieldHandler();

    private final List<ForcefieldCube> fadeOutFields = new ArrayList<>();
    private final Map<Integer, IForcefieldProvider> fieldProviders = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity renderEntity = mc.getRenderViewEntity();
        if( renderEntity == null ) {
            return;
        }

        final float partialTicks = event.getPartialTicks();
        double renderX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * partialTicks;
        double renderY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * partialTicks;
        double renderZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * partialTicks;

        List<ForcefieldCube> cubes = new ArrayList<>();

        int worldTicks = (int) (mc.world.getTotalWorldTime() % Integer.MAX_VALUE);

        Iterator<Map.Entry<Integer, IForcefieldProvider>> it = this.fieldProviders.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<Integer, IForcefieldProvider> entry = it.next();
            Entity entity = mc.world.getEntityByID(entry.getKey());
            IForcefieldProvider ffProvider = entry.getValue();

            if( entity == null ) {
                it.remove();
                continue;
            }

            ColorObj color = new ColorObj(ffProvider.getShieldColor());

            double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

            ForcefieldCube cube = new ForcefieldCube(new Vec3d(entityX - renderX, entityY - renderY, entityZ - renderZ), ffProvider.getShieldBoundingBox(), color);

            if( entity.isDead || !entity.isEntityAlive() || !ffProvider.isShieldActive() || !mc.world.loadedEntityList.contains(entity) ) {
                if( ffProvider.hasSmoothFadeOut() ) {
                    this.fadeOutFields.add(cube);
                }
                it.remove();
            } else {
                if( TmrConfiguration.calcForcefieldIntf ) {
                    for( ForcefieldCube intfCube : cubes ) {
                        cube.interfere(intfCube, false);
                        intfCube.interfere(cube, true);
                    }
                }

                cubes.add(cube);
            }
        }

        Iterator<ForcefieldCube> fadeOutIt = this.fadeOutFields.iterator();
        while( fadeOutIt.hasNext() ) {
            ForcefieldCube shield = fadeOutIt.next();
            if( shield.boxColor.alpha() <= 0 ) {
                fadeOutIt.remove();
            } else {
//                ForcefieldCube cube = new ForcefieldCube(new Vec3d(shield.posX - renderX, shield.posY - renderY, shield.posZ - renderZ), shield.shieldBB, shield.color);

                cubes.add(shield);

                shield.boxColor.setAlpha(shield.boxColor.alpha() - 3);
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
                    mc.renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P1.getResource());
                    break;
                case 2:
                    texTranslateX = transformTexAmount * 0.005F;
                    texTranslateY = transformTexAmount * 0.005F;
                    mc.renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P2.getResource());
                    break;
                case 3:
                    texTranslateX = transformTexAmount * -0.005F;
                    texTranslateY = transformTexAmount * 0.005F;
                    mc.renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P1.getResource());
                    break;
                case 4:
                    texTranslateX = transformTexAmount * 0.0025F;
                    texTranslateY = transformTexAmount * 0.0025F;
                    mc.renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P2.getResource());
                    break;
                case 5:
                    texTranslateX = transformTexAmount * 0.00F;
                    texTranslateY = transformTexAmount * 0.00F;
                    mc.renderEngine.bindTexture(Resources.TURRET_FORCEFIELD_P3.getResource());
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

    @SuppressWarnings("unused")
    public void addForcefieldRenderer(Entity entity, IForcefieldProvider provider) {
        this.fieldProviders.put(entity.getEntityId(), provider);
    }

//    private static class ForcefieldFadeOut
//            extends ForcefieldCube
//    {
//        public ColorObj color;
//
//        public double posX;
//        public double posY;
//        public double posZ;
//
//        ForcefieldFadeOut(double posX, double posY, double posZ, ColorObj origRGBA, AxisAlignedBB origShieldBB) {
//            this.posX = posX;
//            this.posY = posY;
//            this.posZ = posZ;
//            this.color = origRGBA;
//        }
//    }
}
