/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.tileentity;

import de.sanandrew.mods.turretmod.client.model.ModelItemTransmitter;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class RenderItemTransmitter
        extends TileEntitySpecialRenderer
{
    ModelItemTransmitter modelBlock = new ModelItemTransmitter();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partTicks) {
        TileEntityItemTransmitter te = ((TileEntityItemTransmitter) tile);

//        final double displayX = -2.5D;
//        final double displayY = -0.775D;
//        final double displayHeight = 0.0775D;
//
//        if( te.ticksGenRemain > 0 ) {
//            if( te.displayAmplitude < 1.0D ) {
//                te.displayAmplitude += 0.05D;
//            }
//        } else {
//            if( te.displayAmplitude > 0.0D ) {
//                te.displayAmplitude -= 0.05D;
//            } else if( te.displayAmplitude < 0.0D ) {
//                te.displayAmplitude = 0.0D;
//            }
//        }
//
//        if( !Minecraft.getMinecraft().isGamePaused() ) {
//            te.displayDrawCycles++;
//            if( te.displayDrawCycles >= 3200 ) {
//                te.displayDrawCycles = 0;
//            }
//            this.modelBlock.addGrinderRotation((float) (te.displayDrawCycles / 31.0D * Math.PI * 2.0D * te.displayAmplitude));
//        }

        this.bindTexture(EnumTextures.TILE_ITEM_TRANSMITTER.getResource());

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
//        GL11.glRotatef(180.0F + te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord) * 90.0F, 0.0F, 1.0F, 0.0F);

        this.modelBlock.render(0.0625F);

        this.bindTexture(EnumTextures.TILE_ITEM_TRANSMITTER_GLOW.getResource());

        int bright = 0xF0;
        int brightX = bright % 65536;
        int brightY = bright / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);
        this.modelBlock.render(0.0625F);

//        GL11.glRotatef(-180.0F, 1.0F, 0.0F, 0.0F);
//        GL11.glScalef(0.25F, 0.25F, 0.25F);
//        GL11.glTranslatef(0.0F, -2.5F, 0.0F);
//        GL11.glTranslatef(1.25F, 0.0F, 0.0F);
//
//        int bright = 0xF0;
//        int brightX = bright % 65536;
//        int brightY = bright / 65536;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);
//        for( int i = 0; i < 40; i++ ) {
//            double yShift = Math.sin((te.displayDrawCycles * (i / 80.0D + 0.625D)) / 20.0D * Math.PI) * 6.0D * displayHeight * te.displayAmplitude;
//            GL11.glColor4f(0.2F, 0.2F, 1.0F, 1.0F);
//            SAPClientUtils.drawSquareZPos(displayX + i*0.025D, displayY + yShift, displayX + 0.025D + i * 0.025D, displayY + 0.05 + yShift, 2.02);
//        }


        GL11.glPopMatrix();
    }
}
