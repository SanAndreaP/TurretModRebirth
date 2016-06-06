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
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

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
        super(UpgradeRegistry.INSTANCE.getUpgradeItem(upgrade), String.format("item.%s:turret_upgrade.%s.name", TurretModRebirth.ID, upgrade.getName()));
        this.upgrade = upgrade;
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks) {
        TurretUpgrade prereq = this.upgrade.getDependantOn();
        int infoHeight = 54;

        gui.mc.fontRenderer.drawString(EnumChatFormatting.ITALIC + StatCollector.translateToLocal(this.getTitle()), 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        gui.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawTexturedModalRect(2, 16, 192, 18, 34, 34);
//        Gui.drawRect(2, 16, 38, 52, 0xFF808080);

        drawItem(gui.mc, 3, 17, UpgradeRegistry.INSTANCE.getUpgradeItem(this.upgrade), 2.0F);

        gui.mc.fontRenderer.drawString(StatCollector.translateToLocal(String.format("gui.%s.tinfo.crafting", TurretModRebirth.ID)), 42, 16, 0xFF6A6A6A, false);
        if( prereq != null ) {
            gui.mc.fontRenderer.drawString(StatCollector.translateToLocal(String.format("gui.%s.tinfo.prereq", TurretModRebirth.ID)), 42, 36, 0xFF6A6A6A, false);
            infoHeight = 56;
        }

        Gui.drawRect(2, infoHeight, MAX_ENTRY_WIDTH - 2, infoHeight + 1, 0xFF0080BB);

        String text = StatCollector.translateToLocal(String.format("item.%s:turret_upgrade.%s.desc", TurretModRebirth.ID, upgrade.getName())).replace("\\n", "\n");
        gui.mc.fontRenderer.drawSplitString(text, 2, infoHeight + 3, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        this.drawHeight = gui.mc.fontRenderer.splitStringWidth(text, MAX_ENTRY_WIDTH - 2) + infoHeight + 3 + 2;

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
