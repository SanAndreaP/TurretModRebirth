/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.tooltip;

public interface TooltipLine<T>
{
    int getWidth(T object);
    int getHeight(T object);
    void renderLine(int x, int y, float partTicks);
}
