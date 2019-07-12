/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.label;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelElement;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.gui.FontRenderer;

public class LabelTurretName
        implements ILabelElement
{
    @Override
    public boolean showElement(ITurretInst turretInst) {
        return true;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public float getHeight(ITurretInst turretInst, FontRenderer fontRenderer) {
        return fontRenderer.FONT_HEIGHT + 2.0F;
    }

    @Override
    public float getWidth(ITurretInst turretInst, FontRenderer stdFontRenderer) {
        return stdFontRenderer.getStringWidth(getName(turretInst));
    }

    @Override
    public void renderTextured(ITurretInst turretInst, float maxWidth, float progress, FontRenderer fontRenderer) {
        int color = new ColorObj(1.0F, 1.0F, 1.0F, Math.max(progress, 4.0F / 255.0F)).getColorInt();
        fontRenderer.drawString(getName(turretInst), 0.0F, 0.0F, color, false);
    }

    private static String getName(ITurretInst turret) {
        return turret.get().hasCustomName() ? turret.get().getCustomNameTag() : LangUtils.translate(Lang.ENTITY_NAME.get(turret.getTurret().getId()));
    }
}
