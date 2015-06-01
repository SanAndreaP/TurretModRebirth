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
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

public class RenderForcefieldHandler
{
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {

        final EntityPlayerSP playerSP = Minecraft.getMinecraft().thePlayer;
        Entity[] renderedTurrets = (Entity[]) Minecraft.getMinecraft().theWorld.loadedEntityList.toArray(new Entity[0]);
        renderedTurrets = Iterators.toArray(Iterators.filter(Iterators.forArray(renderedTurrets), new Predicate<Entity>() {
                                                                 @Override public boolean apply(Entity input) {
                                                                     return input instanceof EntityTurretBase && input.isInRangeToRender3d(playerSP.posX, playerSP.posY, playerSP.posZ);
                                                                 }
                                                             }), Entity.class);

        EntityLivingBase entitylivingbase1 = Minecraft.getMinecraft().renderViewEntity;
        double d3 = entitylivingbase1.lastTickPosX + (entitylivingbase1.posX - entitylivingbase1.lastTickPosX) * (double)event.partialTicks;
        double d4 = entitylivingbase1.lastTickPosY + (entitylivingbase1.posY - entitylivingbase1.lastTickPosY) * (double)event.partialTicks;
        double d5 = entitylivingbase1.lastTickPosZ + (entitylivingbase1.posZ - entitylivingbase1.lastTickPosZ) * (double)event.partialTicks;

        for( Entity e : renderedTurrets ) {
            GL11.glPushMatrix();
            GL11.glTranslated(e.posX - d3, e.posY - d4, e.posZ - d5);

            Vec3[][] vectors = new Vec3[8][16];
            vectors[0][0] = Vec3.createVectorHelper(8.0F, 0.0F, 0.0F);
            for( int i = 1; i < vectors[0].length; i++ ) {
                vectors[0][i] = Vec3.createVectorHelper(vectors[0][0].xCoord, vectors[0][0].yCoord, vectors[0][0].zCoord);
                vectors[0][i].rotateAroundY((float) Math.PI / ( vectors[0].length / 2.0F ) * i);
                for( int j = 1; j < vectors.length; j++ ) {
                    vectors[j][i] = Vec3.createVectorHelper(vectors[0][i].xCoord, vectors[0][i].yCoord, vectors[0][i].zCoord);
                    vectors[j][i].rotateAroundX((float) Math.PI / ( vectors.length ) * j);
                }
            }

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
//            GL11.glCullFace(GL11.GL_FRONT);
            Tessellator tess = Tessellator.instance;
            tess.startDrawingQuads();
            tess.setColorOpaque_F(1.0F, 0.0F, 0.0F);
            for( int i = 0; i < vectors[0].length; i++ ) {
                for( int j = 0; i < vectors.length; j++ ) {
                    Vec3 beginVec = vectors[j][i];
                    Vec3 endVecBtm = i < vectors.length - 1 ? vectors[i + 1] : vectors[0];

                    tess.addVertex(beginVec.xCoord, 0, beginVec.zCoord);
                    tess.addVertex(endVecBtm.xCoord, 0, endVecBtm.zCoord);
                    tess.addVertex(endVecBtm.xCoord, 5, endVecBtm.zCoord);
                    tess.addVertex(beginVec.xCoord, 5, beginVec.zCoord);
                }
            }
            tess.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glCullFace(GL11.GL_BACK);
            GL11.glPopMatrix();
        }
    }
}
