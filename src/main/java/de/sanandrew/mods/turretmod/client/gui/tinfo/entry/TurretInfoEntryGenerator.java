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
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.turretinfo.IGuiTurretInfo;
import de.sanandrew.mods.turretmod.registry.electrolytegen.ElectrolyteRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class TurretInfoEntryGenerator
        extends TurretInfoEntryMiscCraftable
{
    private int drawHeight;

    public TurretInfoEntryGenerator(IRecipe recipe) {
        super(recipe);
    }

    @Nonnull
    private NonNullList<ItemStack> fuelItems;
    @Nonnull
    private ItemStack tooltipItem;

    @Override
    public void drawPage(int mouseX, int mouseY, int scrollY, float partTicks) {
        super.drawPage(mouseX, mouseY, scrollY, partTicks);

        this.drawHeight = super.getPageHeight() + 3;

        Gui.drawRect(2, this.drawHeight, MAX_ENTRY_WIDTH - 2, this.drawHeight + 1, 0xFF0080BB);

        this.drawHeight += 3;

        int maxItems = 9;
        this.tooltipItem = ItemStackUtils.getEmpty();
        for( int i = 0, cnt = this.fuelItems.size(); i < cnt; i++ ) {
            int x = i % maxItems;
            int y = i / maxItems;

            this.drawFuelItem(3 + 18 * x, this.drawHeight + 18 * y, mouseX, mouseY, scrollY, this.fuelItems.get(i));
        }

        if( ItemStackUtils.isValid(this.tooltipItem) ) {
            this.drawTooltipFuel(this.guiInfo.__getMc(), scrollY);
        }

        this.drawHeight += (fuelItems.size() / maxItems) * 18 + 20 + 48;
    }

    private void drawTooltipFuel(Minecraft mc, int scrollY) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, MAX_ENTRY_HEIGHT - 48 + scrollY, 100.0F);
        Gui.drawRect(0, 0, MAX_ENTRY_WIDTH, 48, 0xD0000000);

        mc.fontRenderer.drawString(String.format("§e%s", GuiUtils.getTooltipWithoutShift(tooltipItem).get(0)), 22, 2, 0xFFFFFFFF, false);
        ElectrolyteRegistry.Fuel fuel = ElectrolyteRegistry.getFuel(tooltipItem);
        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_EFFICIENCY.get(), fuel.effect), 22, 11, 0xFFFFFFFF, false);
        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_DECAY.get(), MiscUtils.getTimeFromTicks(fuel.ticksProc)), 22, 20, 0xFFFFFFFF, false);
        mc.fontRenderer.drawString(String.format("§a%s", GuiUtils.getTooltipWithoutShift(fuel.trash).get(0)), 32, 29, 0xFFFFFFFF, false);
        mc.fontRenderer.drawString(String.format("§d%s", GuiUtils.getTooltipWithoutShift(fuel.treasure).get(0)), 32, 38, 0xFFFFFFFF, false);

        RenderUtils.renderStackInGui(tooltipItem, 2, 12, 1.0F, mc.fontRenderer);
        RenderUtils.renderStackInGui(fuel.trash, 22, 28, 0.5F);
        RenderUtils.renderStackInGui(fuel.treasure, 22, 37, 0.5F);

        GlStateManager.popMatrix();
    }

    private void drawFuelItem(int x, int y, int mouseX, int mouseY, int scrollY, @Nonnull ItemStack stack) {
        this.guiInfo.__getMc().getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.guiInfo.__drawTexturedRect(x, y, 192, 0, 18, 18);

        GlStateManager.pushMatrix();
        boolean mouseOver = mouseY >= 0 && mouseY < MAX_ENTRY_HEIGHT && mouseX >= x && mouseX < x + 18 && mouseY >= y - scrollY && mouseY < y + 18 - scrollY;

        if( mouseOver ) {
            this.tooltipItem = stack;
        }

        GlStateManager.translate(x, y, 32.0F);

        RenderUtils.renderStackInGui(stack, 1, 1, 1.0D, this.guiInfo.__getMc().fontRenderer);

        if( mouseOver ) {
            GlStateManager.translate(0, 0, 64.0F);
            Gui.drawRect(1, 1, 17, 17, 0x80FFFFFF);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void initEntry(IGuiTurretInfo gui) {
        super.initEntry(gui);

        Set<ItemStack> fuelList = ElectrolyteRegistry.getFuelMap().keySet();
        this.fuelItems = NonNullList.from(ItemStackUtils.getEmpty(), fuelList.toArray(new ItemStack[fuelList.size()]));
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }
}
