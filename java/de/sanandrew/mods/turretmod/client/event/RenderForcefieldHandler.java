/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.sanandrew.mods.turretmod.client.render.ForcefieldCube;
import de.sanandrew.mods.turretmod.client.util.ForcefieldProvider;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.darkhax.bookshelf.lib.ColorObject;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class RenderForcefieldHandler
{
    public static final RenderForcefieldHandler INSTANCE = new RenderForcefieldHandler();

    private List<ForcefieldFadeOut> fadeOutFields = new ArrayList<>();
    private Map<Integer, ForcefieldProvider> fieldProviders = new HashMap<>();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        EntityLivingBase renderEntity = Minecraft.getMinecraft().renderViewEntity;
        double renderX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * event.partialTicks;
        double renderY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * event.partialTicks;
        double renderZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * event.partialTicks;

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
                double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks;
                double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks;
                double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks;

                ForcefieldCube cube = new ForcefieldCube(Vec3.createVectorHelper(entityX - renderX, entityY - renderY, entityZ - renderZ), ffProvider.getShieldBoundingBox(), ffProvider.getShieldColor());

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
            if( shield.color.getAlpha() <= 0.0F ) {
                fadeOutIt.remove();
            } else {
                ForcefieldCube cube = new ForcefieldCube(Vec3.createVectorHelper(shield.posX - renderX, shield.posY - renderY, shield.posZ - renderZ), shield.shieldBB, shield.color);

                cubes.add(cube);

                shield.color.setAlpha(shield.color.getAlpha() - 0.01F);
            }
        }

        Tessellator tess = Tessellator.instance;
        for( int pass = 1; pass <= 5; pass++ ) {


            float transformTexAmount = worldTicks % 400 + event.partialTicks;
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

            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GL11.glTranslatef(texTranslateX, texTranslateY, 0.0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            for( ForcefieldCube cube : cubes ) {
                tess.startDrawingQuads();

                cube.draw(tess);

                GL14.glBlendColor(1.0f, 1.0f, 1.0f, cube.boxColor.getAlpha());
                GL11.glDepthMask(false);
                GL11.glDisable(GL11.GL_CULL_FACE);
                tess.draw();
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_CULL_FACE);
            }

            GL11.glDisable(GL11.GL_BLEND);

            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    public void addForcefieldRenderer(Entity entity, ForcefieldProvider provider) {
        this.fieldProviders.put(entity.getEntityId(), provider);
    }

    private static class ForcefieldFadeOut
    {
        public ColorObject color;
        public final AxisAlignedBB shieldBB;

        public double posX;
        public double posY;
        public double posZ;

        public ForcefieldFadeOut(double posX, double posY, double posZ, ColorObject origRGBA, AxisAlignedBB origShieldBB) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.color = origRGBA;
            this.shieldBB = origShieldBB;
        }
    }
}
