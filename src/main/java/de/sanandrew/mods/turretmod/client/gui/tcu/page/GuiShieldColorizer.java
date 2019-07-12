/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.event.RenderForcefieldHandler;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.ShieldColorizer;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GuiShieldColorizer
        extends Gui
        implements IGuiTCU
{
    private static final int[] DISP_MIN_MAX_HUE = {22, 122};
    private static final int[] DISP_MIN_MAX_SAT = {40, 140};
    private static final int[] DISP_MIN_MAX_LUM = {129, 138, 40, 140};
    private static final int[] DISP_MIN_MAX_ALP = {145, 154, 40, 140};

    private GuiTextField rgbColor;
    private float[] hsl = new float[3];
    private float alpha;

    private RenderForcefieldHandler.ShieldTexture[] shieldTextures;

    @Override
    public void initialize(IGuiTcuInst<?> gui) {
        this.rgbColor = new GuiTextField(0, gui.getFontRenderer(), gui.getPosX() + 94, gui.getPosY() + 180, 60, 10);
        this.rgbColor.setMaxStringLength(9);
        this.rgbColor.setValidator(s -> {
            if( Strings.isNullOrEmpty(s) ) {
                return true;
            }
            Integer val = getInteger(s);
            return val != null;
        });
        this.shieldTextures = RenderForcefieldHandler.getTextures(gui.getGui().mc.getResourceManager());

        ShieldColorizer settings = getSettings(gui);
        if( settings != null ) {
            ColorObj color = new ColorObj(settings.getColor());
            this.updateColor(color);
        }
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        this.rgbColor.updateCursorCounter();
    }

    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        GuiScreen guiScr = gui.getGui();
        guiScr.mc.renderEngine.bindTexture(Resources.GUI_TCU_COLORIZER.resource);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        guiScr.drawTexturedModalRect(gui.getPosX(), gui.getPosY(), 0, 0, gui.getWidth(), gui.getHeight());

        this.rgbColor.drawTextBox();
    }

    @Override
    public void drawForeground(IGuiTcuInst<?> gui, int mouseX, int mouseY) {
        GuiScreen guiScr = gui.getGui();
        int h = Math.round(this.hsl[0] / 360.0F * 99.0F);
        int s = 99 - Math.round(this.hsl[1] * 99.0F);
        int l = 99 - Math.round(this.hsl[2] * 99.0F);
        int a = 99 - Math.round(this.alpha * 99.0F);

        int brightColor = ColorObj.fromHSLA(this.hsl[0], this.hsl[1], 0.5F, 1.0F).getColorInt();
        Gui.drawRect(DISP_MIN_MAX_LUM[0], DISP_MIN_MAX_LUM[2], DISP_MIN_MAX_LUM[1], DISP_MIN_MAX_LUM[3], brightColor);

        int lumenHalf = DISP_MIN_MAX_LUM[2] + (DISP_MIN_MAX_LUM[3] - DISP_MIN_MAX_LUM[2]) / 2;
        this.drawGradientRect(DISP_MIN_MAX_LUM[0], DISP_MIN_MAX_LUM[2], DISP_MIN_MAX_LUM[1], lumenHalf, 0xFFFFFFFF, 0x00FFFFFF);
        this.drawGradientRect(DISP_MIN_MAX_LUM[0], lumenHalf, DISP_MIN_MAX_LUM[1], DISP_MIN_MAX_LUM[3], 0x00000000, 0xFF000000);

        ColorObj newColor = ColorObj.fromHSLA(this.hsl[0], this.hsl[1], this.hsl[2], 1.0F);
        int rgbColor = newColor.getColorInt();
        newColor.setAlpha(0);
        int rgbColorNA = newColor.getColorInt();
        this.drawGradientRect(DISP_MIN_MAX_ALP[0], DISP_MIN_MAX_ALP[2], DISP_MIN_MAX_ALP[1], DISP_MIN_MAX_ALP[3], rgbColor, rgbColorNA);

        gui.getFontRenderer().drawString("#", 87, 181, 0xA0A0A0);
        gui.getFontRenderer().drawSplitString(LangUtils.translate(Lang.TCU_COLORIZER_CLRCODE), 22, 181, 60, 0xA0A0A0);

        guiScr.mc.renderEngine.bindTexture(Resources.GUI_TCU_COLORIZER.resource);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ZERO);
        this.drawTexturedModalRect(DISP_MIN_MAX_HUE[0] - 2 + h, DISP_MIN_MAX_SAT[0] - 2 + s, 239, 0, 5, 5);
        this.drawTexturedModalRect(DISP_MIN_MAX_LUM[0] - 1, DISP_MIN_MAX_LUM[2] - 2 + l, 244, 0, 12, 5);
        this.drawTexturedModalRect(DISP_MIN_MAX_ALP[0] - 1, DISP_MIN_MAX_ALP[2] - 2 + a, 244, 0, 12, 5);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);


        ColorObj color = ColorObj.fromHSLA(this.hsl[0], this.hsl[1], this.hsl[2], this.alpha);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(color.fRed(), color.fGreen(), color.fBlue(), color.fAlpha());
        float partTicks = guiScr.mc.getRenderPartialTicks();
        for( RenderForcefieldHandler.ShieldTexture tx : this.shieldTextures ) {
            float transformTexAmount = guiScr.mc.world.getTotalWorldTime() % 400 + partTicks;
            float texTranslateX = transformTexAmount * tx.moveMultiplierX;
            float texTranslateY = transformTexAmount * tx.moveMultiplierY;

            guiScr.mc.renderEngine.bindTexture(tx.getTexture());
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.translate(texTranslateX, texTranslateY, 0.0F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);

            Gui.drawScaledCustomSizeModalRect(22, 144, 0, 0, 264, 42, 132, 21, 256, 256);

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onMouseClick(IGuiTcuInst<?> gui, int mouseX, int mouseY, int mouseButton) {
        this.rgbColor.mouseClicked(mouseX, mouseY, mouseButton);
        this.onMouseClickMove(gui, mouseX, mouseY, mouseButton, 0);
    }

    @Override
    public void onMouseClickMove(IGuiTcuInst<?> gui, int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        int mouseGuiX = mouseX - gui.getPosX();
        int mouseGuiY = mouseY - gui.getPosY();

        if( mouseGuiX >= DISP_MIN_MAX_HUE[0] && mouseGuiX < DISP_MIN_MAX_HUE[1] && mouseGuiY >= DISP_MIN_MAX_SAT[0] && mouseGuiY < DISP_MIN_MAX_SAT[1] ) {
            this.hsl[0] = TmrUtils.wrap360((Math.round(Math.rint(mouseGuiX * 1.01D)) - DISP_MIN_MAX_HUE[0]) * 360.0F / (DISP_MIN_MAX_HUE[1] - DISP_MIN_MAX_HUE[0]));
            this.hsl[1] = (100 - Math.round(Math.rint(mouseGuiY * 1.01D)) + DISP_MIN_MAX_SAT[0]) / (float) (DISP_MIN_MAX_SAT[1] - DISP_MIN_MAX_SAT[0]);

            setColor(ColorObj.fromHSLA(hsl[0], hsl[1], hsl[2], this.alpha), gui.getTurretInst());
        } else if( mouseGuiX >= DISP_MIN_MAX_LUM[0] && mouseGuiX < DISP_MIN_MAX_LUM[1] && mouseGuiY >= DISP_MIN_MAX_LUM[2] && mouseGuiY < DISP_MIN_MAX_LUM[3] ) {
            this.hsl[2] = (100 - Math.round(Math.rint(mouseGuiY * 1.01D)) + DISP_MIN_MAX_LUM[2]) / (float) (DISP_MIN_MAX_LUM[3] - DISP_MIN_MAX_LUM[2]);

            setColor(ColorObj.fromHSLA(hsl[0], hsl[1], hsl[2], this.alpha), gui.getTurretInst());
        } else if( mouseGuiX >= DISP_MIN_MAX_ALP[0] && mouseGuiX < DISP_MIN_MAX_ALP[1] && mouseGuiY >= DISP_MIN_MAX_ALP[2] && mouseGuiY < DISP_MIN_MAX_ALP[3] ) {
            this.alpha = (100 - Math.round(Math.rint(mouseGuiY * 1.01D)) + DISP_MIN_MAX_ALP[2]) / (float) (DISP_MIN_MAX_ALP[3] - DISP_MIN_MAX_ALP[2]);

            setColor(ColorObj.fromHSLA(hsl[0], hsl[1], hsl[2], this.alpha), gui.getTurretInst());
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
        this.alpha = color.fAlpha();
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
