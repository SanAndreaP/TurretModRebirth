/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.tileentity;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.client.model.ModelItemTransmitter;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter.RequestType;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderItemTransmitter
        extends TileEntitySpecialRenderer
{
    ModelItemTransmitter modelBlock = new ModelItemTransmitter();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partTicks) {
        TileEntityItemTransmitter te = ((TileEntityItemTransmitter) tile);
        Minecraft mc = Minecraft.getMinecraft();

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

        GL11.glPushMatrix();
        this.bindTexture(EnumTextures.TILE_ITEM_TRANSMITTER.getResource());
        this.modelBlock.render(0.0625F);
        this.bindTexture(EnumTextures.TILE_ITEM_TRANSMITTER_GLOW.getResource());

        float prevBrightX = OpenGlHelper.lastBrightnessX;
        float prevBrightY = OpenGlHelper.lastBrightnessY;
        int bright = 0xF0;
        int brightX = bright % 65536;
        int brightY = bright / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);
        this.modelBlock.render(0.0625F);
        GL11.glPopMatrix();

        MovingObjectPosition objPos = mc.objectMouseOver;
        if( objPos.typeOfHit == MovingObjectType.BLOCK && mc.theWorld.getTileEntity(objPos.blockX, objPos.blockY, objPos.blockZ) == te ) {
            GL11.glPushMatrix();
            setupTooltipRenderer(mc, te, partTicks);

            if( te.scaleTooltip < 1.0F ) {
                te.scaleTooltip += 0.1F;
            } else {
                te.scaleTooltip = 1.01F;
            }
            renderTooltipBg(Tessellator.instance, te.scaleTooltip);

            if( te.scaleTooltip >= 1.0F ) {
                if( te.lengthTooltipRod < 1.0F ) {
                    te.lengthTooltipRod += 0.05F;
                } else {
                    te.lengthTooltipRod = 1.01F;
                }
                renderTooltipRod(Tessellator.instance, te.lengthTooltipRod);
            }
            finishTooltipRenderer();

            if( te.scaleTooltip >= 1.0F ) {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
                GL11.glScalef(0.01F, 0.01F, 0.01F);
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
                int max = 0xA0;
                int alpha = Math.min(Math.max(0x01, MathHelper.ceiling_float_int(max * te.lengthTooltipRod)), max);

                mc.fontRenderer.drawString(te.getRequestType().name(), 0, 0, 0xFFFFFF | (alpha << 24));
                if( te.getRequestType() != RequestType.NONE ) {
                    mc.fontRenderer.drawString(te.getRequestItem().getDisplayName(), 0, 9, 0xFFFFFF | (alpha << 24));
                }
            }

            GL11.glPopMatrix();
        } else {
            if( te.timestampLastRendered < mc.theWorld.getTotalWorldTime() - 2 ) {
                te.scaleTooltip = 0.0F;
                te.lengthTooltipRod = 0.0F;
            } else if( te.scaleTooltip > 0.0F ) {
                GL11.glPushMatrix();
                setupTooltipRenderer(mc, te, partTicks);
                if( te.lengthTooltipRod > 0.0F ) {
                    te.lengthTooltipRod -= 0.05F;
                    renderTooltipRod(Tessellator.instance, te.lengthTooltipRod);
                } else {
                    te.lengthTooltipRod = -0.01F;
                }

                if( te.lengthTooltipRod <= 0.0F ) {
                    if( te.scaleTooltip > 0.0F ) {
                        te.scaleTooltip -= 0.1F;
                    } else {
                        te.scaleTooltip = -0.01F;
                    }
                }
                renderTooltipBg(Tessellator.instance, te.scaleTooltip);

                finishTooltipRenderer();

                if( te.lengthTooltipRod > 0.0F ) {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
                    GL11.glScalef(0.01F, 0.01F, 0.01F);
                    GL11.glTranslatef(0.0F, 0.0F, -1.0F);
                    int max = 0xA0;
                    int alpha = Math.min(Math.max(0x04, MathHelper.ceiling_float_int(max * te.lengthTooltipRod)), max);

                    mc.fontRenderer.drawString(te.getRequestType().name(), 0, 0, 0xFFFFFF | (alpha << 24));
                    if( te.getRequestType() != RequestType.NONE ) {
                        mc.fontRenderer.drawString(te.getRequestItem().getDisplayName(), 0, 9, 0xFFFFFF | (alpha << 24));
                    }
                }
                GL11.glPopMatrix();
            }
        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);

        GL11.glPopMatrix();

        te.timestampLastRendered = mc.theWorld.getTotalWorldTime();
    }

    private static void setupTooltipRenderer(Minecraft mc, TileEntityItemTransmitter te, float partTicks) {
        double playerPTX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partTicks;
        double playerPTZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partTicks;
        Vec3 vecPos = Vec3.createVectorHelper(playerPTX - (te.xCoord + 0.5D), 0.0, playerPTZ - (te.zCoord + 0.5D));
        vecPos = vecPos.normalize();
        double angle = Math.toDegrees(Math.atan2(vecPos.zCoord, vecPos.xCoord));

        GL11.glDepthMask(false);
        GL11.glRotated(90, 0.0F, 1.0F, 0.0F);
        GL11.glRotated(angle, 0.0F, 1.0F, 0.0F);
        GL11.glTranslated(-1.0F, 0.0F, 0.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL11.glColor4d(0.0D, 1.0D, 0.25D, 0.25D + (float) SAPUtils.RNG.nextGaussian() * 0.02F);
    }

    private static void finishTooltipRenderer() {
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private static void renderTooltipBg(Tessellator tessellator, float scale) {
        tessellator.startDrawingQuads();
        tessellator.addVertex(-0.5D * scale, -0.25D * scale + 0.25D, 0.0D);
        tessellator.addVertex(0.5D * scale, -0.25D * scale + 0.25D, 0.0D);
        tessellator.addVertex(0.5D * scale, 0.25D * scale + 0.25D, 0.0D);
        tessellator.addVertex(-0.5D * scale, 0.25D * scale + 0.25D, 0.0D);
        tessellator.draw();
    }

    private static void renderTooltipRod(Tessellator tessellator, float size) {
        GL11.glLineWidth(10.0F);
        tessellator.startDrawing(GL11.GL_LINES);
        tessellator.addVertex(0.52D * size + 0.48D, 0.52D * size + 0.48D, 0.0D);
        tessellator.addVertex(0.48D, 0.48D, -0.01D);
        tessellator.draw();
    }
}
