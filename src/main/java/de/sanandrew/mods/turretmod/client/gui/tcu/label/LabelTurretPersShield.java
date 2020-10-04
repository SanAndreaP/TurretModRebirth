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
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import de.sanandrew.mods.turretmod.registry.Lang;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.ShieldPersonal;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;

public class LabelTurretPersShield
        implements ILabelElement
{
    @Override
    public boolean showElement(ITurretInst turretInst) {
        return turretInst.getUpgradeProcessor().hasUpgrade(Upgrades.SHIELD_PERSONAL);
    }

    @Override
    public float getHeight(ITurretInst turretInst, FontRenderer fontRenderer) {
        return fontRenderer.FONT_HEIGHT + 6.0F;
    }

    @Override
    public float getWidth(ITurretInst turretInst, FontRenderer stdFontRenderer) {
        return stdFontRenderer.getStringWidth(getLabel(turretInst));
    }

    @Override
    public void renderQuads(ITurretInst turretInst, float maxWidth, float progress, FontRenderer fontRenderer, float currHeight, BufferBuilder buffer) {
        ShieldPersonal shield = turretInst.getUpgradeProcessor().getUpgradeInstance(Upgrades.SHIELD_PERSONAL.getId());
        float healthPerc = shield.getValue() / ShieldPersonal.MAX_VALUE * maxWidth;

        currHeight += fontRenderer.FONT_HEIGHT + 2.0F;

        ClientProxy.addQuad(buffer, 0.0D, currHeight, healthPerc, currHeight + 2.0D, new ColorObj(0.87F, 0.45F, 1.0F, Math.max(progress, 4.0F / 255.0F)));
        ClientProxy.addQuad(buffer, healthPerc, currHeight, maxWidth, currHeight + 2.0D, new ColorObj(0.22F, 0.11F, 0.4F, Math.max(progress, 4.0F / 255.0F)));
    }

    @Override
    public void renderTextured(ITurretInst turretInst, float maxWidth, float progress, FontRenderer fontRenderer) {
        fontRenderer.drawString(getLabel(turretInst), 0.0F, 0.0F, new ColorObj(0.87F, 0.45F, 1.0F, Math.max(progress, 4.0F / 255.0F)).getColorInt(), false);
    }

    private static String getLabel(ITurretInst turretInst) {
        ShieldPersonal shield = turretInst.getUpgradeProcessor().getUpgradeInstance(Upgrades.SHIELD_PERSONAL.getId());

        if( shield.isInRecovery() ) {
            return LangUtils.translate(Lang.TCU_LABEL_PRSSHIELD_RECV, String.format("%.0f %%", shield.getRecoveryPercentage()));
        } else {
            return LangUtils.translate(Lang.TCU_LABEL_PRSSHIELD, String.format("%.2f/%.2f", shield.getValue(), ShieldPersonal.MAX_VALUE));
        }
    }
}
