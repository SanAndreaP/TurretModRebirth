/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class LineString<T>
        implements TooltipLine<T>
{
    private static FontRenderer tooltipFR;

    private String line;

    public LineString(String line) {
        if( tooltipFR == null ) {
            Minecraft mc = Minecraft.getMinecraft();
            tooltipFR = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, true);
            if( mc.gameSettings.language != null ) {
                tooltipFR.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
            }
        }

        this.line = line;
    }

    @Override
    public int getWidth(T object) {
        return tooltipFR.getStringWidth(this.line);
    }

    @Override
    public int getHeight(T object) {
        return tooltipFR.FONT_HEIGHT;
    }

    @Override
    public void renderLine(int x, int y, float partTicks) {
        tooltipFR.drawString(this.line, x, y, 0xFFFFFFFF);
    }
}
