package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import net.minecraft.client.renderer.GlStateManager;

public final class GuiHelper
{
    public static void initGuiDef(GuiDefinition guiDef, IGui gui) {
        if( guiDef == null ) {
            gui.get().mc.displayGuiScreen(null);
            return;
        }

        guiDef.initGui(gui);
    }

    public static void drawGDBackground(GuiDefinition guiDef, IGui gui, float partTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(gui.getScreenPosX(), gui.getScreenPosY(), 0.0F);
        guiDef.drawBackground(gui, mouseX, mouseY, partTicks);
        GlStateManager.popMatrix();
    }

    public static void drawTooltipBg(int xPos, int yPos, int w, int h) {
        final int lightBg = 0x505000FF;
        final int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;

        drawTooltipBg(xPos, yPos, w, h, 0xF0100010, lightBg, darkBg);
    }

    public static void drawTooltipBg(int xPos, int yPos, int w, int h, int bkgColor, int lightBg, int darkBg) {
        GuiUtils.drawGradientRect(xPos - 3,     yPos - 4,     w + 6, 1,     bkgColor, bkgColor, true);
        GuiUtils.drawGradientRect(xPos - 3,     yPos + h + 3, w + 6, 1,     bkgColor, bkgColor, true);
        GuiUtils.drawGradientRect(xPos - 3,     yPos - 3,     w + 6, h + 6, bkgColor, bkgColor, true);
        GuiUtils.drawGradientRect(xPos - 4,     yPos - 3,     1,     h + 6, bkgColor, bkgColor, true);
        GuiUtils.drawGradientRect(xPos + w + 3, yPos - 3,     1,     h + 6, bkgColor, bkgColor, true);

        GuiUtils.drawGradientRect(xPos - 3,     yPos - 2, 1,  h + 4,    lightBg, darkBg,  true);
        GuiUtils.drawGradientRect(xPos + w + 2, yPos - 2, 1,  h + 4,    lightBg, darkBg,  true);
        GuiUtils.drawGradientRect(xPos - 3,     yPos - 3,     w + 6, 1, lightBg, lightBg, true);
        GuiUtils.drawGradientRect(xPos - 3,     yPos + h + 2, w + 6, 1, darkBg, darkBg,   true);
    }
}
