/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.CraftingUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.client.turretinfo.IGuiTurretInfo;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoEntry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class TurretInfoEntryMiscCraftable
        implements ITurretInfoEntry
{
    private final String desc;
    private final Tuple crafting;

    private int drawHeight;
    private long lastTimestamp;

    protected IGuiTurretInfo guiInfo;
    private final ItemStack icon;
    private final String title;

    public TurretInfoEntryMiscCraftable(IRecipe recipe) {
        this(recipe != null ? recipe.getRecipeOutput() : ItemStackUtils.getEmpty(), recipe);
    }

    private TurretInfoEntryMiscCraftable(@Nonnull ItemStack stack, IRecipe recipe) {
        this.icon = stack;
        this.title = String.format("%s.name", stack.getUnlocalizedName());
        this.desc = String.format("%s.desc", stack.getUnlocalizedName());
        if( recipe != null ) {
            if( recipe instanceof ShapedOreRecipe ) {
                this.crafting = getCrafting((ShapedOreRecipe) recipe);
            } else if( recipe instanceof ShapedRecipes ) {
                this.crafting = getCrafting((ShapedRecipes) recipe);
            } else {
                this.crafting = new Tuple(null, 0, 0);
            }
        } else {
            this.crafting = new Tuple(null, 0, 0);
        }
    }

    @Nonnull
    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public String getTitle() {
        return Lang.translate(this.title);
    }

    @Override
    public void initEntry(IGuiTurretInfo gui) {
        this.guiInfo = gui;
    }

    private static Tuple getCrafting(ShapedRecipes cRecipe) {
        ItemStack[][] crfArray = new ItemStack[cRecipe.recipeWidth * cRecipe.recipeHeight][];

        for( int i = 0; i < cRecipe.recipeHeight; i++ ) {
            for( int j = 0; j < cRecipe.recipeWidth; j++ ) {
                ItemStack recpStack = cRecipe.recipeItems[i * cRecipe.recipeWidth + j];
                ItemStack[] recpStacks = null;
                if( ItemStackUtils.isValid(recpStack) ) {
                    NonNullList<ItemStack> stacks = NonNullList.withSize(1, recpStack);
                    recpStacks = stacks.toArray(new ItemStack[stacks.size()]);
                }

                crfArray[i * cRecipe.recipeWidth + j] = recpStacks;
            }
        }

        return new Tuple(crfArray, cRecipe.recipeWidth, cRecipe.recipeHeight);
    }

    private static Tuple getCrafting(ShapedOreRecipe cRecipe) {
        int recipeWidth = CraftingUtils.getOreRecipeWidth(cRecipe);
        int recipeHeight = CraftingUtils.getOreRecipeHeight(cRecipe);
        ItemStack[][] crfArray = new ItemStack[recipeWidth * recipeHeight][];

        for( int i = 0; i < recipeHeight; i++ ) {
            for( int j = 0; j < recipeWidth; j++ ) {
                Object recpObj = cRecipe.getInput()[i * recipeWidth + j];
                ItemStack[] recpStacks = null;
                if( recpObj != null ) {
                    List<ItemStack> stacks = new ArrayList<>();

                    if( recpObj instanceof ItemStack && ItemStackUtils.isValid((ItemStack) recpObj) ) {
                        ItemStack recpStack = (ItemStack) recpObj;
                        stacks.add(recpStack);
                    } else if( recpObj instanceof List ) {
//                        noinspection unchecked
                        ((List<ItemStack>) recpObj).stream().filter(input -> input != null && ItemStackUtils.isValid(input)).forEach(stacks::add);
                    }

                    recpStacks = stacks.toArray(new ItemStack[stacks.size()]);
                }

                crfArray[i * recipeWidth + j] = recpStacks;
            }
        }

        return new Tuple(crfArray, recipeWidth, recipeHeight);
    }

    @Override
    public void drawPage(int mouseX, int mouseY, int scrollY, float partTicks) {
        Minecraft mc = this.guiInfo.__getMc();
        
        mc.fontRenderer.drawString(TextFormatting.ITALIC + Lang.translate(this.getTitle()), 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.guiInfo.__drawTexturedRect(2, 16, 192, 18, 34, 34);

        RenderUtils.renderStackInGui(this.icon, 3, 17, 2.0F);

        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_WORKBENCH.get()), 42, 16, 0xFF6A6A6A, false);

        this.drawHeight = 27 + 9 * this.crafting.<Integer>getValue(2);

        Gui.drawRect(2, this.drawHeight, MAX_ENTRY_WIDTH - 2, this.drawHeight + 1, 0xFF0080BB);

        String text = Lang.translate(this.desc).replace("\\n", "\n");
        mc.fontRenderer.drawSplitString(text, 2, this.drawHeight + 3, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        this.drawHeight = mc.fontRenderer.getWordWrappedHeight(text, MAX_ENTRY_WIDTH - 2) + this.drawHeight + 3 + 2;

        for( int i = 0, maxI = this.crafting.getValue(1); i < maxI; i++ ) {
            for( int j = 0, maxJ = this.crafting.getValue(2); j < maxJ; j++ ) {

                ItemStack crfStack[] = this.crafting.<ItemStack[][]>getValue(0)[i*3 + j];
                ItemStack drawnStack = ItemStackUtils.getEmpty();
                if( crfStack != null && crfStack.length > 0 ) {
                    drawnStack = crfStack[(int)(this.lastTimestamp / 1000L % crfStack.length)];
                }

                this.guiInfo.drawMiniItem(42 + 9 * j, 25 + 9 * i, mouseX, mouseY, scrollY, drawnStack, true);
            }
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
