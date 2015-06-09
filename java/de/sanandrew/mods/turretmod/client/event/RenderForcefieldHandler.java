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
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.*;

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

        EntityLivingBase renderEntity = Minecraft.getMinecraft().renderViewEntity;
        double renderX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * (double)event.partialTicks;
        double renderY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * (double)event.partialTicks;
        double renderZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * (double)event.partialTicks;


//        Map<Entity, Vec3[][]> vecs = new HashMap<>();


//        tess.addVertex(renderX, renderY, renderZ);
//        tess.addVertex(renderX + 5, renderY, renderZ);
//        tess.addVertex(renderX + 5, renderY + 5, renderZ);
//        tess.addVertex(renderX, renderY + 5, renderZ);
//        tess.addVertex(0, 0, 0);
//        tess.addVertex(0 + 5, 0, 0);
//        tess.addVertex(0 + 5, 0 + 5, 0);
//        tess.addVertex(0, 0 + 5, 0);
//
        ArrayList<Cube> cubes = new ArrayList<>();

        for( Entity e : renderedTurrets ) {
            double entityX = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)event.partialTicks;
            double entityY = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)event.partialTicks;
            double entityZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)event.partialTicks;

            Cube cube = new Cube(Vec3.createVectorHelper(entityX - renderX, entityY - renderY, entityZ - renderZ), 4.0D);
            for( Cube intfCube : cubes ) {
                cube.interfere(intfCube);
                intfCube.interfere(cube);
            }
            cubes.add(cube);
//
//
//            tess.addVertex(entityX - renderX, entityY - renderY, entityZ - renderZ);
//            tess.addVertex(entityX - renderX + 1, entityY - renderY, entityZ - renderZ);
//            tess.addVertex(entityX - renderX + 1, entityY - renderY + 1, entityZ - renderZ);
//            tess.addVertex(entityX - renderX, entityY - renderY + 1, entityZ - renderZ);

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
        }

        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setColorRGBA_F(1.0F, 0.0F, 0.0F, 0.5F);

        for( Cube cube : cubes ) {
            cube.draw(tess);
        }

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glDisable(GL11.GL_CULL_FACE);
////        GL11.glDepthMask(false);
//
//
//        Collections.reverse(renderedTurrets);
        GL11.glDepthFunc(GL11.GL_NOTEQUAL);
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
//                    tess.addVertex(e.posX - renderX + beginVecBtm.xCoord, e.posY - renderY + beginVecBtm.yCoord, e.posZ - renderZ + beginVecBtm.zCoord);
//                    tess.addVertex(e.posX - renderX + endVecBtm.xCoord, e.posY - renderY + endVecBtm.yCoord, e.posZ - renderZ + endVecBtm.zCoord);
//                    tess.addVertex(e.posX - renderX + endVecTop.xCoord, e.posY - renderY + endVecTop.yCoord, e.posZ - renderZ + endVecTop.zCoord);
//                    tess.addVertex(e.posX - renderX + beginVecTop.xCoord, e.posY - renderY + beginVecTop.yCoord, e.posZ - renderZ + beginVecTop.zCoord);
//                }
//            }
//
            tess.draw();
//        }
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
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

        public CubeFace[] intersect(CubeFace intersector) {
            RectCoords myCoord = this.get2DCoords();
            RectCoords intsCoord = intersector.get2DCoords();


            if( myCoord.endX <= intsCoord.beginX || myCoord.beginX >= intsCoord.endX || myCoord.beginY <= intsCoord.endY || myCoord.endY >= intsCoord.beginY ) {
                return null;
            }

            List<CubeFace> newCubes = new ArrayList<>(4);

            RectCoords topRect = new RectCoords(myCoord.beginX, myCoord.beginY, myCoord.endX, Math.min(intsCoord.beginY, myCoord.beginY));
            RectCoords btmRect = new RectCoords(myCoord.beginX, Math.max(intsCoord.endY, myCoord.endY), myCoord.endX, myCoord.endY);
            RectCoords lftRect = new RectCoords(myCoord.beginX, Math.min(intsCoord.beginY, myCoord.beginY), Math.max(intsCoord.beginX, myCoord.beginX), Math.max(intsCoord.endY, myCoord.endY));
            RectCoords rgtRect = new RectCoords(Math.min(intsCoord.endX, myCoord.endX), Math.min(intsCoord.beginY, myCoord.beginY), myCoord.endX, Math.max(intsCoord.endY, myCoord.endY));

            if( !topRect.isEmptyOrNegative() ) {
                newCubes.add(this.get3DCoords(topRect));
            }
            if( !btmRect.isEmptyOrNegative() ) {
                newCubes.add(this.get3DCoords(btmRect));
            }
            if( !lftRect.isEmptyOrNegative() ) {
                newCubes.add(this.get3DCoords(lftRect));
            }
            if( !rgtRect.isEmptyOrNegative() ) {
                newCubes.add(this.get3DCoords(rgtRect));
            }

            if( newCubes.size() == 0 ) {
                return new CubeFace[0];
            }

            return newCubes.toArray(new CubeFace[newCubes.size()]);
        }

        public RectCoords get2DCoords() {
            switch( this.facing ) {
                case NORTH:
                case SOUTH:
                    return new RectCoords(this.beginPt.zCoord, this.beginPt.yCoord, this.endPt.zCoord, this.endPt.yCoord);
                case EAST:
                case WEST:
                    return new RectCoords(this.beginPt.xCoord, this.beginPt.yCoord, this.endPt.xCoord, this.endPt.yCoord);
                case UP:
                case DOWN:
                    return new RectCoords(this.beginPt.xCoord, this.beginPt.zCoord, this.endPt.xCoord, this.endPt.zCoord);
                default:
                    return new RectCoords(0.0D, 0.0D, 0.0D, 0.0D);
            }
        }

        public CubeFace get3DCoords(RectCoords newRect) {
            switch( this.facing ) {
                case NORTH:
                case SOUTH:
                    return new CubeFace(this.facing, Vec3.createVectorHelper(this.beginPt.xCoord, newRect.beginY, newRect.beginX), Vec3.createVectorHelper(this.endPt.xCoord, newRect.endY, newRect.endX));
                case EAST:
                case WEST:
                    return new CubeFace(this.facing, Vec3.createVectorHelper(newRect.beginX, newRect.beginY, this.beginPt.zCoord), Vec3.createVectorHelper(newRect.endX, newRect.endY, this.endPt.zCoord));
                case UP:
                case DOWN:
                    return new CubeFace(this.facing, Vec3.createVectorHelper(newRect.beginX, this.beginPt.yCoord, newRect.beginY), Vec3.createVectorHelper(newRect.endX, this.endPt.yCoord, newRect.endY));
                default:
                    return null;
            }
        }
    }

    private static class RectCoords {
        public final double beginX;
        public final double beginY;
        public final double endX;
        public final double endY;

        public RectCoords(double xBegin, double yBegin, double xEnd, double yEnd) {
            this.beginX = xBegin;
            this.beginY = yBegin;
            this.endX = xEnd;
            this.endY = yEnd;
        }

        public boolean isEmptyOrNegative() {
            return beginX >= endX && beginY >= endY;
        }
    }

    private static class Cube
    {
        public Map<ForgeDirection, CubeFace[]> faces = new EnumMap<>(ForgeDirection.class);
        private final Vec3 center;
        private final double radius;

        public Cube(Vec3 mpCenter, double mpRadius) {
            this.center = mpCenter;
            this.radius = mpRadius;

            CubeFace face;
            face = new CubeFace(ForgeDirection.NORTH, Vec3.createVectorHelper(center.xCoord + mpRadius, center.yCoord + mpRadius, center.zCoord - mpRadius),
                                Vec3.createVectorHelper(center.xCoord + mpRadius, center.yCoord - mpRadius, center.zCoord + mpRadius)
            );
//            faces.put(ForgeDirection.NORTH, new CubeFace[] {face});
            face = new CubeFace(ForgeDirection.EAST, Vec3.createVectorHelper(center.xCoord - mpRadius, center.yCoord + mpRadius, center.zCoord + mpRadius),
                                Vec3.createVectorHelper(center.xCoord + mpRadius, center.yCoord - mpRadius, center.zCoord + mpRadius)
            );
//            faces.put(ForgeDirection.EAST, new CubeFace[] {face});
            face = new CubeFace(ForgeDirection.SOUTH, Vec3.createVectorHelper(center.xCoord - mpRadius, center.yCoord + mpRadius, center.zCoord - mpRadius),
                                Vec3.createVectorHelper(center.xCoord - mpRadius, center.yCoord - mpRadius, center.zCoord + mpRadius)
            );
            faces.put(ForgeDirection.SOUTH, new CubeFace[]{face});
            face = new CubeFace(ForgeDirection.WEST, Vec3.createVectorHelper(center.xCoord - mpRadius, center.yCoord + mpRadius, center.zCoord - mpRadius),
                                Vec3.createVectorHelper(center.xCoord + mpRadius, center.yCoord - mpRadius, center.zCoord - mpRadius)
            );
//            faces.put(ForgeDirection.WEST, new CubeFace[] {face});
            face = new CubeFace(ForgeDirection.UP, Vec3.createVectorHelper(center.xCoord - mpRadius, center.yCoord + mpRadius, center.zCoord + mpRadius),
                                Vec3.createVectorHelper(center.xCoord + mpRadius, center.yCoord + mpRadius, center.zCoord - mpRadius)
            );
//            faces.put(ForgeDirection.UP, new CubeFace[] {face});
            face = new CubeFace(ForgeDirection.DOWN, Vec3.createVectorHelper(center.xCoord - mpRadius, center.yCoord - mpRadius, center.zCoord + mpRadius),
                                Vec3.createVectorHelper(center.xCoord + mpRadius, center.yCoord - mpRadius, center.zCoord - mpRadius)
            );
//            faces.put(ForgeDirection.DOWN, new CubeFace[] {face});
        }

        public void draw(Tessellator tess) {
            for( CubeFace[] faceList : faces.values() ) {
                for( CubeFace facePart : faceList ) {
//                    if( facePart.facing == ForgeDirection.NORTH ) {
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.endPt.zCoord);
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
//                    } else if( facePart.facing == ForgeDirection.EAST ) {
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.endPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
//                    } else if( facePart.facing == ForgeDirection.SOUTH ) {
                    tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
                    tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                    tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
                    tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.endPt.zCoord);
//                    } else if( facePart.facing == ForgeDirection.WEST ) {
//                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.endPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
//                    } else if( facePart.facing == ForgeDirection.UP ) {
//                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
//                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
//                    } else if( facePart.facing == ForgeDirection.DOWN ) {
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
//                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
//                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
//                    }
                }
            }
        }

        public void interfere(Cube interfered) {
//            CubeFace newFace;
//
            List<CubeFace> newFaces = new ArrayList<>();
            List<CubeFace> newFaces2 = new ArrayList<>();
            Map<ForgeDirection, CubeFace[]> newFaceMap = new EnumMap<>(ForgeDirection.class);

            for( Map.Entry<ForgeDirection, CubeFace[]> intfFaceEntry : interfered.faces.entrySet() ) {
                for( Map.Entry<ForgeDirection, CubeFace[]> myFaceEntry : this.faces.entrySet() ) {
                    if( myFaceEntry.getKey() == intfFaceEntry.getKey() ) {
                        newFaces.clear();
                        for( CubeFace intfFace : intfFaceEntry.getValue() ) {
                            Collections.addAll(newFaces, intersectFacePart(myFaceEntry.getValue(), intfFace, interfered));
                        }

                        if( newFaces.size() > 0 ) {
                            newFaceMap.put(myFaceEntry.getKey(), newFaces.toArray(new CubeFace[newFaces.size()]));
                        } else {
                            newFaceMap.put(myFaceEntry.getKey(), myFaceEntry.getValue());
                        }
                    }
                }

                this.faces = newFaceMap;
            }
        }

        private CubeFace[] intersectFacePart(CubeFace[] faces, CubeFace intfFace, Cube interfered) {
            List<CubeFace> newFaces = new ArrayList<>();
            for( CubeFace myFace : faces ) {
                boolean intersectXPos = this.center.xCoord + radius > interfered.center.xCoord - radius && this.center.xCoord + radius < interfered.center.xCoord + radius;
                boolean intersectYPos = this.center.yCoord + radius > interfered.center.yCoord - radius && this.center.yCoord + radius < interfered.center.yCoord + radius;
                boolean intersectZPos = this.center.zCoord + radius > interfered.center.zCoord - radius && this.center.zCoord + radius < interfered.center.zCoord + radius;

                boolean intersectXNeg = this.center.xCoord - radius <= interfered.center.xCoord + radius && this.center.xCoord - radius >= interfered.center.xCoord - radius;
                boolean intersectYNeg = this.center.yCoord - radius < interfered.center.yCoord + radius && this.center.yCoord - radius > interfered.center.yCoord - radius;
                boolean intersectZNeg = this.center.zCoord - radius < interfered.center.zCoord + radius && this.center.zCoord - radius > interfered.center.zCoord - radius;

                if( myFace.facing == ForgeDirection.NORTH /**&& !intersectXPos**/ ) {
                    continue;
                } else if( myFace.facing == ForgeDirection.SOUTH && !intersectXNeg ) {
                    continue;
                } else if( myFace.facing == ForgeDirection.EAST/* && !intersectZPos*/ ) {
                    continue;
                } else if( myFace.facing == ForgeDirection.WEST/* && !intersectZNeg*/ ) {
                    continue;
                } else if( myFace.facing == ForgeDirection.UP/* && !intersectYPos*/ ) {
                    continue;
                } else if( myFace.facing == ForgeDirection.DOWN/* && !intersectYNeg*/ ) {
                    continue;
                }

                CubeFace[] newFacesArr = myFace.intersect(intfFace);

                if( newFacesArr != null ) {
                    Collections.addAll(newFaces, newFacesArr);
//                    Collections.addAll(newFaces, intersectFacePart(newFacesArr, intfFace, interfered));
                } else {
                    newFaces.add(myFace);
                }
            }

            return newFaces.toArray(new CubeFace[newFaces.size()]);
        }
    }
}
