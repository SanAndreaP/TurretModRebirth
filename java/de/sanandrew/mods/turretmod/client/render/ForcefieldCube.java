/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render;

import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.ColorObject;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Level;

import java.util.*;
import java.util.Map.Entry;

public class ForcefieldCube
{
    public Map<EnumFacing, CubeFace[]> faces = new EnumMap<>(EnumFacing.class);
    private final Vec3d center;
    private final AxisAlignedBB boxAABB;
    public ColorObject boxColor;

    public ForcefieldCube(Vec3d mpCenter, AxisAlignedBB cubeBox, ColorObject color) {
        this.center = mpCenter;
        this.boxAABB = cubeBox;
        this.boxColor = color;

        for( EnumFacing direction : EnumFacing.VALUES ) {
            this.faces.put(direction, new CubeFace[]{new CubeFace(direction, mpCenter, cubeBox, color)});
        }
    }

    @Override
    public ForcefieldCube clone() {
        return new ForcefieldCube(this.center, this.boxAABB, this.boxColor);
    }

    public void draw(Tessellator tess) {
        VertexBuffer buffer = tess.getBuffer();
        for( CubeFace[] faceList : faces.values() ) {
            for( CubeFace facePart : faceList ) {
//                buffer.setColorRGBA_F(facePart.color.getRed(), facePart.color.getGreen(), facePart.color.getBlue(), facePart.color.getAlpha());
                if( facePart.facing == EnumFacing.NORTH ) {
                    double maxU = (facePart.endPt.zCoord - facePart.beginPt.zCoord) / 8.0D;
                    double maxV = (facePart.beginPt.yCoord - facePart.endPt.yCoord) / 8.0D;
                    buffer.pos(facePart.beginPt.xCoord - 0.0005D, facePart.beginPt.yCoord, facePart.beginPt.zCoord).tex(maxU, 0.0D).endVertex();
                    buffer.pos(facePart.beginPt.xCoord - 0.0005D, facePart.beginPt.yCoord, facePart.endPt.zCoord).tex(0.0D, 0.0D).endVertex();
                    buffer.pos(facePart.beginPt.xCoord - 0.0005D, facePart.endPt.yCoord, facePart.endPt.zCoord).tex(0.0D, maxV).endVertex();
                    buffer.pos(facePart.beginPt.xCoord - 0.0005D, facePart.endPt.yCoord, facePart.beginPt.zCoord).tex(maxU, maxV).endVertex();
                } else if( facePart.facing == EnumFacing.EAST ) {
                    double maxU = (facePart.endPt.xCoord - facePart.beginPt.xCoord) / 8.0D;
                    double maxV = (facePart.beginPt.yCoord - facePart.endPt.yCoord) / 8.0D;
                    buffer.pos(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord - 0.0005D).tex(maxU, 0.0D).endVertex();
                    buffer.pos(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord - 0.0005D).tex(0.0D, 0.0D).endVertex();
                    buffer.pos(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord - 0.0005D).tex(0.0D, maxV).endVertex();
                    buffer.pos(facePart.endPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord - 0.0005D).tex(maxU, maxV).endVertex();
                } else if( facePart.facing == EnumFacing.SOUTH ) {
                    double maxU = (facePart.endPt.zCoord - facePart.beginPt.zCoord) / 8.0D;
                    double maxV = (facePart.beginPt.yCoord - facePart.endPt.yCoord) / 8.0D;
                    buffer.pos(facePart.beginPt.xCoord + 0.0005D, facePart.beginPt.yCoord, facePart.endPt.zCoord).tex(maxU, 0.0D).endVertex();
                    buffer.pos(facePart.beginPt.xCoord + 0.0005D, facePart.beginPt.yCoord, facePart.beginPt.zCoord).tex(0.0D, 0.0D).endVertex();
                    buffer.pos(facePart.beginPt.xCoord + 0.0005D, facePart.endPt.yCoord, facePart.beginPt.zCoord).tex(0.0D, maxV).endVertex();
                    buffer.pos(facePart.beginPt.xCoord + 0.0005D, facePart.endPt.yCoord, facePart.endPt.zCoord).tex(maxU, maxV).endVertex();
                } else if( facePart.facing == EnumFacing.WEST ) {
                    double maxU = (facePart.endPt.xCoord - facePart.beginPt.xCoord) / 8.0D;
                    double maxV = (facePart.beginPt.yCoord - facePart.endPt.yCoord) / 8.0D;
                    buffer.pos(facePart.beginPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord + 0.0005D).tex(maxU, 0.0D).endVertex();
                    buffer.pos(facePart.endPt.xCoord, facePart.beginPt.yCoord, facePart.beginPt.zCoord + 0.0005D).tex(0.0D, 0.0D).endVertex();
                    buffer.pos(facePart.endPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord + 0.0005D).tex(0.0D, maxV).endVertex();
                    buffer.pos(facePart.beginPt.xCoord, facePart.endPt.yCoord, facePart.beginPt.zCoord + 0.0005D).tex(maxU, maxV).endVertex();
                } else if( facePart.facing == EnumFacing.UP ) {
                    double maxU = (facePart.beginPt.zCoord - facePart.endPt.zCoord) / 8.0D;
                    double maxV = (facePart.endPt.xCoord - facePart.beginPt.xCoord) / 8.0D;
                    buffer.pos(facePart.beginPt.xCoord, facePart.beginPt.yCoord - 0.0005D, facePart.beginPt.zCoord).tex(maxU, maxV).endVertex();
                    buffer.pos(facePart.endPt.xCoord, facePart.beginPt.yCoord - 0.0005D, facePart.beginPt.zCoord).tex(maxU, 0.0D).endVertex();
                    buffer.pos(facePart.endPt.xCoord, facePart.beginPt.yCoord - 0.0005D, facePart.endPt.zCoord).tex(0.0D, 0.0D).endVertex();
                    buffer.pos(facePart.beginPt.xCoord, facePart.beginPt.yCoord - 0.0005D, facePart.endPt.zCoord).tex(0.0D, maxV).endVertex();
                } else if( facePart.facing == EnumFacing.DOWN ) {
                    double maxU = (facePart.endPt.zCoord - facePart.beginPt.zCoord) / 8.0D;
                    double maxV = (facePart.endPt.xCoord - facePart.beginPt.xCoord) / 8.0D;
                    buffer.pos(facePart.endPt.xCoord, facePart.beginPt.yCoord + 0.0005D, facePart.beginPt.zCoord).tex(maxU, maxV).endVertex();
                    buffer.pos(facePart.beginPt.xCoord, facePart.beginPt.yCoord + 0.0005D, facePart.beginPt.zCoord).tex(maxU, 0.0D).endVertex();
                    buffer.pos(facePart.beginPt.xCoord, facePart.beginPt.yCoord + 0.0005D, facePart.endPt.zCoord).tex(0.0D, 0.0D).endVertex();
                    buffer.pos(facePart.endPt.xCoord, facePart.beginPt.yCoord + 0.0005D, facePart.endPt.zCoord).tex(0.0D, maxV).endVertex();
                }
            }
        }
    }

    public void interfere(ForcefieldCube interfered, boolean isRecessive) {
        Iterator<Entry<EnumFacing, CubeFace[]>> faceIterator = this.faces.entrySet().iterator();
        Map<EnumFacing, CubeFace[]> newFaceMap = new EnumMap<>(EnumFacing.class);

        while( faceIterator.hasNext() ) {
            Entry<EnumFacing, CubeFace[]> myFace = faceIterator.next();
            List<CubeFace> newFaces = new ArrayList<>();
            boolean intersects;

            switch( myFace.getKey() ) {
                case NORTH:
                    intersects = this.center.xCoord + this.boxAABB.maxX + (isRecessive ? 0.001D : 0) >= interfered.center.xCoord + interfered.boxAABB.minX
                            && this.center.xCoord + this.boxAABB.maxX + (isRecessive ? 0.001D : 0) <= interfered.center.xCoord + interfered.boxAABB.maxX;
                    break;
                case SOUTH:
                    intersects = this.center.xCoord + this.boxAABB.minX - (isRecessive ? 0.001D : 0) <= interfered.center.xCoord + interfered.boxAABB.maxX
                            && this.center.xCoord + this.boxAABB.minX - (isRecessive ? 0.001D : 0) >= interfered.center.xCoord + interfered.boxAABB.minX;
                    break;
                case EAST:
                    intersects = this.center.zCoord + this.boxAABB.maxZ + (isRecessive ? 0.001D : 0) >= interfered.center.zCoord + interfered.boxAABB.minZ
                            && this.center.zCoord + this.boxAABB.maxZ + (isRecessive ? 0.001D : 0) <= interfered.center.zCoord + interfered.boxAABB.maxZ;
                    break;
                case WEST:
                    intersects = this.center.zCoord + this.boxAABB.minZ - (isRecessive ? 0.001D : 0) <= interfered.center.zCoord + interfered.boxAABB.maxZ
                            && this.center.zCoord + this.boxAABB.minZ - (isRecessive ? 0.001D : 0) >= interfered.center.zCoord + interfered.boxAABB.minZ;
                    break;
                case UP:
                    intersects = this.center.yCoord + this.boxAABB.maxY + (isRecessive ? 0.001D : 0) >= interfered.center.yCoord + interfered.boxAABB.minY
                            && this.center.yCoord + this.boxAABB.maxY + (isRecessive ? 0.001D : 0) <= interfered.center.yCoord + interfered.boxAABB.maxY;
                    break;
                case DOWN:
                    intersects = this.center.yCoord + this.boxAABB.minY - (isRecessive ? 0.001D : 0) <= interfered.center.yCoord + interfered.boxAABB.maxY
                            && this.center.yCoord + this.boxAABB.minY - (isRecessive ? 0.001D : 0) >= interfered.center.yCoord + interfered.boxAABB.minY;
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
                        TurretModRebirth.LOG.log(Level.ERROR, "Too many face parts for shield! Max. of 10000 is exceeded! Removing Face!");
                        newFaces.clear();
                        break;
                    }
                }
            }

            newFaceMap.put(myFace.getKey(), newFaces.toArray(new CubeFace[newFaces.size()]));
        }

        this.faces = newFaceMap;
    }

    private static class CubeFace
    {
        public final EnumFacing facing;
        public final Vec3d beginPt;
        public final Vec3d endPt;
        public final ColorObject color;

        public CubeFace(EnumFacing direction, Vec3d begin, Vec3d end, ColorObject faceColor) {
            this.facing = direction;
            this.beginPt = begin;
            this.endPt = end;
            this.color = faceColor;
        }

        public CubeFace(EnumFacing direction, Vec3d center, AxisAlignedBB boxBB, ColorObject faceColor) {
            this.facing = direction;
            this.color = faceColor;
            switch( direction ) {
                case NORTH:
                    this.beginPt = new Vec3d(center.xCoord + boxBB.maxX, center.yCoord + boxBB.maxY, center.zCoord + boxBB.minZ);
                    this.endPt = new Vec3d(center.xCoord + boxBB.maxX, center.yCoord + boxBB.minY, center.zCoord + boxBB.maxZ);
                    break;
                case SOUTH:
                    this.beginPt = new Vec3d(center.xCoord + boxBB.minX, center.yCoord + boxBB.maxY, center.zCoord + boxBB.minZ);
                    this.endPt = new Vec3d(center.xCoord + boxBB.minX, center.yCoord + boxBB.minY, center.zCoord + boxBB.maxZ);
                    break;
                case EAST:
                    this.beginPt = new Vec3d(center.xCoord + boxBB.minX, center.yCoord + boxBB.maxY, center.zCoord + boxBB.maxZ);
                    this.endPt = new Vec3d(center.xCoord + boxBB.maxX, center.yCoord + boxBB.minY, center.zCoord + boxBB.maxZ);
                    break;
                case WEST:
                    this.beginPt = new Vec3d(center.xCoord + boxBB.minX, center.yCoord + boxBB.maxY, center.zCoord + boxBB.minZ);
                    this.endPt = new Vec3d(center.xCoord + boxBB.maxX, center.yCoord + boxBB.minY, center.zCoord + boxBB.minZ);
                    break;
                case UP:
                    this.beginPt = new Vec3d(center.xCoord + boxBB.minX, center.yCoord + boxBB.maxY, center.zCoord + boxBB.maxZ);
                    this.endPt = new Vec3d(center.xCoord + boxBB.maxX, center.yCoord + boxBB.maxY, center.zCoord + boxBB.minZ);
                    break;
                case DOWN:
                    this.beginPt = new Vec3d(center.xCoord + boxBB.minX, center.yCoord + boxBB.minY, center.zCoord + boxBB.maxZ);
                    this.endPt = new Vec3d(center.xCoord + boxBB.maxX, center.yCoord + boxBB.minY, center.zCoord + boxBB.minZ);
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
                    return new CubeFace(this.facing, new Vec3d(this.beginPt.xCoord, newRect.beginY, newRect.beginX),
                                        new Vec3d(this.endPt.xCoord, newRect.endY, newRect.endX), this.color);
                case EAST:
                case WEST:
                    return new CubeFace(this.facing, new Vec3d(newRect.beginX, newRect.beginY, this.beginPt.zCoord),
                                        new Vec3d(newRect.endX, newRect.endY, this.endPt.zCoord), this.color);
                case UP:
                case DOWN:
                    return new CubeFace(this.facing, new Vec3d(newRect.beginX, this.beginPt.yCoord, newRect.beginY),
                                        new Vec3d(newRect.endX, this.endPt.yCoord, newRect.endY), this.color);
                default:
                    return null;
            }
        }
    }

    private static class RectCoords
    {
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
}
