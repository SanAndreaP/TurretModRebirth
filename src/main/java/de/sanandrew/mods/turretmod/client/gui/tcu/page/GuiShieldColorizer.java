/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.ShieldColorizer;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

public class GuiShieldColorizer
        extends Gui
        implements IGuiTCU
{
    private static final int MAX_HUE = 360;
    private static final int MAX_SATURATION = 100;
    private static final int MAX_LUMEN = 100;

    private static final int[] DISP_MIN_MAX_HUE = {32, 132};
    private static final int[] DISP_MIN_MAX_SAT = {40, 140};
    private static final int[] DISP_MIN_MAX_LUM = {136, 144, 40, 140};

    private GuiTextField rgbColor;
    private float[] hsl = new float[3];
    private float alpha;

    @Override
    public void initGui(IGuiTcuInst<?> gui) {
        ShieldColorizer settings = getSettings(gui);
        if( settings != null ) {
            this.rgbColor = new GuiTextField(0, gui.getFontRenderer(), gui.getPosX() + 110, gui.getPosY() + 157, 60, 10);
            this.rgbColor.setMaxStringLength(9);
            this.rgbColor.setValidator(s -> {
                if( Strings.isNullOrEmpty(s) ) {
                    return true;
                }
                Integer val = getInteger(s);
                return val != null;
            });

            ColorObj color = new ColorObj(settings.getColor());
            this.updateColor(color);
        }
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        ShieldColorizer settings = getSettings(gui);
        if( settings != null ) {
            this.rgbColor.updateCursorCounter();
        }
    }

    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        gui.getGui().mc.renderEngine.bindTexture(Resources.GUI_TCU_COLORIZER.resource);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        gui.getGui().drawTexturedModalRect(gui.getPosX(), gui.getPosY(), 0, 0, gui.getWidth(), gui.getHeight());

        if( this.rgbColor != null ) {
            this.rgbColor.drawTextBox();
        }
    }

    @Override
    public void drawForeground(IGuiTcuInst<?> gui, int mouseX, int mouseY) {
        int h = Math.round(this.hsl[0] / 360.0F * 99.0F);
        int s = 99 - Math.round(this.hsl[1] * 99.0F);
        int l = 99 - Math.round(this.hsl[2] * 99.0F);

        Gui.drawRect(DISP_MIN_MAX_LUM[0], DISP_MIN_MAX_LUM[2], DISP_MIN_MAX_LUM[1], DISP_MIN_MAX_LUM[3], ColorObj.fromHSLA(this.hsl[0], this.hsl[1], 0.5F, 1.0F).getColorInt());

        int lumenHalf = DISP_MIN_MAX_LUM[2] + (DISP_MIN_MAX_LUM[3] - DISP_MIN_MAX_LUM[2]) / 2;
        this.drawGradientRect(DISP_MIN_MAX_LUM[0], DISP_MIN_MAX_LUM[2], DISP_MIN_MAX_LUM[1], lumenHalf, 0xFFFFFFFF, 0x00FFFFFF);
        this.drawGradientRect(DISP_MIN_MAX_LUM[0], lumenHalf, DISP_MIN_MAX_LUM[1], DISP_MIN_MAX_LUM[3], 0x00000000, 0xFF000000);

        gui.getGui().mc.renderEngine.bindTexture(Resources.GUI_TCU_COLORIZER.resource);
        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        this.drawTexturedModalRect(DISP_MIN_MAX_LUM[0], DISP_MIN_MAX_LUM[2], 248, 5, 8, 100);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ZERO);
        this.drawTexturedModalRect(DISP_MIN_MAX_LUM[0] - 1, DISP_MIN_MAX_LUM[2] - 2 + l, 244, 0, 12, 5);

        this.drawTexturedModalRect(DISP_MIN_MAX_HUE[0] - 2 + h, DISP_MIN_MAX_SAT[0] - 2 + s, 239, 0, 5, 5);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void onMouseClick(IGuiTcuInst<?> gui, int mouseX, int mouseY, int mouseButton) {
        int mouseGuiX = mouseX - gui.getPosX();
        int mouseGuiY = mouseY - gui.getPosY();

        if( mouseGuiX >= DISP_MIN_MAX_HUE[0] && mouseGuiX < DISP_MIN_MAX_HUE[1] && mouseGuiY >= DISP_MIN_MAX_SAT[0] && mouseGuiY < DISP_MIN_MAX_SAT[1] ) {
            this.hsl[0] = TmrUtils.wrap360((Math.round(Math.rint(mouseGuiX * 1.01D)) - DISP_MIN_MAX_HUE[0]) * 360.0F / (DISP_MIN_MAX_HUE[1] - DISP_MIN_MAX_HUE[0]));
            this.hsl[1] = (100 - Math.round(Math.rint(mouseGuiY * 1.01D)) + DISP_MIN_MAX_SAT[0]) / (float) (DISP_MIN_MAX_SAT[1] - DISP_MIN_MAX_SAT[0]);

            setColor(ColorObj.fromHSLA(hsl[0], hsl[1], hsl[2], this.alpha), gui.getTurretInst());
        } else if( mouseGuiX >= DISP_MIN_MAX_LUM[0] && mouseGuiX < DISP_MIN_MAX_LUM[1] && mouseGuiY >= DISP_MIN_MAX_LUM[2] && mouseGuiY < DISP_MIN_MAX_LUM[3] ) {
            this.hsl[2] = (100 - Math.round(Math.rint(mouseGuiY * 1.01D)) + DISP_MIN_MAX_LUM[2]) / (float) (DISP_MIN_MAX_LUM[3] - DISP_MIN_MAX_LUM[2]);

            setColor(ColorObj.fromHSLA(hsl[0], hsl[1], hsl[2], this.alpha), gui.getTurretInst());
        } else {
            this.rgbColor.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public boolean doKeyIntercept(IGuiTcuInst<?> gui, char typedChar, int keyCode) {
        if( this.rgbColor.textboxKeyTyped(typedChar, keyCode) ) {
            ShieldColorizer settings = getSettings(gui);
            String s = this.rgbColor.getText();
            if( settings != null && s.length() == 8 ) {
                Integer val = getInteger(s);
                if( val != null ) {
                    ColorObj color = new ColorObj(val);
                    this.updateColor(color);
                    setColor(color, gui.getTurretInst());
                }
            }

            return true;
        }

        return false;
    }

//    private static void drawGroupBox(FontRenderer fontRenderer, String title, int x, int y) {
//        final int width2 = 162;
//        final int height2 = 30;
//        final int frameColor2 = 0x30000000;
//
//        int strWidth = fontRenderer.getStringWidth(title);
//        GlStateManager.enableBlend();
//        fontRenderer.drawString(title, x + 5, y, 0x80000000, false);
//
//        Gui.drawRect(x,                y + 4,          x + 3,     y + 5,          frameColor2);
//        Gui.drawRect(x + strWidth + 6, y + 4,          x + width2, y + 5,          frameColor2);
//        Gui.drawRect(x,                y + height2 - 1, x + width2, y + height2,     frameColor2);
//        Gui.drawRect(x,                y + 5,          x + 1,     y + height2 - 1, frameColor2);
//        Gui.drawRect(x + width2 - 1,    y + 5,          x + width2, y + height2 - 1, frameColor2);
//    }

    public static boolean showTab(IGuiTcuInst<?> gui) {
        return gui.hasPermision() && gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.SHIELD_COLORIZER);
    }

    private static ShieldColorizer getSettings(IGuiTcuInst<?> gui) {
        ShieldColorizer settings = gui.getTurretInst().getUpgradeProcessor().getUpgradeInstance(Upgrades.SHIELD_COLORIZER.getId());
        if( settings == null ) {
            gui.getGui().mc.player.closeScreen();
        }
        return settings;
    }

    private void setColor(ColorObj color, ITurretInst syncWith) {
        if( syncWith != null ) {
            ShieldColorizer settings = syncWith.getUpgradeProcessor().getUpgradeInstance(Upgrades.SHIELD_COLORIZER.getId());
            if( settings != null ) {
                int iColor = color.getColorInt();
                settings.setColor(iColor);
                this.rgbColor.setText(String.format("%08X", iColor));
                syncSettings(syncWith);
            }
        }
    }

    private void updateColor(ColorObj color) {
        int colorInt = color.getColorInt();
        this.rgbColor.setText(String.format("%08X", colorInt));
        this.hsl = color.calcHSL();
        this.alpha = color.alpha();
    }

    private static void syncSettings(ITurretInst turretInst) {
        UpgradeRegistry.INSTANCE.syncWithServer(turretInst, Upgrades.SHIELD_COLORIZER.getId());
    }

    private static Integer getInteger(String s) {
        try {
            s = s.startsWith("0x") ? s : "0x" + s;
            long l = Long.decode(s);
            return (int)(l & 0xFFFFFFFFL);
        } catch( NumberFormatException ex ) {
            return null;
        }
    }
}
