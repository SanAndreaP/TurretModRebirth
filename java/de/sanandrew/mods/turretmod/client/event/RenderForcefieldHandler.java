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
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RenderForcefieldHandler
{
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        final EntityPlayerSP playerSP = Minecraft.getMinecraft().thePlayer;
        List<Entity> renderedTurrets = SAPUtils.getCasted(Minecraft.getMinecraft().theWorld.loadedEntityList);
        renderedTurrets = Lists.newArrayList(Iterators.filter(Iterators.forArray(renderedTurrets.toArray(new Entity[renderedTurrets.size()])), new Predicate<Entity>()
                                                              {
                                                                  @Override
                                                                  public boolean apply(Entity input) {
                                                                      return input instanceof EntityTurretBase && input.isInRangeToRender3d(playerSP.posX, playerSP.posY, playerSP.posZ);
                                                                  }
                                                              }
                                             ));

        EntityLivingBase entitylivingbase1 = Minecraft.getMinecraft().renderViewEntity;
        double d3 = entitylivingbase1.lastTickPosX + (entitylivingbase1.posX - entitylivingbase1.lastTickPosX) * (double)event.partialTicks;
        double d4 = entitylivingbase1.lastTickPosY + (entitylivingbase1.posY - entitylivingbase1.lastTickPosY) * (double)event.partialTicks;
        double d5 = entitylivingbase1.lastTickPosZ + (entitylivingbase1.posZ - entitylivingbase1.lastTickPosZ) * (double)event.partialTicks;

        Tessellator tess = Tessellator.instance;

//        Map<Entity, Vec3[][]> vecs = new HashMap<>();
//
//        for( Entity e : renderedTurrets ) {
//            Vec3[][] vectors = new Vec3[16][16];
//            vectors[0][0] = Vec3.createVectorHelper(0.0F, -8.0F, 0.0F);
//            for( int j = 1; j < vectors.length; j++ ) {
//                vectors[j][0] = Vec3.createVectorHelper(vectors[0][0].xCoord, vectors[0][0].yCoord, vectors[0][0].zCoord);
//                vectors[j][0].rotateAroundZ(-(float) Math.PI / (vectors.length * 1.0F) * (j + 1));
//            }
//
//            for( int i = 1; i < vectors[0].length; i++ ) {
//                vectors[0][i] = Vec3.createVectorHelper(vectors[0][0].xCoord, vectors[0][0].yCoord, vectors[0][0].zCoord);
//                vectors[0][i].rotateAroundY((float) Math.PI / (vectors[0].length / 2.0F) * i);
//                for( int j = 1; j < vectors.length; j++ ) {
//                    vectors[j][i] = Vec3.createVectorHelper(vectors[j][0].xCoord, 0.0F, vectors[j][0].zCoord);
//                    vectors[j][i].rotateAroundY((float) Math.PI / (vectors[0].length / 2.0F) * i);
//                    vectors[j][i] = vectors[j][i].addVector(0.0F, vectors[j][0].yCoord, 0.0F);
//                }
//            }
//
//            vecs.put(e, vectors);
//        }
//
//
//        GL11.glEnable(GL11.GL_BLEND);
//        OpenGlHelper.glBlendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
////        GL11.glDisable(GL11.GL_CULL_FACE);
////        GL11.glDepthMask(false);
//
//
//        Collections.reverse(renderedTurrets);
//        GL11.glDepthFunc(GL11.GL_NOTEQUAL);
//        for( Entity e : renderedTurrets ) {
//            tess.startDrawingQuads();
//            tess.setColorRGBA_F(1.0F, 0.0F, 0.0F, 0.5F);
//            if( e instanceof EntityTurretRevolver ) {
//                tess.setColorRGBA_F(0.0F, 0.0F, 1.0F, 0.5F);
//            }
//            Vec3[][] vectors = vecs.get(e);
//            for( int j = 0; j < vectors.length - 1; j++ ) {
//                for( int i = 0; i < vectors[0].length; i++ ) {
//                    int endI = i < vectors[0].length - 1 ? i + 1 : 0;
//
//                    Vec3 beginVecBtm = vectors[j][i];
//                    Vec3 beginVecTop = vectors[j + 1][i];
//                    Vec3 endVecBtm = vectors[j][endI];
//                    Vec3 endVecTop = vectors[j + 1][endI];
//
//                    tess.addVertex(e.posX - d3 + beginVecBtm.xCoord, e.posY - d4 + beginVecBtm.yCoord, e.posZ - d5 + beginVecBtm.zCoord);
//                    tess.addVertex(e.posX - d3 + endVecBtm.xCoord, e.posY - d4 + endVecBtm.yCoord, e.posZ - d5 + endVecBtm.zCoord);
//                    tess.addVertex(e.posX - d3 + endVecTop.xCoord, e.posY - d4 + endVecTop.yCoord, e.posZ - d5 + endVecTop.zCoord);
//                    tess.addVertex(e.posX - d3 + beginVecTop.xCoord, e.posY - d4 + beginVecTop.yCoord, e.posZ - d5 + beginVecTop.zCoord);
//                }
//            }
//
//            tess.draw();
//        }
//        GL11.glDepthFunc(GL11.GL_LEQUAL);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glDepthMask(true);
//        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private static class CubeFace {
        public final ForgeDirection facing;
        public final Vec3 beginPt;
        public final Vec3 endPt;

        public CubeFace(ForgeDirection direction, Vec3 begin, Vec3 end) {
            this.facing = direction;
            this.beginPt = begin;
            this.endPt = end;
        }
    }

    private static class Cube {
        public Map<ForgeDirection, CubeFace[]> faces = new EnumMap<>(ForgeDirection.class);

        public Cube(Vec3 center, double mpRadius) {
            faces.put(ForgeDirection.NORTH, new CubeFace[] {new CubeFace(ForgeDirection.NORTH, Vec3.createVectorHelper(center.xCoord))})
        }
    }
}
