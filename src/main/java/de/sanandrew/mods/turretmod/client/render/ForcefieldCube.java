/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ForcefieldCube
        implements Cloneable
{
    private Map<EnumFacing, CubeFace[]> faces = new EnumMap<>(EnumFacing.class);
    private final Vec3d center;
    private final AxisAlignedBB boxAABB;
    public final ColorObj boxColor;
    public final boolean cullFaces;
    public boolean fullRendered;

    public ForcefieldCube(Vec3d mpCenter, AxisAlignedBB cubeBox, ColorObj color, boolean cullFaces) {
        this.center = mpCenter;
        this.boxAABB = cubeBox;
        this.boxColor = color;
        this.cullFaces = cullFaces;

        for( EnumFacing direction : EnumFacing.VALUES ) {
            this.faces.put(direction, new CubeFace[]{new CubeFace(direction, mpCenter, cubeBox, color)});
        }
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public ForcefieldCube clone() {
        return new ForcefieldCube(this.center, this.boxAABB, this.boxColor, this.cullFaces);
    }

    public void draw(Tessellator tess) {
        BufferBuilder buffer = tess.getBuffer();
        for( CubeFace[] faceList : faces.values() ) {
            for( CubeFace facePart : faceList ) {
                float red = facePart.color.fRed();
                float green = facePart.color.fGreen();
                float blue = facePart.color.fBlue();
                float alpha = facePart.color.fAlpha();
                if( facePart.facing == EnumFacing.NORTH ) {
                    double maxU = (facePart.endPt.z - facePart.beginPt.z) / 8.0D;
                    double maxV = (facePart.beginPt.y - facePart.endPt.y) / 8.0D;
                    buffer.pos(facePart.beginPt.x - 0.0005D, facePart.beginPt.y, facePart.beginPt.z).tex(maxU, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x - 0.0005D, facePart.beginPt.y, facePart.endPt.z).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x - 0.0005D, facePart.endPt.y, facePart.endPt.z).tex(0.0D, maxV).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x - 0.0005D, facePart.endPt.y, facePart.beginPt.z).tex(maxU, maxV).color(red, green, blue, alpha).endVertex();
                } else if( facePart.facing == EnumFacing.EAST ) {
                    double maxU = (facePart.endPt.x - facePart.beginPt.x) / 8.0D;
                    double maxV = (facePart.beginPt.y - facePart.endPt.y) / 8.0D;
                    buffer.pos(facePart.endPt.x, facePart.beginPt.y, facePart.beginPt.z - 0.0005D).tex(maxU, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x, facePart.beginPt.y, facePart.beginPt.z - 0.0005D).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x, facePart.endPt.y, facePart.beginPt.z - 0.0005D).tex(0.0D, maxV).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.endPt.x, facePart.endPt.y, facePart.beginPt.z - 0.0005D).tex(maxU, maxV).color(red, green, blue, alpha).endVertex();
                } else if( facePart.facing == EnumFacing.SOUTH ) {
                    double maxU = (facePart.endPt.z - facePart.beginPt.z) / 8.0D;
                    double maxV = (facePart.beginPt.y - facePart.endPt.y) / 8.0D;
                    buffer.pos(facePart.beginPt.x + 0.0005D, facePart.beginPt.y, facePart.endPt.z).tex(maxU, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x + 0.0005D, facePart.beginPt.y, facePart.beginPt.z).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x + 0.0005D, facePart.endPt.y, facePart.beginPt.z).tex(0.0D, maxV).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x + 0.0005D, facePart.endPt.y, facePart.endPt.z).tex(maxU, maxV).color(red, green, blue, alpha).endVertex();
                } else if( facePart.facing == EnumFacing.WEST ) {
                    double maxU = (facePart.endPt.x - facePart.beginPt.x) / 8.0D;
                    double maxV = (facePart.beginPt.y - facePart.endPt.y) / 8.0D;
                    buffer.pos(facePart.beginPt.x, facePart.beginPt.y, facePart.beginPt.z + 0.0005D).tex(maxU, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.endPt.x, facePart.beginPt.y, facePart.beginPt.z + 0.0005D).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.endPt.x, facePart.endPt.y, facePart.beginPt.z + 0.0005D).tex(0.0D, maxV).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x, facePart.endPt.y, facePart.beginPt.z + 0.0005D).tex(maxU, maxV).color(red, green, blue, alpha).endVertex();
                } else if( facePart.facing == EnumFacing.UP ) {
                    double maxU = (facePart.beginPt.z - facePart.endPt.z) / 8.0D;
                    double maxV = (facePart.endPt.x - facePart.beginPt.x) / 8.0D;
                    buffer.pos(facePart.beginPt.x, facePart.beginPt.y - 0.0005D, facePart.beginPt.z).tex(maxU, maxV).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.endPt.x, facePart.beginPt.y - 0.0005D, facePart.beginPt.z).tex(maxU, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.endPt.x, facePart.beginPt.y - 0.0005D, facePart.endPt.z).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x, facePart.beginPt.y - 0.0005D, facePart.endPt.z).tex(0.0D, maxV).color(red, green, blue, alpha).endVertex();
                } else if( facePart.facing == EnumFacing.DOWN ) {
                    double maxU = (facePart.endPt.z - facePart.beginPt.z) / 8.0D;
                    double maxV = (facePart.endPt.x - facePart.beginPt.x) / 8.0D;
                    buffer.pos(facePart.endPt.x, facePart.beginPt.y + 0.0005D, facePart.beginPt.z).tex(maxU, maxV).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x, facePart.beginPt.y + 0.0005D, facePart.beginPt.z).tex(maxU, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.beginPt.x, facePart.beginPt.y + 0.0005D, facePart.endPt.z).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
                    buffer.pos(facePart.endPt.x, facePart.beginPt.y + 0.0005D, facePart.endPt.z).tex(0.0D, maxV).color(red, green, blue, alpha).endVertex();
                }
            }
        }
    }

    public void interfere(ForcefieldCube interfered, boolean isRecessive) {
        if( this.fullRendered || interfered.fullRendered ) {
            return;
        }

        Iterator<Entry<EnumFacing, CubeFace[]>> faceIterator = this.faces.entrySet().iterator();
        Map<EnumFacing, CubeFace[]> newFaceMap = new EnumMap<>(EnumFacing.class);

        while( faceIterator.hasNext() ) {
            Entry<EnumFacing, CubeFace[]> myFace = faceIterator.next();
            List<CubeFace> newFaces = new ArrayList<>();
            boolean intersects;

            switch( myFace.getKey() ) {
                case NORTH:
                    intersects = this.center.x + this.boxAABB.maxX + (isRecessive ? 0.001D : 0.0D) >= interfered.center.x + interfered.boxAABB.minX
                            && this.center.x + this.boxAABB.maxX + (isRecessive ? 0.001D : 0.0D) <= interfered.center.x + interfered.boxAABB.maxX;
                    break;
                case SOUTH:
                    intersects = this.center.x + this.boxAABB.minX - (isRecessive ? 0.001D : 0.0D) <= interfered.center.x + interfered.boxAABB.maxX
                            && this.center.x + this.boxAABB.minX - (isRecessive ? 0.001D : 0.0D) >= interfered.center.x + interfered.boxAABB.minX;
                    break;
                case EAST:
                    intersects = this.center.z + this.boxAABB.maxZ + (isRecessive ? 0.001D : 0.0D) >= interfered.center.z + interfered.boxAABB.minZ
                            && this.center.z + this.boxAABB.maxZ + (isRecessive ? 0.001D : 0.0D) <= interfered.center.z + interfered.boxAABB.maxZ;
                    break;
                case WEST:
                    intersects = this.center.z + this.boxAABB.minZ - (isRecessive ? 0.001D : 0.0D) <= interfered.center.z + interfered.boxAABB.maxZ
                            && this.center.z + this.boxAABB.minZ - (isRecessive ? 0.001D : 0.0D) >= interfered.center.z + interfered.boxAABB.minZ;
                    break;
                case UP:
                    intersects = this.center.y + this.boxAABB.maxY + (isRecessive ? 0.001D : 0.0D) >= interfered.center.y + interfered.boxAABB.minY
                            && this.center.y + this.boxAABB.maxY + (isRecessive ? 0.001D : 0.0D) <= interfered.center.y + interfered.boxAABB.maxY;
                    break;
                case DOWN:
                    intersects = this.center.y + this.boxAABB.minY - (isRecessive ? 0.001D : 0.0D) <= interfered.center.y + interfered.boxAABB.maxY
                            && this.center.y + this.boxAABB.minY - (isRecessive ? 0.001D : 0.0D) >= interfered.center.y + interfered.boxAABB.minY;
                    break;
                default:
                    continue;
            }

            Collections.addAll(newFaces, myFace.getValue());

            if( newFaces.size() > 0 && intersects ) {
                CubeFace myFacePart;
                CubeFace[] newFaceParts;
                CubeFace intfFace = new CubeFace(myFace.getKey().getOpposite(), interfered.center, interfered.boxAABB, interfered.boxColor);
                for( int i = 0, j = 0; i < newFaces.size(); i++, j++ ) {
                    myFacePart = newFaces.get(i);
                    newFaceParts = myFacePart.intersect(intfFace);

                    if( newFaceParts != null ) {
                        newFaces.remove(i);
                        Collections.addAll(newFaces, newFaceParts);
                        i--;
                    }

                    if( j >= 10000 ) {
                        TmrConstants.LOG.log(Level.ERROR, "Too many face parts for shield! Max. of 10000 is exceeded! Removing Face!");
                        newFaces.clear();
                        break;
                    }
                }
            }

            newFaceMap.put(myFace.getKey(), newFaces.toArray(new CubeFace[0]));
        }

        this.faces = newFaceMap;
    }

    private static class CubeFace
    {
        private final EnumFacing facing;
        private final Vec3d      beginPt;
        private final Vec3d      endPt;
        private final ColorObj   color;

        private CubeFace(EnumFacing direction, Vec3d begin, Vec3d end, ColorObj faceColor) {
            this.facing = direction;
            this.beginPt = begin;
            this.endPt = end;
            this.color = faceColor;
        }

        private CubeFace(EnumFacing direction, Vec3d center, AxisAlignedBB boxBB, ColorObj faceColor) {
            this.facing = direction;
            this.color = faceColor;
            switch( direction ) {
                case NORTH:
                    this.beginPt = new Vec3d(center.x + boxBB.maxX, center.y + boxBB.maxY, center.z + boxBB.minZ);
                    this.endPt = new Vec3d(center.x + boxBB.maxX, center.y + boxBB.minY, center.z + boxBB.maxZ);
                    break;
                case SOUTH:
                    this.beginPt = new Vec3d(center.x + boxBB.minX, center.y + boxBB.maxY, center.z + boxBB.minZ);
                    this.endPt = new Vec3d(center.x + boxBB.minX, center.y + boxBB.minY, center.z + boxBB.maxZ);
                    break;
                case EAST:
                    this.beginPt = new Vec3d(center.x + boxBB.minX, center.y + boxBB.maxY, center.z + boxBB.maxZ);
                    this.endPt = new Vec3d(center.x + boxBB.maxX, center.y + boxBB.minY, center.z + boxBB.maxZ);
                    break;
                case WEST:
                    this.beginPt = new Vec3d(center.x + boxBB.minX, center.y + boxBB.maxY, center.z + boxBB.minZ);
                    this.endPt = new Vec3d(center.x + boxBB.maxX, center.y + boxBB.minY, center.z + boxBB.minZ);
                    break;
                case UP:
                    this.beginPt = new Vec3d(center.x + boxBB.minX, center.y + boxBB.maxY, center.z + boxBB.maxZ);
                    this.endPt = new Vec3d(center.x + boxBB.maxX, center.y + boxBB.maxY, center.z + boxBB.minZ);
                    break;
                case DOWN:
                    this.beginPt = new Vec3d(center.x + boxBB.minX, center.y + boxBB.minY, center.z + boxBB.maxZ);
                    this.endPt = new Vec3d(center.x + boxBB.maxX, center.y + boxBB.minY, center.z + boxBB.minZ);
                    break;
                default:
                    throw new RuntimeException(String.format("Invalid direction for Forcefield Face: %s", direction.toString()));
            }
        }

        private CubeFace[] intersect(CubeFace intersector) {
            RectCoords my = this.get2DCoords();
            RectCoords intsCoord = intersector.get2DCoords();

            if( my.endX <= intsCoord.beginX || my.beginX >= intsCoord.endX || my.beginY <= intsCoord.endY || my.endY >= intsCoord.beginY ) {
                return null;
            }

            List<CubeFace> newCubes = new ArrayList<>(4);

            RectCoords topRect = new RectCoords(my.beginX, my.beginY, my.endX, Math.max(intsCoord.beginY, my.endY));
            RectCoords btmRect = new RectCoords(my.beginX, Math.max(intsCoord.endY, my.endY), my.endX, my.endY);
            RectCoords lftRect = new RectCoords(my.beginX, Math.min(intsCoord.beginY, my.beginY), Math.max(intsCoord.beginX, my.beginX), Math.max(intsCoord.endY, my.endY));
            RectCoords rgtRect = new RectCoords(Math.min(intsCoord.endX, my.endX), Math.min(intsCoord.beginY, my.beginY), my.endX, Math.max(intsCoord.endY, my.endY));

            if( topRect.isSizePositive() ) {
                newCubes.add(this.get3DCoords(topRect));
            }
            if( btmRect.isSizePositive() ) {
                newCubes.add(this.get3DCoords(btmRect));
            }
            if( lftRect.isSizePositive() ) {
                newCubes.add(this.get3DCoords(lftRect));
            }
            if( rgtRect.isSizePositive() ) {
                newCubes.add(this.get3DCoords(rgtRect));
            }

            if( newCubes.size() == 0 ) {
                return new CubeFace[0];
            }

            return newCubes.toArray(new CubeFace[0]);
        }

        private RectCoords get2DCoords() {
            switch( this.facing ) {
                case NORTH:
                case SOUTH:
                    return new RectCoords(this.beginPt.z, this.beginPt.y, this.endPt.z, this.endPt.y);
                case EAST:
                case WEST:
                    return new RectCoords(this.beginPt.x, this.beginPt.y, this.endPt.x, this.endPt.y);
                case UP:
                case DOWN:
                    return new RectCoords(this.beginPt.x, this.beginPt.z, this.endPt.x, this.endPt.z);
                default:
                    return new RectCoords(0.0D, 0.0D, 0.0D, 0.0D);
            }
        }

        private CubeFace get3DCoords(RectCoords newRect) {
            switch( this.facing ) {
                case NORTH:
                case SOUTH:
                    return new CubeFace(this.facing, new Vec3d(this.beginPt.x, newRect.beginY, newRect.beginX),
                                        new Vec3d(this.endPt.x, newRect.endY, newRect.endX), this.color);
                case EAST:
                case WEST:
                    return new CubeFace(this.facing, new Vec3d(newRect.beginX, newRect.beginY, this.beginPt.z),
                                        new Vec3d(newRect.endX, newRect.endY, this.endPt.z), this.color);
                case UP:
                case DOWN:
                    return new CubeFace(this.facing, new Vec3d(newRect.beginX, this.beginPt.y, newRect.beginY),
                                        new Vec3d(newRect.endX, this.endPt.y, newRect.endY), this.color);
                default:
                    return null;
            }
        }
    }

    private static class RectCoords
    {
        final double beginX;
        final double beginY;
        final double endX;
        final double endY;

        RectCoords(double xBegin, double yBegin, double xEnd, double yEnd) {
            this.beginX = xBegin;
            this.beginY = yBegin;
            this.endX = xEnd;
            this.endY = yEnd;
        }

        boolean isSizePositive() {
            return beginX + 0.0001D < endX && beginY > endY + 0.0001D;
        }
    }
}
