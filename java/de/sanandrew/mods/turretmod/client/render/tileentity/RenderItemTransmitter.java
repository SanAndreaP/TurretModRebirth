/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.tileentity;

import de.sanandrew.core.manpack.util.client.IconParticle;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.client.model.ModelItemTransmitter;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter.RequestType;
import de.sanandrew.mods.turretmod.util.Textures;
import de.sanandrew.mods.turretmod.util.TmrItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.lwjgl.opengl.GL11;

public class RenderItemTransmitter
        extends TileEntitySpecialRenderer
{
    private ModelItemTransmitter modelBlock = new ModelItemTransmitter();
    private FontRenderer tooltipFR;
    private static final IIcon TEXTURE_ICON = new IconParticle("hTooltipTransmitter", 32, 20, 0, 0, 32, 20);

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        if( this.tooltipFR == null ) {
            this.tooltipFR = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, true);
            if( mc.gameSettings.language != null ) {
                this.tooltipFR.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
            }
        }

        TileEntityItemTransmitter te = ((TileEntityItemTransmitter) tile);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

        if( te.renderPass == 0 ) {
            GL11.glPushMatrix();
            this.bindTexture(Textures.TILE_ITEM_TRANSMITTER.getResource());
            this.modelBlock.render(0.0625F);
            this.bindTexture(Textures.TILE_ITEM_TRANSMITTER_GLOW.getResource());

            float prevBrightX = OpenGlHelper.lastBrightnessX;
            float prevBrightY = OpenGlHelper.lastBrightnessY;
            int bright = 0xF0;
            int brightX = bright % 65536;
            int brightY = bright / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);
            this.modelBlock.render(0.0625F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);
            GL11.glPopMatrix();
        } else {
            MovingObjectPosition objPos = mc.objectMouseOver;

            float prevBrightX = OpenGlHelper.lastBrightnessX;
            float prevBrightY = OpenGlHelper.lastBrightnessY;
            int bright = 0xF0;
            int brightX = bright % 65536;
            int brightY = bright / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);

            if( objPos.typeOfHit == MovingObjectType.BLOCK && mc.theWorld.getTileEntity(objPos.blockX, objPos.blockY, objPos.blockZ) == te
                && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == TmrItems.turretCtrlUnit )
            {
                GL11.glPushMatrix();
                setupTooltipRenderer(mc, te, partTicks);

                if( te.scaleTooltip < 1.0F ) {
                    te.scaleTooltip += 0.2F;
                } else {
                    te.scaleTooltip = 1.01F;
                }
                renderTooltipBg(Tessellator.instance, te.scaleTooltip);

                if( te.scaleTooltip >= 1.0F ) {
                    if( te.lengthTooltipRod < 1.0F ) {
                        te.lengthTooltipRod += 0.1F;
                    } else {
                        te.lengthTooltipRod = 1.01F;
                    }
                    renderTooltipRod(Tessellator.instance, te.lengthTooltipRod);
                }
                finishTooltipRenderer();

                if( te.scaleTooltip >= 1.0F ) {
                    renderTooltipText(this.tooltipFR, te, 0xFFFFFF, te.lengthTooltipRod);
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
                        te.lengthTooltipRod -= 0.1F;
                        renderTooltipRod(Tessellator.instance, te.lengthTooltipRod);
                    } else {
                        te.lengthTooltipRod = -0.01F;
                    }

                    if( te.lengthTooltipRod <= 0.0F ) {
                        if( te.scaleTooltip > 0.0F ) {
                            te.scaleTooltip -= 0.2F;
                        } else {
                            te.scaleTooltip = -0.01F;
                        }
                    }
                    renderTooltipBg(Tessellator.instance, te.scaleTooltip);

                    finishTooltipRenderer();

                    if( te.lengthTooltipRod > 0.0F ) {
                        renderTooltipText(this.tooltipFR, te, 0xFFFFFF, te.lengthTooltipRod);
                    }
                    GL11.glPopMatrix();
                }
            }

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);

            te.timestampLastRendered = mc.theWorld.getTotalWorldTime();
        }

        GL11.glPopMatrix();
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
        GL11.glTranslated(0.0F, -0.25F, 0.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL11.glColor4d(1.0D, 1.0D, 1.0D, 0.75D + (float) SAPUtils.RNG.nextGaussian() * 0.02F);
    }

    private static void finishTooltipRenderer() {
        GL11.glDepthMask(true);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private static void renderTooltipBg(Tessellator tessellator, float scale) {
        Minecraft.getMinecraft().renderEngine.bindTexture(Textures.GUI_TOOLTIP_HOLOGRAPH.getResource());
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, scale);
        scale = 1.0F;
        tessellator.addVertexWithUV(-0.5D * scale, -0.3125D * scale + 0.3125D, 0.0D, TEXTURE_ICON.getMinU(), TEXTURE_ICON.getMinV());
        tessellator.addVertexWithUV(0.5D * scale, -0.3125D * scale + 0.3125D, 0.0D, TEXTURE_ICON.getMaxU(), TEXTURE_ICON.getMinV());
        tessellator.addVertexWithUV(0.5D * scale, 0.3125D * scale + 0.3125D, 0.0D, TEXTURE_ICON.getMaxU(), TEXTURE_ICON.getMaxV());
        tessellator.addVertexWithUV(-0.5D * scale, 0.3125D * scale + 0.3125D, 0.0D, TEXTURE_ICON.getMinU(), TEXTURE_ICON.getMaxV());
        tessellator.draw();
    }

    private static void renderTooltipRod(Tessellator tessellator, float size) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(10.0F);
        tessellator.startDrawing(GL11.GL_LINES);
        tessellator.setColorOpaque_I(0x001E10);
        tessellator.addVertex(0.0D, 0.5D * size + 0.635D, -0.01D);
        tessellator.addVertex(0.0D, 0.635D, -0.01D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    //TODO: add translations!
    private static void renderTooltipText(FontRenderer fontRenderer, TileEntityItemTransmitter te, int color, float alpha) {
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
        GL11.glScalef(0.01F, 0.01F, 0.01F);
        GL11.glTranslatef(8.0F, 8.0F, -1.0F);
        int max = 0xE0;
        int alphaInt = Math.min(Math.max(0x04, MathHelper.ceiling_float_int(max * alpha)), max);
        ItemStack stack = te.getRequestItem();
        Turret turret = te.getRequestingTurret();
        String s;

        s = SAPUtils.translatePostFormat("Requesting: %s", te.getRequestType().name());
        fontRenderer.drawString(s, 0, te.getRequestType() == RequestType.NONE ? 18 : 0, color | (alphaInt << 24));
        if( te.getRequestType() != RequestType.NONE && turret != null && stack != null ) {
            s = SAPUtils.translatePostFormat("Item: %s x%d", stack.getDisplayName(), stack.stackSize);
            fontRenderer.drawString(s, 0, 9, color | (alphaInt << 24));
            s = SAPUtils.translatePostFormat("Turret: %s", turret.getTurretName());
            fontRenderer.drawString(s, 0, 18, color | (alphaInt << 24));
            s = SAPUtils.translatePostFormat("    @ x:%.0f y:%.0f z:%.0f", turret.getEntity().posX, turret.getEntity().posY, turret.getEntity().posZ);
            fontRenderer.drawString(s, 0, 27, color | (alphaInt << 24));
            s = SAPUtils.translatePostFormat("Expiration time: %d", te.requestTimeout);
            fontRenderer.drawString(s, 0, 36, color | (alphaInt << 24));
        }
    }
}
