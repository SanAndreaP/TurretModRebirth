/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.renderer.turret;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
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
    private       Map<Direction, CubeFace[]> faces = new EnumMap<>(Direction.class);
    private final Vector3d                   center;
    private final AxisAlignedBB                  boxAABB;
    public final ColorObj boxColor;
    public final boolean cullFaces;
    private final              boolean      fullRendered;


    public ForcefieldCube(Vector3d mpCenter, AxisAlignedBB cubeBox, ColorObj color, boolean cullFaces, boolean fullRendered) {
        this.center = mpCenter;
        this.boxAABB = cubeBox;
        this.boxColor = color;
        this.cullFaces = cullFaces;
        this.fullRendered = fullRendered;

        for( Direction direction : Direction.values() ) {
            this.faces.put(direction, new CubeFace[]{new CubeFace(direction, mpCenter, cubeBox, color)});
        }
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public ForcefieldCube clone() {
        return new ForcefieldCube(this.center, this.boxAABB, this.boxColor, this.cullFaces, this.fullRendered);
    }

    public void translate(MatrixStack mat) {
        mat.translate(this.center.x, this.center.y, this.center.z);
    }

    public void draw(IVertexBuilder buf, Matrix4f pose) {
        for( CubeFace[] faceList : faces.values() ) {
            for( CubeFace facePart : faceList ) {
                if( facePart.facing == Direction.NORTH ) {
                    float maxU = (float) (facePart.endPt.z - facePart.beginPt.z) / 8.0F;
                    float maxV = (float) (facePart.beginPt.y - facePart.endPt.y) / 8.0F;
                    vertex(buf, pose, facePart.beginPt.x - 0.0005D, facePart.beginPt.y, facePart.beginPt.z, maxU, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x - 0.0005D, facePart.beginPt.y, facePart.endPt.z, 0.0F, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x - 0.0005D, facePart.endPt.y, facePart.endPt.z, 0.0F, maxV, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x - 0.0005D, facePart.endPt.y, facePart.beginPt.z, maxU, maxV, facePart.color);
                } else if( facePart.facing == Direction.EAST ) {
                    float maxU = (float) (facePart.endPt.x - facePart.beginPt.x) / 8.0F;
                    float maxV = (float) (facePart.beginPt.y - facePart.endPt.y) / 8.0F;
                    vertex(buf, pose, facePart.endPt.x, facePart.beginPt.y, facePart.beginPt.z - 0.0005D, maxU, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x, facePart.beginPt.y, facePart.beginPt.z - 0.0005D, 0.0F, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x, facePart.endPt.y, facePart.beginPt.z - 0.0005D, 0.0F, maxV, facePart.color);
                    vertex(buf, pose, facePart.endPt.x, facePart.endPt.y, facePart.beginPt.z - 0.0005D, maxU, maxV, facePart.color);
                } else if( facePart.facing == Direction.SOUTH ) {
                    float maxU = (float) (facePart.endPt.z - facePart.beginPt.z) / 8.0F;
                    float maxV = (float) (facePart.beginPt.y - facePart.endPt.y) / 8.0F;
                    vertex(buf, pose, facePart.beginPt.x + 0.0005D, facePart.beginPt.y, facePart.endPt.z, maxU, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x + 0.0005D, facePart.beginPt.y, facePart.beginPt.z, 0.0F, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x + 0.0005D, facePart.endPt.y, facePart.beginPt.z, 0.0F, maxV, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x + 0.0005D, facePart.endPt.y, facePart.endPt.z, maxU, maxV, facePart.color);
                } else if( facePart.facing == Direction.WEST ) {
                    float maxU = (float) (facePart.endPt.x - facePart.beginPt.x) / 8.0F;
                    float maxV = (float) (facePart.beginPt.y - facePart.endPt.y) / 8.0F;
                    vertex(buf, pose, facePart.beginPt.x, facePart.beginPt.y, facePart.beginPt.z + 0.0005D, maxU, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.endPt.x, facePart.beginPt.y, facePart.beginPt.z + 0.0005D, 0.0F, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.endPt.x, facePart.endPt.y, facePart.beginPt.z + 0.0005D, 0.0F, maxV, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x, facePart.endPt.y, facePart.beginPt.z + 0.0005D, maxU, maxV, facePart.color);
                } else if( facePart.facing == Direction.UP ) {
                    float maxU = (float) (facePart.beginPt.z - facePart.endPt.z) / 8.0F;
                    float maxV = (float) (facePart.endPt.x - facePart.beginPt.x) / 8.0F;
                    vertex(buf, pose, facePart.beginPt.x, facePart.beginPt.y - 0.0005D, facePart.beginPt.z, maxU, maxV, facePart.color);
                    vertex(buf, pose, facePart.endPt.x, facePart.beginPt.y - 0.0005D, facePart.beginPt.z, maxU, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.endPt.x, facePart.beginPt.y - 0.0005D, facePart.endPt.z, 0.0F, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x, facePart.beginPt.y - 0.0005D, facePart.endPt.z, 0.0F, maxV, facePart.color);
                } else if( facePart.facing == Direction.DOWN ) {
                    float maxU = (float) (facePart.endPt.z - facePart.beginPt.z) / 8.0F;
                    float maxV = (float) (facePart.endPt.x - facePart.beginPt.x) / 8.0F;
                    vertex(buf, pose, facePart.endPt.x, facePart.beginPt.y + 0.0005D, facePart.beginPt.z, maxU, maxV, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x, facePart.beginPt.y + 0.0005D, facePart.beginPt.z, maxU, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.beginPt.x, facePart.beginPt.y + 0.0005D, facePart.endPt.z, 0.0F, 0.0F, facePart.color);
                    vertex(buf, pose, facePart.endPt.x, facePart.beginPt.y + 0.0005D, facePart.endPt.z, 0.0F, maxV, facePart.color);
                }
            }
        }
    }

    private static void vertex(IVertexBuilder buf, Matrix4f m4f, double x, double y, double z, float u, float v, ColorObj rgba) {
        buf.vertex(m4f, (float) x, (float) y, (float) z).color(rgba.fRed(), rgba.fGreen(), rgba.fBlue(), rgba.fAlpha()).uv(u, v).endVertex();
    }

    public void interfere(ForcefieldCube interfered, boolean isRecessive) {
        if( this.fullRendered || interfered.fullRendered ) {
            return;
        }

        Iterator<Entry<Direction, CubeFace[]>> faceIterator = this.faces.entrySet().iterator();
        Map<Direction, CubeFace[]> newFaceMap = new EnumMap<>(Direction.class);

        while( faceIterator.hasNext() ) {
            Entry<Direction, CubeFace[]> myFace = faceIterator.next();
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

            if( !newFaces.isEmpty() && intersects ) {
                CubeFace myFacePart;
                CubeFace[] newFaceParts;
                CubeFace intfFace = new CubeFace(myFace.getKey().getOpposite(), interfered.center, interfered.boxAABB, interfered.boxColor);
                for( int i = 0, j = 0; i < newFaces.size(); i++, j++ ) {
                    myFacePart = newFaces.get(i);
                    newFaceParts = myFacePart.intersect(intfFace);

                    if( newFaceParts != null ) {
                        newFaces.remove(i--);
                        Collections.addAll(newFaces, newFaceParts);
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
        private final Direction facing;
        private final Vector3d center;
        private final Vector3d      beginPt;
        private final Vector3d      endPt;
        private final ColorObj   color;

        private CubeFace(Direction direction, Vector3d center, Vector3d begin, Vector3d end, ColorObj faceColor) {
            this.facing = direction;
            this.center = center;
            this.beginPt = begin;
            this.endPt = end;
            this.color = faceColor;
        }

        private CubeFace(Direction direction, Vector3d center, AxisAlignedBB boxBB, ColorObj faceColor) {
            this.facing = direction;
            this.color = faceColor;
            this.center = center;
            switch( direction ) {
                case NORTH:
                    this.beginPt = new Vector3d(boxBB.maxX, boxBB.maxY, boxBB.minZ);
                    this.endPt = new Vector3d(boxBB.maxX, boxBB.minY, boxBB.maxZ);
                    break;
                case SOUTH:
                    this.beginPt = new Vector3d(boxBB.minX, boxBB.maxY, boxBB.minZ);
                    this.endPt = new Vector3d(boxBB.minX, boxBB.minY, boxBB.maxZ);
                    break;
                case EAST:
                    this.beginPt = new Vector3d(boxBB.minX, boxBB.maxY, boxBB.maxZ);
                    this.endPt = new Vector3d(boxBB.maxX, boxBB.minY, boxBB.maxZ);
                    break;
                case WEST:
                    this.beginPt = new Vector3d(boxBB.minX, boxBB.maxY, boxBB.minZ);
                    this.endPt = new Vector3d(boxBB.maxX, boxBB.minY, boxBB.minZ);
                    break;
                case UP:
                    this.beginPt = new Vector3d(boxBB.minX, boxBB.maxY, boxBB.maxZ);
                    this.endPt = new Vector3d(boxBB.maxX, boxBB.maxY, boxBB.minZ);
                    break;
                case DOWN:
                    this.beginPt = new Vector3d(boxBB.minX, boxBB.minY, boxBB.maxZ);
                    this.endPt = new Vector3d(boxBB.maxX, boxBB.minY, boxBB.minZ);
                    break;
                default:
                    throw new RuntimeException(String.format("Invalid direction for Forcefield Face: %s", direction));
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
                newCubes.add(this.get3DCoords(topRect.toLocal(this.center, this.facing)));
            }
            if( btmRect.isSizePositive() ) {
                newCubes.add(this.get3DCoords(btmRect.toLocal(this.center, this.facing)));
            }
            if( lftRect.isSizePositive() ) {
                newCubes.add(this.get3DCoords(lftRect.toLocal(this.center, this.facing)));
            }
            if( rgtRect.isSizePositive() ) {
                newCubes.add(this.get3DCoords(rgtRect.toLocal(this.center, this.facing)));
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
                    return new RectCoords(this.center.z + this.beginPt.z, this.center.y + this.beginPt.y, this.center.z + this.endPt.z, this.center.y + this.endPt.y);
                case EAST:
                case WEST:
                    return new RectCoords(this.center.x + this.beginPt.x, this.center.y + this.beginPt.y, this.center.x + this.endPt.x, this.center.y + this.endPt.y);
                case UP:
                case DOWN:
                    return new RectCoords(this.center.x + this.beginPt.x, this.center.z + this.beginPt.z, this.center.x + this.endPt.x, this.center.z + this.endPt.z);
                default:
                    return new RectCoords(0.0D, 0.0D, 0.0D, 0.0D);
            }
        }

        private CubeFace get3DCoords(RectCoords newRect) {
            switch( this.facing ) {
                case NORTH:
                case SOUTH:
                    return new CubeFace(this.facing, this.center, new Vector3d(this.beginPt.x, newRect.beginY, newRect.beginX),
                                        new Vector3d(this.endPt.x, newRect.endY, newRect.endX), this.color);
                case EAST:
                case WEST:
                    return new CubeFace(this.facing, this.center, new Vector3d(newRect.beginX, newRect.beginY, this.beginPt.z),
                                        new Vector3d(newRect.endX, newRect.endY, this.endPt.z), this.color);
                case UP:
                case DOWN:
                    return new CubeFace(this.facing, this.center, new Vector3d(newRect.beginX, this.beginPt.y, newRect.beginY),
                                        new Vector3d(newRect.endX, this.endPt.y, newRect.endY), this.color);
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

        RectCoords toLocal(Vector3d center, Direction facing) {
            switch( facing ) {
                case NORTH:
                case SOUTH:
                    return new RectCoords(this.beginX - center.z, this.beginY - center.y, this.endX - center.z, this.endY - center.y);
                case EAST:
                case WEST:
                    return new RectCoords(this.beginX - center.x, this.beginY - center.y, this.endX - center.x, this.endY - center.y);
                case UP:
                case DOWN:
                    return new RectCoords(this.beginX - center.x, this.beginY - center.z, this.endX - center.x, this.endY - center.z);
                default:
                    return new RectCoords(0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
