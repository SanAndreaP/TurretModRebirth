/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.sanandrew.core.manpack.util.client.helpers.AverageColorHelper;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.helpers.SAPUtils.RGBAValues;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter.RequestType;
import de.sanandrew.mods.turretmod.util.Textures;
import de.sanandrew.mods.turretmod.util.TmrBlocks;
import de.sanandrew.mods.turretmod.util.TmrItems;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiIngameTcuInfos
        extends Gui
{
    private Minecraft mc;
    private FontRenderer tooltipFR;

    @SubscribeEvent
    public void onRenderIngame(RenderGameOverlayEvent.Pre event) {
        if( this.mc == null ) {
            this.mc = Minecraft.getMinecraft();
        }

        if( this.mc.thePlayer == null || this.mc.thePlayer.getHeldItem() == null || this.mc.thePlayer.getHeldItem().getItem() != TmrItems.turretCtrlUnit ) {
            return;
        }

        if( this.tooltipFR == null ) {
            this.tooltipFR = new FontRenderer(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.renderEngine, true);
            if( this.mc.gameSettings.language != null ) {
                this.tooltipFR.setBidiFlag(this.mc.getLanguageManager().isCurrentLanguageBidirectional());
            }
        }

        if( event.type == ElementType.CROSSHAIRS ) {
            GL11.glPushMatrix();
            //noinspection IntegerDivisionInFloatingPointContext
            GL11.glTranslatef(event.resolution.getScaledWidth() / 2 + 6, event.resolution.getScaledHeight() / 2 + 6, 0.0F);
            MovingObjectPosition objPos = this.mc.objectMouseOver;

            if( objPos.typeOfHit == MovingObjectType.BLOCK ) {
                Block block = this.mc.theWorld.getBlock(objPos.blockX, objPos.blockY, objPos.blockZ);
                if( block == TmrBlocks.itemTransmitter ) {
                    TileEntityItemTransmitter transmitter = (TileEntityItemTransmitter) this.mc.theWorld.getTileEntity(objPos.blockX, objPos.blockY, objPos.blockZ);
                    ItemStack stack = transmitter.getRequestItem();
                    Turret turret = transmitter.getRequestingTurret();
                    List<String> lines = new ArrayList<>();

                    lines.add(SAPUtils.translatePostFormat("Requesting: %s", transmitter.getRequestType().name()));
                    if( transmitter.getRequestType() != RequestType.NONE && turret != null && stack != null ) {
                        lines.add(SAPUtils.translatePostFormat("Item: %s x%d", stack.getDisplayName(), stack.stackSize));
                        lines.add(SAPUtils.translatePostFormat("Turret: %s", turret.getTurretName()));
                        lines.add(SAPUtils.translatePostFormat("  @ x:%.0f y:%.0f z:%.0f", turret.getEntity().posX, turret.getEntity().posY, turret.getEntity().posZ));
                        lines.add(SAPUtils.translatePostFormat("Expiration time: %d", transmitter.requestTimeout));
                    }

                    this.drawTooltip(lines, -3, -3, 0xA085C96F);
                }
            } else if( objPos.typeOfHit == MovingObjectType.ENTITY ) {
                Entity e = objPos.entityHit;
                if( e instanceof EntityTurretBase ) {
                    EntityTurretBase turret = (EntityTurretBase) e;
                    RGBAValues clr;
                    try {
                        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(turret.getGlowTexture());
                        clr = AverageColorHelper.getAverageColor(res.getInputStream(), new RGBAValues(0xFF000000));
                    } catch( IOException ex ) {
                        clr = new RGBAValues(0, 0, 0, 255);
                    }

                    List<String> lines = new ArrayList<>();

                    lines.add(SAPUtils.translatePostFormat("Turret: %s", turret.getTurretName()));
                    lines.add(SAPUtils.translatePostFormat("Frequency: %s", turret.getFrequency()));
                    lines.add(SAPUtils.translatePostFormat("Health: %s", turret.getHealth()));

                    this.drawTooltip(lines, -3, -3, (0xA0 << 24) | (clr.getRed() << 16) | (clr.getGreen() << 8) | clr.getBlue());
                }
            }

            GL11.glPopMatrix();
        }
    }

    private void drawTooltip(List<String> lines, int x, int y, int frameClr) {
        int maxLength = this.tooltipFR.getStringWidth(new OrderingStringLength().max(lines));
        int maxHeight = lines.size() * 9;

        int darkFrameClr = subtractClrRel(frameClr, 128);

        Gui.drawRect(x, y, x + maxLength + 5, y + 1, frameClr);
        Gui.drawRect(x, y + maxHeight + 3, x + maxLength + 5, y + maxHeight + 4, darkFrameClr);
        this.drawGradientRect(x, y + 1, x + 1, y + maxHeight + 3, frameClr, darkFrameClr);
        this.drawGradientRect(x + maxLength + 4, y + 1, x + maxLength + 5, y + maxHeight + 3, frameClr, darkFrameClr);
        Gui.drawRect(x + 1, y + 1, x + maxLength + 4, y + maxHeight + 3, 0xA0000000);

        for( int i = 0, max = lines.size(); i < max; i++ ) {
            this.tooltipFR.drawString(lines.get(i), x + 3, y + 2 + 9 * i, 0xFFFFFFFF);
        }
    }

    private int subtractClrRel(int color, int subt) {
        RGBAValues clr = new RGBAValues(color);
        int red = clr.getRed() - (int) ((clr.getRed() / (float) 0xFF) * subt);
        int green = clr.getGreen() - (int) ((clr.getGreen() / (float) 0xFF) * subt);
        int blue = clr.getBlue() - (int) ((clr.getBlue() / (float) 0xFF) * subt);
        return new RGBAValues(red, green, blue, clr.getAlpha()).getColorInt();
    }

    private static class OrderingStringLength
            extends Ordering<String>
    {
        @Override
        public int compare(String left, String right) {
            return Ints.compare(left.length(), right.length());
        }
    }
}
