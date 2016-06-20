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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.helpers.SAPUtils.RGBAValues;
import de.sanandrew.mods.turretmod.api.ForcefieldProvider;
import de.sanandrew.mods.turretmod.client.render.ForcefieldCube;
import de.sanandrew.mods.turretmod.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RenderForcefieldHandler
{
    private List<Entity> currFfEntities = new ArrayList<>();
    private List<ForcefieldFadeOut> fadeOutFfields = new ArrayList<>();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        List<Entity> ffEntities = SAPUtils.getCasted(Minecraft.getMinecraft().theWorld.loadedEntityList);
        ffEntities = Lists.newArrayList(Iterators.filter(Iterators.forArray(ffEntities.toArray(new Entity[ffEntities.size()])), PredicateRenderedEntities.INSTANCE));

        EntityLivingBase renderEntity = Minecraft.getMinecraft().renderViewEntity;
        double renderX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * event.partialTicks;
        double renderY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * event.partialTicks;
        double renderZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * event.partialTicks;

        ArrayList<ForcefieldCube> cubes = new ArrayList<>();

        int worldTicks = (int) (Minecraft.getMinecraft().theWorld.getTotalWorldTime() % Integer.MAX_VALUE);

        for( Entity entity : ffEntities ) {
            ForcefieldProvider ffProvider = (ForcefieldProvider) entity;

            double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks;
            double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks;
            double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks;

            ForcefieldCube cube = new ForcefieldCube(Vec3.createVectorHelper(entityX - renderX, entityY - renderY, entityZ - renderZ),
                                                     ffProvider.getShieldBoundingBox(), SAPUtils.getRgbaFromColorInt(ffProvider.getShieldColor()));

            if( !entity.isEntityAlive() || !ffProvider.hasShieldActive() ) {
                continue;
            }

            if( !this.currFfEntities.contains(entity) ) {
                this.currFfEntities.add(entity);
            }

            for( ForcefieldCube intfCube : cubes ) {
                cube.interfere(intfCube, false);
                intfCube.interfere(cube, true);
            }

            cubes.add(cube);
        }

        Iterator<Entity> currFfEntitiesIterator = this.currFfEntities.iterator();
        while( currFfEntitiesIterator.hasNext() ) {
            Entity entity = currFfEntitiesIterator.next();
            ForcefieldProvider ffProvider = (ForcefieldProvider) entity;
            if( !entity.isEntityAlive() || !ffProvider.hasShieldActive() || !ffEntities.contains(entity) ) {
                ForcefieldFadeOut ffFadedOut = new ForcefieldFadeOut(entity.posX, entity.posY, entity.posZ, SAPUtils.getRgbaFromColorInt(ffProvider.getShieldColor()),
                                                                     ffProvider.getShieldBoundingBox());
                this.fadeOutFfields.add(ffFadedOut);
                currFfEntitiesIterator.remove();
            }
        }

        Iterator<ForcefieldFadeOut> fadeOutFfieldsIterator = this.fadeOutFfields.iterator();
        while( fadeOutFfieldsIterator.hasNext() ) {
            ForcefieldFadeOut shield = fadeOutFfieldsIterator.next();
            if( shield.rgbaValues.getAlpha() <= 0 ) {
                fadeOutFfieldsIterator.remove();
            } else {
                ForcefieldCube cube = new ForcefieldCube(Vec3.createVectorHelper(shield.posX - renderX, shield.posY - renderY, shield.posZ - renderZ),
                                                         shield.shieldBB, shield.rgbaValues);

                cubes.add(cube);

                shield.rgbaValues = new RGBAValues(shield.rgbaValues.getRed(), shield.rgbaValues.getGreen(), shield.rgbaValues.getBlue(),
                                                   Math.max(0, shield.rgbaValues.getAlpha() - 10));
            }
        }

        Tessellator tess = Tessellator.instance;
        for( int pass = 1; pass <= 5; pass++ ) {
            tess.startDrawingQuads();

            for( ForcefieldCube cube : cubes ) {
                cube.draw(tess);
            }

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_CULL_FACE);

            float transformTexAmount = worldTicks + event.partialTicks;
            float texTranslateX = 0.0F;
            float texTranslateY = 0.0F;

            switch( pass ) {
                case 1:
                    texTranslateX = transformTexAmount * -0.011F;
                    texTranslateY = transformTexAmount * 0.011F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Textures.TURRET_FORCEFIELD_P1.getResource());
                    break;
                case 2:
                    texTranslateX = transformTexAmount * 0.009F;
                    texTranslateY = transformTexAmount * 0.009F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Textures.TURRET_FORCEFIELD_P2.getResource());
                    break;
                case 3:
                    texTranslateX = transformTexAmount * -0.007F;
                    texTranslateY = transformTexAmount * 0.007F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Textures.TURRET_FORCEFIELD_P1.getResource());
                    break;
                case 4:
                    texTranslateX = transformTexAmount * 0.005F;
                    texTranslateY = transformTexAmount * 0.005F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Textures.TURRET_FORCEFIELD_P2.getResource());
                    break;
                case 5:
                    texTranslateX = transformTexAmount * 0.00F;
                    texTranslateY = transformTexAmount * 0.00F;
                    Minecraft.getMinecraft().renderEngine.bindTexture(Textures.TURRET_FORCEFIELD_P3.getResource());
                    break;
            }

            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GL11.glTranslatef(texTranslateX, texTranslateY, 0.0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);

            tess.draw();

            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    private static class PredicateRenderedEntities
            implements Predicate<Entity>
    {
        private static final PredicateRenderedEntities INSTANCE = new PredicateRenderedEntities();

        @Override
        public boolean apply(Entity input) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            return input instanceof ForcefieldProvider && input.isInRangeToRender3d(player.posX, player.posY, player.posZ);
        }
    }

    private static class ForcefieldFadeOut
    {
        public RGBAValues rgbaValues;
        public final AxisAlignedBB shieldBB;

        public double posX;
        public double posY;
        public double posZ;

        public ForcefieldFadeOut(double posX, double posY, double posZ, RGBAValues origRGBA, AxisAlignedBB origShieldBB) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.rgbaValues = origRGBA;
            this.shieldBB = origShieldBB;
        }
    }
}
