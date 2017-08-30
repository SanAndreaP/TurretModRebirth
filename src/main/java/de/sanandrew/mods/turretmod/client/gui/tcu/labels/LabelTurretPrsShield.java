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
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradePrsShield;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LabelTurretPrsShield
        implements ILabelElement
{
    @Override
    public boolean showElement(ITurretInst turretInst) {
        return turretInst.getUpgradeProcessor().hasUpgrade(UpgradeRegistry.SHIELD);
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
        UpgradePrsShield.Shield shield = turretInst.getUpgradeProcessor().getUpgradeInstance(UpgradeRegistry.SHIELD);
        float healthPerc = shield.getValue() / UpgradePrsShield.Shield.MAX_VALUE * (maxWidth - 2.0F);

        currHeight += stdFontRenderer.FONT_HEIGHT + 2.0F;

        addQuad(tessBuffer, 1.0D, currHeight, 1.0D + healthPerc, currHeight + 2.0D, new ColorObj(0.3F, 1.0F, 0.3F, Math.max(progress, 0x4 / 255.0F)));
        addQuad(tessBuffer, 1.0D + healthPerc, currHeight, maxWidth - 1.0F, currHeight + 2.0D, new ColorObj(0.1F, 0.4F, 0.1F, Math.max(progress, 0x4 / 255.0F)));
    }

    @Override
    public void doRenderTextured(ITurretInst turretInst, float maxWidth, float progress, FontRenderer stdFontRenderer) {
        UpgradePrsShield.Shield shield = turretInst.getUpgradeProcessor().getUpgradeInstance(UpgradeRegistry.SHIELD);
        String s;

        if( shield.isInRecovery() ) {
            s = Lang.translate(Lang.TCU_LABEL_SHIELD_RECV, String.format("%.0f %%", shield.getRecoveryPercentage()));
        } else {
            s = Lang.translate(Lang.TCU_LABEL_SHIELD, String.format("%.2f/%.2f", shield.getValue(), UpgradePrsShield.Shield.MAX_VALUE));
        }
        stdFontRenderer.drawString(s, 1.0F, 1.0F, new ColorObj(0.3F, 1.0F, 0.3F, Math.max(progress, 0x4 / 255.0F)).getColorInt(), false);
    }

    private static void addQuad(BufferBuilder buf, double minX, double minY, double maxX, double maxY, ColorObj clr) {
        buf.pos(minX, minY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(minX, maxY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(maxX, maxY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(maxX, minY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
    }
}
