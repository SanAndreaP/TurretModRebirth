/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.tooltip;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class LineString<T>
        implements TooltipLine<T>
{
    private static FontRenderer tooltipFR;

    private String line;
    private Object[] formatData;

    public LineString(String line, Object... data) {
        if( tooltipFR == null ) {
            Minecraft mc = Minecraft.getMinecraft();
            tooltipFR = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, true);
            if( mc.gameSettings.language != null ) {
                tooltipFR.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
            }
        }

        this.line = line;
        this.formatData = data;
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
        tooltipFR.drawString(SAPUtils.translatePostFormat(this.line, this.formatData), x, y, 0xFFFFFFFF);
    }
}
