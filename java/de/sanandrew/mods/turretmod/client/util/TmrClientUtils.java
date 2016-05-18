/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class TmrClientUtils
{
    private static Minecraft mc;

    public static Minecraft getMc() {
        if( mc == null ) {
            mc = Minecraft.getMinecraft();
        }
        return mc;
    }

    public static void doGlScissor(int x, int y, int width, int height) {
        Minecraft mc = getMc();
        int scaleFactor = 1;
        int guiScale = mc.gameSettings.guiScale;

        if( guiScale == 0 ) {
            guiScale = 1000;
        }

        while( scaleFactor < guiScale && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240 ) {
            ++scaleFactor;
        }

        GL11.glScissor(x * scaleFactor, mc.displayHeight - (y + height) * scaleFactor, width * scaleFactor, height * scaleFactor);
    }


    public static void drawTexturedModalRect(int xPos, int yPos, float z, int u, int v, int width, int height) {
        drawTexturedModalRect(xPos, yPos, z, u, v, width, height, 0.00390625F, 0.00390625F);
    }

    public static void drawTexturedModalRect(int xPos, int yPos, float z, int u, int v, int width, int height, float resScaleX, float resScaleY) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(xPos, yPos + height, z, u * resScaleX, (v + height) * resScaleY);
        tessellator.addVertexWithUV(xPos + width, yPos + height, z, (u + width) * resScaleX, (v + height) * resScaleY);
        tessellator.addVertexWithUV(xPos + width, yPos, z, (u + width) * resScaleX, v * resScaleY);
        tessellator.addVertexWithUV(xPos, yPos, z, u * resScaleX, v * resScaleY);
        tessellator.draw();
    }
}
