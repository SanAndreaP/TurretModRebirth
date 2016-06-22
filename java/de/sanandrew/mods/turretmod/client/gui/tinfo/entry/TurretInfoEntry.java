/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.turretmod.client.gui.tinfo.GuiTurretInfo;
import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

public abstract class TurretInfoEntry
{
    public static final int MAX_ENTRY_WIDTH = 168;
    public static final int MAX_ENTRY_HEIGHT = 183;
    private ItemStack icon;
    private String title;

    protected final String txtRounds;
    protected final String txtDps;
    protected final String txtHealth;
    protected final String txtHealthVal;
    protected final String txtTurret;
    protected final String txtCrft;
    protected final String txtWorkbench;
    protected final String txtPrereq;
    protected final String txtRange;
    protected final String txtAmmoCap;
    protected final String txtAmmoUse;

    protected TurretInfoEntry(ItemStack icon, String title) {
        this.icon = icon;
        this.title = title;

        this.txtRounds = Lang.translate(Lang.TINFO_ENTRY_ROUNDS.get());
        this.txtDps = Lang.translate(Lang.TINFO_ENTRY_DPS.get());
        this.txtHealth = Lang.translate(Lang.TINFO_ENTRY_HEALTH.get());
        this.txtHealthVal = Lang.translate(Lang.TINFO_ENTRY_HEALTHVAL.get());
        this.txtTurret = Lang.translate(Lang.TINFO_ENTRY_TURRET.get());
        this.txtCrft = Lang.translate(Lang.TINFO_ENTRY_CRAFTING.get());
        this.txtWorkbench = Lang.translate(Lang.TINFO_ENTRY_WORKBENCH.get());
        this.txtPrereq = Lang.translate(Lang.TINFO_ENTRY_PREREQ.get());
        this.txtRange = Lang.translate(Lang.TINFO_ENTRY_RANGE.get());
        this.txtAmmoCap = Lang.translate(Lang.TINFO_ENTRY_AMMOCAP.get());
        this.txtAmmoUse = Lang.translate(Lang.TINFO_ENTRY_AMMOUSE.get());
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

            List tooltip = TmrClientUtils.getTooltipWithoutShift(stack);
            gui.mc.fontRendererObj.drawString(tooltip.get(0).toString(), 22, 2, 0xFFFFFFFF, false);
            if( drawTooltip && tooltip.size() > 1 ) {
                gui.mc.fontRendererObj.drawString(tooltip.get(1).toString(), 22, 11, 0xFF808080, false);
            }

            TmrClientUtils.renderStackInGui(stack, 2, 2, 1.0F, gui.mc.fontRendererObj);

            GlStateManager.popMatrix();
        }

        if( stack != null ) {
            TmrClientUtils.renderStackInGui(stack, x, y, 0.5F);
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
