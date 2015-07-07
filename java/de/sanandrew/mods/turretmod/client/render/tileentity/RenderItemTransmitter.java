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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.lwjgl.opengl.GL11;

public class RenderItemTransmitter
        extends TileEntitySpecialRenderer
{
    ModelItemTransmitter modelBlock = new ModelItemTransmitter();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partTicks) {
        TileEntityItemTransmitter te = ((TileEntityItemTransmitter) tile);
        Minecraft mc = Minecraft.getMinecraft();

        this.bindTexture(EnumTextures.TILE_ITEM_TRANSMITTER.getResource());

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

        MovingObjectPosition objPos = mc.objectMouseOver;
        if( objPos.typeOfHit == MovingObjectType.BLOCK && mc.theWorld.getTileEntity(objPos.blockX, objPos.blockY, objPos.blockZ) == te ) {
            Tessellator tessellator = Tessellator.instance;

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.5F, -0.5F);
            GL11.glRotatef(RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);

            float healthPerc = 0.0F;

            float prevBrightX = OpenGlHelper.lastBrightnessX;
            float prevBrightY = OpenGlHelper.lastBrightnessY;
            int brightness = 0xF0;
            int brightX = brightness % 65536;
            int brightY = brightness / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
            tessellator.addVertex(-0.5D, -0.05D, 0.0D);
            tessellator.addVertex(-0.5D + healthPerc, -0.05D, 0.0D);
            tessellator.addVertex(-0.5D + healthPerc, 0.05D, 0.0D);
            tessellator.addVertex(-0.5D, 0.05D, 0.0D);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_F(1.0F, 0.0F, 0.0F, 1.0F);
            tessellator.addVertex(-0.5D + healthPerc, -0.05D, 0.0D);
            tessellator.addVertex(0.5D, -0.05D, 0.0D);
            tessellator.addVertex(0.5D, 0.05D, 0.0D);
            tessellator.addVertex(-0.5D + healthPerc, 0.05D, 0.0D);
            tessellator.draw();

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        }


        this.modelBlock.render(0.0625F);

        this.bindTexture(EnumTextures.TILE_ITEM_TRANSMITTER_GLOW.getResource());

        int bright = 0xF0;
        int brightX = bright % 65536;
        int brightY = bright / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);
        this.modelBlock.render(0.0625F);

        GL11.glPopMatrix();
    }
}
