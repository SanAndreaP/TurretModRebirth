package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
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
}
