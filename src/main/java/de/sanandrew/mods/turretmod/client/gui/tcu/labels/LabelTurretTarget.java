/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.labels;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelElement;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LabelTurretTarget
        implements ILabelElement
{
    @Override
    public boolean showElement(ITurretInst turretInst) {
        return true;
    }

    @Override
    public float getHeight(ITurretInst turretInst, FontRenderer stdFontRenderer) {
        return stdFontRenderer.FONT_HEIGHT + 2.0F;
    }

    @Override
    public float getWidth(ITurretInst turretInst, FontRenderer stdFontRenderer) {
        return stdFontRenderer.getStringWidth(getLabel(turretInst)) + 2.0F;
    }

    @Override
    public void doRenderTextured(ITurretInst turretInst, float maxWidth, float progress, FontRenderer stdFontRenderer) {
        int color = new ColorObj(1.0F, 1.0F, 0.625F, Math.max(progress, 0x4 / 255.0F)).getColorInt();
        stdFontRenderer.drawString(getLabel(turretInst), 1.0F, 1.0F, color, false);
    }

    private static String getLabel(ITurretInst turretInst) {
        Entity target = turretInst.getTargetProcessor().getTarget();
        return Lang.translate(Lang.TCU_LABEL_TARGET, target == null ? "n/a" : target instanceof EntityPlayer ? ((EntityPlayer) target).getName() : Lang.translateEntityCls(target.getClass()));
    }
}
