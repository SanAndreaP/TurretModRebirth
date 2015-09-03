/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.tooltip;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.sanandrew.core.manpack.util.client.helpers.AverageColorHelper;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.helpers.SAPUtils.RGBAValues;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.client.gui.tcu.tooltip.LineInfoBar.LineAmmoBar;
import de.sanandrew.mods.turretmod.client.gui.tcu.tooltip.LineInfoBar.LineHealthBar;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter.RequestType;
import de.sanandrew.mods.turretmod.util.TmrBlocks;
import de.sanandrew.mods.turretmod.util.TmrItems;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
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

    @SubscribeEvent
    public void onRenderIngame(RenderGameOverlayEvent.Pre event) {
        if( this.mc == null ) {
            this.mc = Minecraft.getMinecraft();
        }

        if( this.mc.thePlayer == null || this.mc.thePlayer.getHeldItem() == null || this.mc.thePlayer.getHeldItem().getItem() != TmrItems.turretCtrlUnit ) {
            return;
        }

        if( event.type == ElementType.CROSSHAIRS ) {
            GL11.glPushMatrix();
            GL11.glTranslatef(event.resolution.getScaledWidth() / 2.0F + 6.0F, event.resolution.getScaledHeight() / 2.0F + 6.0F, 0.0F);
            MovingObjectPosition objPos = this.mc.objectMouseOver;

            if( objPos.typeOfHit == MovingObjectType.BLOCK ) {
                Block block = this.mc.theWorld.getBlock(objPos.blockX, objPos.blockY, objPos.blockZ);
                if( block == TmrBlocks.itemTransmitter ) {
                    TileEntityItemTransmitter transmitter = (TileEntityItemTransmitter) this.mc.theWorld.getTileEntity(objPos.blockX, objPos.blockY, objPos.blockZ);
                    ItemStack stack = transmitter.getRequestItem();
                    Turret turret = transmitter.getRequestingTurret();
                    List<TooltipLine<TileEntityItemTransmitter>> lines = new ArrayList<>();

                    lines.add(new LineString<TileEntityItemTransmitter>(SAPUtils.translatePostFormat("Requesting: %s", transmitter.getRequestType().name())));
                    if( transmitter.getRequestType() != RequestType.NONE && turret != null && stack != null ) {
                        lines.add(new LineString<TileEntityItemTransmitter>(SAPUtils.translatePostFormat("Item: %s x%d", stack.getDisplayName(), stack.stackSize)));
                        lines.add(new LineString<TileEntityItemTransmitter>(SAPUtils.translatePostFormat("Turret: %s", turret.getTurretName())));
                        lines.add(new LineString<TileEntityItemTransmitter>(SAPUtils.translatePostFormat("  @ x:%.0f y:%.0f z:%.0f", turret.getEntity().posX, turret.getEntity().posY, turret.getEntity().posZ)));
                        lines.add(new LineString<TileEntityItemTransmitter>(SAPUtils.translatePostFormat("Expiration time: %d", transmitter.requestTimeout)));
                    }

                    this.drawTooltip(lines, transmitter, -3, -3, 0xA085C96F);
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

                    List<TooltipLine<EntityTurretBase>> lines = new ArrayList<>();

                    lines.add(new LineString<EntityTurretBase>("Turret: %s", turret.getTurretName()));
                    lines.add(new LineString<EntityTurretBase>("Frequency: %s", turret.getFrequency()));
                    lines.add(new LineHealthBar<EntityTurretBase>(turret.getHealth(), turret.getMaxHealth()));
                    lines.add(new LineAmmoBar<EntityTurretBase>(turret.getAmmo(), turret.getMaxAmmo()));

                    this.drawTooltip(lines, turret, -3, -3, (0xA0 << 24) | (clr.getRed() << 16) | (clr.getGreen() << 8) | clr.getBlue());
                }
            }

            GL11.glPopMatrix();
        }
    }

    private <T> void drawTooltip(List<TooltipLine<T>> lines, T obj, int x, int y, int frameClr) {
        int maxWidth = 0;
        int maxHeight = 0;

        for( TooltipLine<T> line : lines ) {
            int wdt = line.getWidth(obj);
            if( maxWidth < wdt ) {
                maxWidth = wdt;
            }

            maxHeight += line.getHeight(obj) + (maxHeight == 0 ? 0 : 1);
        }

        int darkFrameClr = subtractClrRel(frameClr, 128);

        Gui.drawRect(x, y, x + maxWidth + 6, y + 1, frameClr);
        Gui.drawRect(x, y + maxHeight + 5, x + maxWidth + 6, y + maxHeight + 6, darkFrameClr);
        this.drawGradientRect(x, y + 1, x + 1, y + maxHeight + 5, frameClr, darkFrameClr);
        this.drawGradientRect(x + maxWidth + 5, y + 1, x + maxWidth + 6, y + maxHeight + 5, frameClr, darkFrameClr);
        Gui.drawRect(x + 1, y + 1, x + maxWidth + 5, y + maxHeight + 5, 0xA0000000);

        int currHgt = 0;
        for( TooltipLine<T> line : lines ) {
            line.renderLine(x + 3, y + 3 + currHgt, 0xFFFFFFFF);
            currHgt += 1 + line.getHeight(obj);
        }
    }

    private static int subtractClrRel(int color, int subt) {
        RGBAValues clr = new RGBAValues(color);
        int red = clr.getRed() - (int) ((clr.getRed() / (float) 0xFF) * subt);
        int green = clr.getGreen() - (int) ((clr.getGreen() / (float) 0xFF) * subt);
        int blue = clr.getBlue() - (int) ((clr.getBlue() / (float) 0xFF) * subt);
        return new RGBAValues(red, green, blue, clr.getAlpha()).getColorInt();
    }
}
