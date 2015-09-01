/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.tooltip;

import net.minecraft.client.gui.Gui;

public class LineTestBox<T>
        implements TooltipLine<T>
{
    @Override
    public int getWidth(T object) {
        return 50;
    }

    @Override
    public int getHeight(T object) {
        return 10;
    }

    @Override
    public void renderLine(int x, int y, float partTicks) {
        Gui.drawRect(x, y, x + 50, y + 10, 0xFFFF0000);
    }
}
