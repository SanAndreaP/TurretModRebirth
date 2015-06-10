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
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.Map.Entry;

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
        }

        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, 0.5F);

        for( Cube cube : cubes ) {
            cube.draw(tess);
        }

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

//        GL11.glDepthFunc(GL11.GL_NOTEQUAL);

        tess.draw();

//        GL11.glDepthFunc(GL11.GL_LEQUAL);
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

        public CubeFace(ForgeDirection direction, Vec3 center, double radius) {
            this.facing = direction;
            switch( direction ) {
                case NORTH:
                    this.beginPt = Vec3.createVectorHelper(center.xCoord + radius, center.yCoord + radius, center.zCoord - radius);
                    this.endPt = Vec3.createVectorHelper(center.xCoord + radius, center.yCoord - radius, center.zCoord + radius);
                    break;
                case SOUTH:
                    this.beginPt = Vec3.createVectorHelper(center.xCoord - radius, center.yCoord + radius, center.zCoord - radius);
                    this.endPt = Vec3.createVectorHelper(center.xCoord - radius, center.yCoord - radius, center.zCoord + radius);
                    break;
                case EAST:
                    this.beginPt = Vec3.createVectorHelper(center.xCoord - radius, center.yCoord + radius, center.zCoord + radius);
                    this.endPt = Vec3.createVectorHelper(center.xCoord + radius, center.yCoord - radius, center.zCoord + radius);
                    break;
                case WEST:
                    this.beginPt = Vec3.createVectorHelper(center.xCoord - radius, center.yCoord + radius, center.zCoord - radius);
                    this.endPt = Vec3.createVectorHelper(center.xCoord + radius, center.yCoord - radius, center.zCoord - radius);
                    break;
                case UP:
                    this.beginPt = Vec3.createVectorHelper(center.xCoord - radius, center.yCoord + radius, center.zCoord + radius);
                    this.endPt = Vec3.createVectorHelper(center.xCoord + radius, center.yCoord + radius, center.zCoord - radius);
                    break;
                case DOWN:
                    this.beginPt = Vec3.createVectorHelper(center.xCoord - radius, center.yCoord - radius, center.zCoord + radius);
                    this.endPt = Vec3.createVectorHelper(center.xCoord + radius, center.yCoord - radius, center.zCoord - radius);
                    break;
                default:
                    throw new RuntimeException(String.format("Invalid direction for Forcefield Face: %s", direction.toString()));
            }
        }

        public CubeFace[] intersect(CubeFace intersector) {
            RectCoords myCoord = this.get2DCoords();
            RectCoords intsCoord = intersector.get2DCoords();

            if( myCoord.endX <= intsCoord.beginX || myCoord.beginX >= intsCoord.endX || myCoord.beginY <= intsCoord.endY || myCoord.endY >= intsCoord.beginY ) {
                return null;
            }

            List<CubeFace> newCubes = new ArrayList<>(4);

            RectCoords topRect = new RectCoords(myCoord.beginX, myCoord.beginY, myCoord.endX, Math.max(intsCoord.beginY, myCoord.endY));
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
                    return new CubeFace(this.facing, Vec3.createVectorHelper(this.beginPt.xCoord, newRect.beginY, newRect.beginX),
                                        Vec3.createVectorHelper(this.endPt.xCoord, newRect.endY, newRect.endX));
                case EAST:
                case WEST:
                    return new CubeFace(this.facing, Vec3.createVectorHelper(newRect.beginX, newRect.beginY, this.beginPt.zCoord),
                                        Vec3.createVectorHelper(newRect.endX, newRect.endY, this.endPt.zCoord));
                case UP:
                case DOWN:
                    return new CubeFace(this.facing, Vec3.createVectorHelper(newRect.beginX, this.beginPt.yCoord, newRect.beginY),
                                        Vec3.createVectorHelper(newRect.endX, this.endPt.yCoord, newRect.endY));
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
            return beginX + 0.0001D >= endX || beginY <= endY + 0.0001D;
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

            for( ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS ) {
                faces.put(direction, new CubeFace[]{new CubeFace(direction, mpCenter, mpRadius)});
            }
        }

        public void draw(Tessellator tess) {
            for( CubeFace[] faceList : faces.values() ) {
                for( CubeFace facePart : faceList ) {
                    if( facePart.facing == ForgeDirection.NORTH ) {
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.endPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
                    } else if( facePart.facing == ForgeDirection.EAST ) {
                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.endPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
                    } else if( facePart.facing == ForgeDirection.SOUTH ) {
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.endPt.zCoord);
                    } else if( facePart.facing == ForgeDirection.WEST ) {
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.endPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord);
                    } else if( facePart.facing == ForgeDirection.UP ) {
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
                    } else if( facePart.facing == ForgeDirection.DOWN ) {
                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord);
                        tess.addVertex(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
                        tess.addVertex(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.endPt.zCoord);
                    }
                }
            }
        }

        public void interfere(Cube interfered) {
            Iterator<Entry<ForgeDirection, CubeFace[]>> faceIterator = this.faces.entrySet().iterator();
            Map<ForgeDirection, CubeFace[]> newFaceMap = new EnumMap<>(ForgeDirection.class);

            while( faceIterator.hasNext() ) {
                Entry<ForgeDirection, CubeFace[]> myFace = faceIterator.next();
                List<CubeFace> newFaces = new ArrayList<>();
                boolean intersects;

                switch( myFace.getKey() ) {
                    case NORTH:
                        intersects = this.center.xCoord + radius - 0.001D >= interfered.center.xCoord - radius && this.center.xCoord + radius - 0.001D <= interfered.center.xCoord + radius;
                        break;
                    case SOUTH:
                        intersects = this.center.xCoord - radius + 0.001D <= interfered.center.xCoord + radius && this.center.xCoord - radius + 0.001D >= interfered.center.xCoord - radius;
                        break;
                    case EAST:
                        intersects = this.center.zCoord + radius - 0.001D >= interfered.center.zCoord - radius && this.center.zCoord + radius - 0.001D <= interfered.center.zCoord + radius;
                        break;
                    case WEST:
                        intersects = this.center.zCoord - radius + 0.001D <= interfered.center.zCoord + radius && this.center.zCoord - radius + 0.001D >= interfered.center.zCoord - radius;
                        break;
                    case UP:
                        intersects = this.center.yCoord + radius - 0.001D >= interfered.center.yCoord - radius && this.center.yCoord + radius - 0.001D <= interfered.center.yCoord + radius;
                        break;
                    case DOWN:
                        intersects = this.center.yCoord - radius + 0.001D <= interfered.center.yCoord + radius && this.center.yCoord - radius + 0.001D >= interfered.center.yCoord - radius;
                        break;
                    default:
                        continue;
                }

                Collections.addAll(newFaces, myFace.getValue());

                if( newFaces.size() > 0 && intersects ) {
                    List<CubeFace> intfFaces = new ArrayList<>();
                    Collections.addAll(intfFaces, interfered.faces.get(myFace.getKey()));
                    Collections.addAll(intfFaces, interfered.faces.get(myFace.getKey().getOpposite()));

                    if( intfFaces.size() > 0 ) {
                        CubeFace myFacePart;
                        CubeFace[] newFaceParts;
                        for( CubeFace intfFace : intfFaces ) {
                            for( int i = 0, j = 0; i < newFaces.size(); i++, j++ ) {
                                myFacePart = newFaces.get(i);
                                newFaceParts = myFacePart.intersect(intfFace);

                                if( newFaceParts != null ) {
                                    newFaces.remove(i);
                                    Collections.addAll(newFaces, newFaceParts);
                                    i--;
                                }

                                if( j >= 10000 ) {
                                    TurretMod.MOD_LOG.log(Level.ERROR, "Too many face parts for shield! Max. of 10000 is exceeded! Removing Face!");
                                    newFaces.clear();
                                    break;
                                }
                            }
                        }
                    }
                }

                newFaceMap.put(myFace.getKey(), newFaces.toArray(new CubeFace[newFaces.size()]));
            }

            this.faces = newFaceMap;
        }
    }
}
