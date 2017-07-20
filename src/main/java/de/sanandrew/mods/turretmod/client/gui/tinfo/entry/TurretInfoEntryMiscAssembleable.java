/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.turretmod.client.gui.tinfo.GuiTurretInfo;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class TurretInfoEntryMiscAssembleable
        extends TurretInfoEntry
{
    private int drawHeight;
    private TurretAssemblyRegistry.RecipeEntry recipe;
    private String desc;
    private long lastTimestamp;

    public TurretInfoEntryMiscAssembleable(ItemStack stack, UUID recipeId) {
        this(stack, TurretAssemblyRegistry.INSTANCE.getRecipeEntry(recipeId));
    }

    private TurretInfoEntryMiscAssembleable(ItemStack stack, TurretAssemblyRegistry.RecipeEntry recipeEntry) {
        super(stack, stack.getUnlocalizedName() + ".name");
        this.recipe = recipeEntry;
        this.desc = stack.getUnlocalizedName() + ".desc";
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks) {
        int infoHeight = 54;

        gui.mc.fontRenderer.drawString(TextFormatting.ITALIC + Lang.translate(this.getTitle()), 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        gui.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawTexturedModalRect(2, 16, 192, 18, 34, 34);

        RenderUtils.renderStackInGui(this.getIcon(), 3, 17, 2.0F);

        gui.mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_CRAFTING.get()), 42, 16, 0xFF6A6A6A, false);

        Gui.drawRect(2, infoHeight, MAX_ENTRY_WIDTH - 2, infoHeight + 1, 0xFF0080BB);

        String text = Lang.translate(this.desc).replace("\\n", "\n");
        gui.mc.fontRenderer.drawSplitString(text, 2, infoHeight + 3, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        this.drawHeight = gui.mc.fontRenderer.getWordWrappedHeight(text, MAX_ENTRY_WIDTH - 2) + infoHeight + 3 + 2;

        for( int i = 0; i < this.recipe.resources.length; i++ ) {
            ItemStack[] stacks = this.recipe.resources[i].getEntryItemStacks();
            drawMiniItem(gui, 45 + 10 * i, 25, mouseX, mouseY, scrollY, stacks[(int)(this.lastTimestamp / 1000L % stacks.length)], this.recipe.resources[i].shouldDrawTooltip());
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
