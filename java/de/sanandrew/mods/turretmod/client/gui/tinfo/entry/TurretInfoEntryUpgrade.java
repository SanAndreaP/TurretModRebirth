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
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

public class TurretInfoEntryUpgrade
        extends TurretInfoEntry
{
    private int drawHeight;
    private TurretUpgrade upgrade;
    private long lastTimestamp;

    public TurretInfoEntryUpgrade(UUID upgId) {
        this(UpgradeRegistry.INSTANCE.getUpgrade(upgId));
    }

    private TurretInfoEntryUpgrade(TurretUpgrade upgrade) {
        super(UpgradeRegistry.INSTANCE.getUpgradeItem(upgrade), Lang.ITEM_UPGRADE_NAME.get(upgrade.getName()));
        this.upgrade = upgrade;
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks) {
        TurretUpgrade prereq = this.upgrade.getDependantOn();
        int infoHeight = 54;

        gui.mc.fontRendererObj.drawString(TextFormatting.ITALIC + Lang.translate(this.getTitle()), 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        gui.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawTexturedModalRect(2, 16, 192, 18, 34, 34);

        TmrClientUtils.renderStackInGui(UpgradeRegistry.INSTANCE.getUpgradeItem(this.upgrade), 3, 17, 2.0F);

        gui.mc.fontRendererObj.drawString(this.txtCrft, 42, 16, 0xFF6A6A6A, false);
        if( prereq != null ) {
            gui.mc.fontRendererObj.drawString(this.txtPrereq, 42, 36, 0xFF6A6A6A, false);
            infoHeight = 56;
        }

        Gui.drawRect(2, infoHeight, MAX_ENTRY_WIDTH - 2, infoHeight + 1, 0xFF0080BB);

        String text = Lang.translate(String.format(Lang.ITEM_UPGRADE_DESC.get(), upgrade.getName())).replace("\\n", "\n");
        gui.mc.fontRendererObj.drawSplitString(text, 2, infoHeight + 3, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        this.drawHeight = gui.mc.fontRendererObj.splitStringWidth(text, MAX_ENTRY_WIDTH - 2) + infoHeight + 3 + 2;

        TurretAssemblyRecipes.RecipeEntry recipeEntry = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(this.upgrade.getRecipeId());
        for( int i = 0; i < recipeEntry.resources.length; i++ ) {
            ItemStack[] stacks = recipeEntry.resources[i].getEntryItemStacks();
            drawMiniItem(gui, 45 + 10 * i, 25, mouseX, mouseY, scrollY, stacks[(int)(this.lastTimestamp / 1000L % stacks.length)], recipeEntry.resources[i].shouldDrawTooltip());
        }

        if( prereq != null ) {
            drawMiniItem(gui, 45, 45, mouseX, mouseY, scrollY, UpgradeRegistry.INSTANCE.getUpgradeItem(prereq), true);
        }

        long time = System.currentTimeMillis();
        if( this.lastTimestamp + 1000 < time ) {
            this.lastTimestamp = time;
        }
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }
}
