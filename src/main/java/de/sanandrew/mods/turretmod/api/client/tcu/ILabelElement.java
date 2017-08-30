/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ILabelElement
{
    boolean showElement(ITurretInst turretInst);

    float getHeight(ITurretInst turretInst, FontRenderer stdFontRenderer);

    float getWidth(ITurretInst turretInst, FontRenderer stdFontRenderer);

    default void doRenderQuads(ITurretInst turretInst, float maxWidth, float progress, FontRenderer stdFontRenderer, float currHeight, BufferBuilder tessBuffer) { }

    default void doRenderTextured(ITurretInst turretInst, float maxWidth, float progress, FontRenderer stdFontRenderer) { }

    default int getPriority() {
        return 0;
    }
}
