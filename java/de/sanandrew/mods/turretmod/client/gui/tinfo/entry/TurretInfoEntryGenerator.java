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
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.CraftingRecipes;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
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

        Map<Item, TileEntityElectrolyteGenerator.Fuel> fuels = TileEntityElectrolyteGenerator.getFuels();
        List<Item> fuelItems = new ArrayList<>(fuels.keySet());

        this.drawHeight = super.getPageHeight() + 3;

        Gui.drawRect(2, this.drawHeight, MAX_ENTRY_WIDTH - 2, this.drawHeight + 1, 0xFF0080BB);

        this.drawHeight += 3;

        int maxItems = 9;
        tooltipItem = null;
        for( int i = 0, cnt = fuelItems.size(); i < cnt; i++ ) {
            int x = i % maxItems;
            int y = i / maxItems;

            drawFuelItem(gui, 3 + 18 * x, this.drawHeight + 18 * y, mouseX, mouseY, scrollY, new ItemStack(fuelItems.get(i)));
        }

        if( tooltipItem != null ) {
            drawTooltipFuel(gui.mc, scrollY);
        }

        this.drawHeight += (fuelItems.size() / maxItems) * 18 + 20 + 48;
    }

    private static void drawTooltipFuel(Minecraft mc, int scrollY) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, MAX_ENTRY_HEIGHT - 48 + scrollY, 64.0F);
        Gui.drawRect(0, 0, MAX_ENTRY_WIDTH, 48, 0xD0000000);

        mc.fontRendererObj.drawString(String.format("§e%s", TmrClientUtils.getTooltipWithoutShift(tooltipItem).get(0)), 22, 2, 0xFFFFFFFF, false);
        TileEntityElectrolyteGenerator.Fuel fuel = TileEntityElectrolyteGenerator.getFuel(tooltipItem.getItem());
        mc.fontRendererObj.drawString(String.format(Lang.translate(Lang.TINFO_ENTRY_EFFICIENCY.get()), fuel.effect), 22, 11, 0xFFFFFFFF, false);
        mc.fontRendererObj.drawString(String.format(Lang.translate(Lang.TINFO_ENTRY_DECAY.get()), TmrClientUtils.getTimeFromTicks(fuel.ticksProc)), 22, 20, 0xFFFFFFFF, false);
        mc.fontRendererObj.drawString(String.format("§a%s", TmrClientUtils.getTooltipWithoutShift(fuel.trash).get(0)), 32, 29, 0xFFFFFFFF, false);
        mc.fontRendererObj.drawString(String.format("§d%s", TmrClientUtils.getTooltipWithoutShift(fuel.treasure).get(0)), 32, 38, 0xFFFFFFFF, false);

        TmrClientUtils.renderStackInGui(tooltipItem, 2, 12, 1.0F, mc.fontRendererObj);
        TmrClientUtils.renderStackInGui(fuel.trash, 22, 28, 0.5F);
        TmrClientUtils.renderStackInGui(fuel.treasure, 22, 37, 0.5F);

        GlStateManager.popMatrix();
    }

    private static void drawFuelItem(GuiTurretInfo gui, int x, int y, int mouseX, int mouseY, int scrollY, ItemStack stack) {
        gui.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawTexturedModalRect(x, y, 192, 0, 18, 18);

        GlStateManager.pushMatrix();
        boolean mouseOver = mouseY >= 0 && mouseY < MAX_ENTRY_HEIGHT && mouseX >= x && mouseX < x + 18 && mouseY >= y - scrollY && mouseY < y + 18 - scrollY;

        if( mouseOver ) {
            tooltipItem = stack;
        }

        GlStateManager.translate(x, y, 32.0F);

        TmrClientUtils.renderStackInGui(stack, 1, 1, 1.0D, gui.mc.fontRendererObj);

        if( mouseOver ) {
            GlStateManager.translate(0, 0, 64.0F);
            Gui.drawRect(1, 1, 17, 17, 0x80FFFFFF);
        }

        GL11.glPopMatrix();
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }
}
