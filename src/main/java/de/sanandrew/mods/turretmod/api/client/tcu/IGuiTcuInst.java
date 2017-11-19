/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public interface IGuiTcuInst<T extends GuiScreen>
{
    T getGui();

    ITurretInst getTurretInst();

    int getPosX();

    int getPosY();

    int getGuiWidth();

    int getGuiHeight();

    boolean hasPermision();

    <U extends GuiButton> U addNewButton(U button);

    int getNewButtonId();

    FontRenderer getFontRenderer();

    void drawGradient(int left, int top, int right, int bottom, int startColor, int endColor);
}
