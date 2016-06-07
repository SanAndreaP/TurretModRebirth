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
import de.sanandrew.mods.turretmod.tileentity.TileEntityPotatoGenerator;
import de.sanandrew.mods.turretmod.util.CraftingRecipes;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TurretInfoEntryGenerator
        extends TurretInfoEntryMiscCraftable
{
    private int drawHeight;
    private static ItemStack tooltipItem;

    public TurretInfoEntryGenerator() {
        super(CraftingRecipes.potatoGenerator);
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks) {
        super.drawPage(gui, mouseX, mouseY, scrollY, partTicks);

        Map<Item, TileEntityPotatoGenerator.Fuel> fuels = TileEntityPotatoGenerator.getFuels();
        List<Item> fuelItems = new ArrayList<>(fuels.keySet());

        this.drawHeight = super.getPageHeight() + 3;

        Gui.drawRect(2, this.drawHeight, MAX_ENTRY_WIDTH - 2, this.drawHeight + 1, 0xFF0080BB);

        this.drawHeight += 3;

        int maxItems = 9;
        tooltipItem = null;
        for( int i = 0, cnt = fuelItems.size(); i < cnt; i++ ) {
            int x = i % maxItems;
            int y = i / maxItems;

            drawFuelItem(gui, 3 + 18 * x, this.drawHeight + 18 * y, mouseX, mouseY, scrollY, new ItemStack(fuelItems.get(i)), true);
        }

        if( tooltipItem != null ) {
            drawTooltipFuel(gui.mc, scrollY);
        }

        this.drawHeight += (fuelItems.size() / maxItems) * 18 + 20 + 48;
    }

    private static void drawTooltipFuel(Minecraft mc, int scrollY) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, MAX_ENTRY_HEIGHT - 48 + scrollY, 64.0F);
        Gui.drawRect(0, 0, MAX_ENTRY_WIDTH, 48, 0xD0000000);

        mc.fontRenderer.drawString(String.format("§e%s", TmrClientUtils.getTooltipWithoutShift(tooltipItem).get(0)), 22, 2, 0xFFFFFFFF, false);
        TileEntityPotatoGenerator.Fuel fuel = TileEntityPotatoGenerator.getFuel(tooltipItem.getItem());
        mc.fontRenderer.drawString(String.format("%.2fx efficiency", fuel.effect), 22, 11, 0xFFFFFFFF, false);
        mc.fontRenderer.drawString(String.format("decays in %s", TmrClientUtils.getTimeFromTicks(fuel.ticksProc)), 22, 20, 0xFFFFFFFF, false);
        mc.fontRenderer.drawString(String.format("§a%s", TmrClientUtils.getTooltipWithoutShift(fuel.trash).get(0)), 32, 29, 0xFFFFFFFF, false);
        mc.fontRenderer.drawString(String.format("§d%s", TmrClientUtils.getTooltipWithoutShift(fuel.treasure).get(0)), 32, 38, 0xFFFFFFFF, false);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        ITEM_RENDER.zLevel = -50.0F;
        ITEM_RENDER.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), tooltipItem, 2, 12);

        GL11.glPushMatrix();
        GL11.glTranslatef(22.0F, 28.0F, 0.0F);
        GL11.glScalef(0.5F, 0.5F, 1.0F);
        ITEM_RENDER.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), fuel.trash, 0, 0);
        ITEM_RENDER.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), fuel.treasure, 0, 18);
        GL11.glPopMatrix();

        ITEM_RENDER.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), tooltipItem, 12, 2);
        ITEM_RENDER.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }

    private static void drawFuelItem(GuiTurretInfo gui, int x, int y, int mouseX, int mouseY, int scrollY, ItemStack stack, boolean drawTooltip) {
        gui.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawTexturedModalRect(x, y, 192, 0, 18, 18);

        GL11.glPushMatrix();
        boolean mouseOver = mouseY >= 0 && mouseY < MAX_ENTRY_HEIGHT && mouseX >= x && mouseX < x + 18 && mouseY >= y - scrollY && mouseY < y + 18 - scrollY;

        if( mouseOver ) {
            tooltipItem = stack;
        }

        GL11.glTranslatef(x, y, 32.0F);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        ITEM_RENDER.zLevel = -50.0F;
        ITEM_RENDER.renderItemAndEffectIntoGUI(gui.mc.fontRenderer, gui.mc.getTextureManager(), stack, 1, 1);
        ITEM_RENDER.renderItemOverlayIntoGUI(gui.mc.fontRenderer, gui.mc.getTextureManager(), stack, 1, 1);
        ITEM_RENDER.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        if( mouseOver ) {
            GL11.glTranslatef(0, 0, 32.0F);
            Gui.drawRect(1, 1, 17, 17, 0x80FFFFFF);
        }

        GL11.glPopMatrix();
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }
}
