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
import de.sanandrew.mods.turretmod.api.client.turretinfo.IGuiTurretInfo;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoEntry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class TurretInfoEntryMiscAssembleable
        implements ITurretInfoEntry
{
    private int drawHeight;
    private long lastTimestamp;

    private IGuiTurretInfo guiInfo;
    private final ItemStack icon;
    private final TurretAssemblyRegistry.RecipeEntry recipe;
    private final String title;
    private final String desc;

    public TurretInfoEntryMiscAssembleable(ItemStack stack) {
        this(stack, TurretAssemblyRegistry.INSTANCE.getRecipeEntry(stack));
    }

    private TurretInfoEntryMiscAssembleable(ItemStack stack, TurretAssemblyRegistry.RecipeEntry recipeEntry) {
        this.icon = stack;
        this.title = stack.getUnlocalizedName() + ".name";
        this.recipe = recipeEntry;
        this.desc = stack.getUnlocalizedName() + ".desc";
    }

    @Override
    public void initEntry(IGuiTurretInfo gui) {
        this.guiInfo = gui;
    }

    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public String getTitle() {
        return Lang.translate(this.title);
    }

    @Override
    public void drawPage(int mouseX, int mouseY, int scrollY, float partTicks) {
        int infoHeight = 54;
        Minecraft mc = this.guiInfo.__getMc();

        mc.fontRenderer.drawString(TextFormatting.ITALIC + Lang.translate(this.title), 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.guiInfo.__drawTexturedRect(2, 16, 192, 18, 34, 34);

        RenderUtils.renderStackInGui(this.icon, 3, 17, 2.0F);

        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_CRAFTING.get()), 42, 16, 0xFF6A6A6A, false);

        Gui.drawRect(2, infoHeight, MAX_ENTRY_WIDTH - 2, infoHeight + 1, 0xFF0080BB);

        String text = Lang.translate(this.desc).replace("\\n", "\n");
        mc.fontRenderer.drawSplitString(text, 2, infoHeight + 3, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        this.drawHeight = mc.fontRenderer.getWordWrappedHeight(text, MAX_ENTRY_WIDTH - 2) + infoHeight + 3 + 2;

        for( int i = 0; i < this.recipe.resources.length; i++ ) {
            ItemStack[] stacks = this.recipe.resources[i].getEntryItemStacks();
            this.guiInfo.drawMiniItem(45 + 10 * i, 25, mouseX, mouseY, scrollY, stacks[(int)(this.lastTimestamp / 1000L % stacks.length)],
                    this.recipe.resources[i].shouldDrawTooltip());
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
