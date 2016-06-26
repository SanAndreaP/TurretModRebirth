/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.tileentity;

import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RenderElectrolyteGenerator
        extends TileEntitySpecialRenderer<TileEntityElectrolyteGenerator>
{
    private static int wireCallList;

    @Override
    public void renderTileEntityAt(TileEntityElectrolyteGenerator tile, double x, double y, double z, float partTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

        for( int i = 0; i < TileEntityElectrolyteGenerator.SLOTS_PROCESSING.length; i++ ) {
            int slot = TileEntityElectrolyteGenerator.SLOTS_PROCESSING[i];
            ItemStack stack = tile.getStackInSlot(slot);

            if( ItemStackUtils.isValidStack(stack) ) {
                drawElectrolyteItem(i, stack);
            }
        }

        GlStateManager.popMatrix();
    }

    private static void drawElectrolyteItem(int index, ItemStack stack) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(40.0F * index, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.4F, 0.0F, 0.0F);
        for( int i = 0, max = 3; i < max; i++ ) {
            TmrClientUtils.renderStackInWorld(stack, 0.0D, 0.0001D * i, 0.0D, 0.0F, (180.0F / max) * i, 0.0F, 0.3D);
        }

        GlStateManager.disableCull();
        GlStateManager.disableLighting();

        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.TILE_ELECTROLYTE_GEN_WIRE.getResource());
        drawParaboleWire(10);

        GlStateManager.enableLighting();
        GlStateManager.enableCull();

        GlStateManager.popMatrix();
    }

    private static void drawParaboleWire(int steps) {
        if( wireCallList == 0 ) {
            double scale = 0.01D;
            final double perpendAngle = 90.0D * Math.PI / 180.0D;

            Vec3d[] builtVecMain = new Vec3d[steps+1];
            Vec3d[] builtVecA = new Vec3d[steps+1];
            Vec3d[] builtVecB = new Vec3d[steps+1];

            for( int i = 0; i <= steps; i++ ) {
                double x = i / (double) steps;
                double y = Math.pow(x, 4.0D);
                builtVecMain[i] = new Vec3d(x * -0.35D, y * -0.5D, 0.00D);
                builtVecA[i] = new Vec3d(x * -0.35D, y * -0.5D, 0.0D);
                builtVecB[i] = new Vec3d(x * -0.35D, y * -0.5D, 0.0D);
            }

            builtVecA[0] = builtVecMain[0].add(rotateVecXY(builtVecMain[1].subtract(builtVecMain[0]).normalize().scale(scale), perpendAngle));
            builtVecB[0] = builtVecMain[0].add(rotateVecXY(builtVecMain[1].subtract(builtVecMain[0]).normalize().scale(scale), -perpendAngle));

            for( int i = 1; i < builtVecA.length - 1; i++) {
                Vec3d vecBtwPre = builtVecMain[i].subtract(builtVecMain[i-1]);
                Vec3d vecBtwPost = builtVecMain[i+1].subtract(builtVecMain[i]);
                double btwAngle = Math.acos(vecBtwPre.dotProduct(vecBtwPost) / (vecBtwPre.lengthVector() * vecBtwPost.lengthVector()));
                builtVecA[i] = builtVecMain[i].add(rotateVecXY( vecBtwPre.normalize().scale(scale), perpendAngle + btwAngle / 2.0D));
                builtVecB[i] = builtVecMain[i].add(rotateVecXY( vecBtwPre.normalize().scale(scale), -perpendAngle - btwAngle / 2.0D));
            }

            Vec3d vecBtw = builtVecMain[builtVecMain.length-1].subtract(builtVecMain[builtVecMain.length-2]).normalize().scale(scale);
            builtVecA[builtVecA.length-1] = builtVecMain[builtVecMain.length-1].add(rotateVecXY(vecBtw, perpendAngle));
            builtVecB[builtVecA.length-1] = builtVecMain[builtVecMain.length-1].add(rotateVecXY(vecBtw, -perpendAngle));

            wireCallList = GlStateManager.glGenLists(1);
            if( wireCallList == 0 ) {
                return;
            }

            GlStateManager.glNewList(wireCallList, GL11.GL_COMPILE);

            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buf = tess.getBuffer();

            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            for( int i = 0; i < builtVecA.length - 1; i++ ) {
                final double u = Math.abs(builtVecA[i+1].subtract(builtVecA[i]).lengthVector()) * 4.5D;

                buf.pos(builtVecA[i].xCoord, builtVecA[i].yCoord, -scale).tex(0.0D, 1.0D).endVertex();
                buf.pos(builtVecA[i+1].xCoord, builtVecA[i+1].yCoord, -scale).tex(u, 1.0D).endVertex();
                buf.pos(builtVecB[i+1].xCoord, builtVecB[i+1].yCoord, scale).tex(u, 0.0D).endVertex();
                buf.pos(builtVecB[i].xCoord, builtVecB[i].yCoord, scale).tex(0.0D, 0.0D).endVertex();

                buf.pos(builtVecA[i].xCoord, builtVecA[i].yCoord, scale).tex(0.0D, 1.0D).endVertex();
                buf.pos(builtVecA[i+1].xCoord, builtVecA[i+1].yCoord, scale).tex(u, 1.0D).endVertex();
                buf.pos(builtVecB[i+1].xCoord, builtVecB[i+1].yCoord, -scale).tex(u, 0.0D).endVertex();
                buf.pos(builtVecB[i].xCoord, builtVecB[i].yCoord, -scale).tex(0.0D, 0.0D).endVertex();
            }

            tess.draw();

            GlStateManager.glEndList();
        }

        GlStateManager.callList(wireCallList);
    }

    private static void delWireCallList() {
        GlStateManager.glDeleteLists(wireCallList, 0);
        wireCallList = 0;
    }

    private static Vec3d rotateVecXY(Vec3d vec, double yaw) {
        double cos = Math.cos(yaw);
        double sin = Math.sin(yaw);
        double d0 = vec.xCoord * cos - vec.yCoord * sin;
        double d1 = vec.yCoord * cos + vec.xCoord * sin;
        double d2 = vec.zCoord;
        return new Vec3d(d0, d1, d2);
    }
}
