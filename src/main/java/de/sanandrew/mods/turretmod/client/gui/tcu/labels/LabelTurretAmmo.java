/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.labels;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelElement;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LabelTurretAmmo
        implements ILabelElement
{
    @Override
    public boolean showElement(ITurretInst turretInst) {
        return true;
    }

    @Override
    public float getHeight(ITurretInst turretInst, FontRenderer stdFontRenderer) {
        return stdFontRenderer.FONT_HEIGHT + 6.0F;
    }

    @Override
    public float getWidth(ITurretInst turretInst, FontRenderer stdFontRenderer) {
        return ILabelRegistry.MIN_WIDTH;
    }

    @Override
    public void doRenderQuads(ITurretInst turretInst, float maxWidth, float progress, FontRenderer stdFontRenderer, float currHeight, BufferBuilder tessBuffer) {
        float ammoPerc = turretInst.getTargetProcessor().getAmmoCount() / (float) turretInst.getTargetProcessor().getMaxAmmoCapacity() * (maxWidth - 2.0F);
        currHeight += stdFontRenderer.FONT_HEIGHT + 2.0F;
        addQuad(tessBuffer, 1.0D, currHeight, 1.0D + ammoPerc, currHeight + 2.0D, new ColorObj(0.625F, 0.625F, 1.0F, Math.max(progress, 0x4 / 255.0F)));
        addQuad(tessBuffer, 1.0D + ammoPerc, currHeight, maxWidth - 1.0F, currHeight + 2.0D, new ColorObj(0.05F, 0.05F, 0.4F, Math.max(progress, 0x4 / 255.0F)));
    }

    @Override
    public void doRenderTextured(ITurretInst turretInst, float maxWidth, float progress, FontRenderer stdFontRenderer) {
        String s = Lang.translate(Lang.TCU_LABEL_AMMO, String.format("%d/%d", turretInst.getTargetProcessor().getAmmoCount(), turretInst.getTargetProcessor().getMaxAmmoCapacity()));
        stdFontRenderer.drawString(s, 1.0F, 1.0F, new ColorObj(0.625F, 0.625F, 1.0F, Math.max(progress, 0x4 / 255.0F)).getColorInt(), false);
    }

    private static void addQuad(BufferBuilder buf, double minX, double minY, double maxX, double maxY, ColorObj clr) {
        buf.pos(minX, minY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(minX, maxY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(maxX, maxY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(maxX, minY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
    }
}
