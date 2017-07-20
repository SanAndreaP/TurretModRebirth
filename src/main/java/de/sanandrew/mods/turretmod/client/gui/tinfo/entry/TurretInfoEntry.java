/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.turretmod.client.gui.tinfo.GuiTurretInfo;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class TurretInfoEntry
{
    public static final int MAX_ENTRY_WIDTH = 168;
    public static final int MAX_ENTRY_HEIGHT = 183;
    private ItemStack icon;
    private String title;

    protected TurretInfoEntry(ItemStack icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public final ItemStack getIcon() {
        return this.icon.copy();
    }

    public final String getTitle() {
        return this.title;
    }

    protected static void drawMiniItem(GuiTurretInfo gui, int x, int y, int mouseX, int mouseY, int scrollY, ItemStack stack, boolean drawTooltip) {
        gui.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - 0.5F, y - 0.5F, 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        gui.drawTexturedModalRect(0, 0, 192, 0, 18, 18);
        GlStateManager.popMatrix();

        boolean mouseOver = mouseY >= 0 && mouseY < MAX_ENTRY_HEIGHT && mouseX >= x && mouseX < x + 8 && mouseY >= y - scrollY && mouseY < y + 8 - scrollY;
        if( mouseOver && stack != null ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, MAX_ENTRY_HEIGHT - 20 + scrollY, 32.0F);
            Gui.drawRect(0, 0, MAX_ENTRY_WIDTH, 20, 0xD0000000);

            List tooltip = GuiUtils.getTooltipWithoutShift(stack);
            gui.mc.fontRenderer.drawString(tooltip.get(0).toString(), 22, 2, 0xFFFFFFFF, false);
            if( drawTooltip && tooltip.size() > 1 ) {
                gui.mc.fontRenderer.drawString(tooltip.get(1).toString(), 22, 11, 0xFF808080, false);
            }

            RenderUtils.renderStackInGui(stack, 2, 2, 1.0F, gui.mc.fontRenderer);

            GlStateManager.popMatrix();
        }

        if( stack != null ) {
            RenderUtils.renderStackInGui(stack, x, y, 0.5F);
        }

        if( mouseOver ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 64.0F);
            Gui.drawRect(x, y, x + 8, y + 8, 0x80FFFFFF);
            GlStateManager.popMatrix();
        }
    }

    public abstract void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks);

    public abstract int getPageHeight();

    public boolean actionPerformed(GuiButton btn) {
        return false;
    }

    public void initEntry(GuiTurretInfo gui) { }
}
