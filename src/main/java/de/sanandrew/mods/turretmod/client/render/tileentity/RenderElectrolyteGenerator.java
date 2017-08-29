/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.tileentity;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.turretmod.registry.electrolytegen.ElectrolyteProcess;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderElectrolyteGenerator
        extends TileEntitySpecialRenderer<TileEntityElectrolyteGenerator>
{
    private static int wireCallList;

    @Override
    public void render(TileEntityElectrolyteGenerator tile, double x, double y, double z, float partTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

        for( int i = 0, max = tile.processes.length; i < max; i++ ) {
            ElectrolyteProcess proc = tile.processes[i];
            if( proc != null ) {
                drawElectrolyteItem(i, proc.processStack);
            }
        }

        GlStateManager.popMatrix();
    }

    private static void drawElectrolyteItem(int index, @Nonnull ItemStack stack) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(40.0F * index, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.4F, 0.0F, 0.0F);
        for( int i = 0, max = 3; i < max; i++ ) {
            RenderUtils.renderStackInWorld(stack, 0.0D, 0.0001D * i, 0.0D, 0.0F, (180.0F / max) * i, 0.0F, 0.3D);
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
            BufferBuilder buf = tess.getBuffer();

            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            for( int i = 0; i < builtVecA.length - 1; i++ ) {
                final double u = Math.abs(builtVecA[i+1].subtract(builtVecA[i]).lengthVector()) * 4.5D;

                buf.pos(builtVecA[i].x, builtVecA[i].y, -scale).tex(0.0D, 1.0D).endVertex();
                buf.pos(builtVecA[i+1].x, builtVecA[i+1].y, -scale).tex(u, 1.0D).endVertex();
                buf.pos(builtVecB[i+1].x, builtVecB[i+1].y, scale).tex(u, 0.0D).endVertex();
                buf.pos(builtVecB[i].x, builtVecB[i].y, scale).tex(0.0D, 0.0D).endVertex();

                buf.pos(builtVecA[i].x, builtVecA[i].y, scale).tex(0.0D, 1.0D).endVertex();
                buf.pos(builtVecA[i+1].x, builtVecA[i+1].y, scale).tex(u, 1.0D).endVertex();
                buf.pos(builtVecB[i+1].x, builtVecB[i+1].y, -scale).tex(u, 0.0D).endVertex();
                buf.pos(builtVecB[i].x, builtVecB[i].y, -scale).tex(0.0D, 0.0D).endVertex();
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
        double d0 = vec.x * cos - vec.y * sin;
        double d1 = vec.y * cos + vec.x * sin;
        double d2 = vec.z;
        return new Vec3d(d0, d1, d2);
    }
}
