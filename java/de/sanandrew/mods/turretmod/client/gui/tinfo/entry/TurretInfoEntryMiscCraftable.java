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
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;

public class TurretInfoEntryMiscCraftable
        extends TurretInfoEntry
{
    private final String desc;
    private final Triplet<ItemStack[][], Integer, Integer> crafting;

    private int drawHeight;
    private long lastTimestamp;

    public TurretInfoEntryMiscCraftable(IRecipe recipe) { this(recipe.getRecipeOutput(), recipe); }

    private TurretInfoEntryMiscCraftable(ItemStack stack, IRecipe recipe) {
        super(stack, String.format("%s.name", stack.getUnlocalizedName()));
        this.desc = String.format("%s.desc", stack.getUnlocalizedName());
        if( recipe != null ) {
            if( recipe instanceof ShapedOreRecipe ) {
                this.crafting = getCrafting((ShapedOreRecipe) recipe);
            } else if( recipe instanceof ShapedRecipes ) {
                this.crafting = getCrafting((ShapedRecipes) recipe);
            } else {
                this.crafting = Triplet.with(null, 0, 0);
            }
        } else {
            this.crafting = Triplet.with(null, 0, 0);
        }
    }

    private static Triplet<ItemStack[][], Integer, Integer> getCrafting(ShapedRecipes cRecipe) {
        ItemStack[][] crfArray = new ItemStack[cRecipe.recipeWidth * cRecipe.recipeHeight][];

        for( int i = 0; i < cRecipe.recipeHeight; i++ ) {
            for( int j = 0; j < cRecipe.recipeWidth; j++ ) {
                ItemStack recpStack = cRecipe.recipeItems[i * cRecipe.recipeWidth + j];
                ItemStack[] recpStacks = null;
                if( recpStack != null ) {
                    List<ItemStack> stacks = new ArrayList<>();

                    if( recpStack.getItemDamage() == OreDictionary.WILDCARD_VALUE ) {
                        recpStack.getItem().getSubItems(recpStack.getItem(), CreativeTabs.tabAllSearch, stacks);
                    } else {
                        stacks.add(recpStack);
                    }

                    recpStacks = stacks.toArray(new ItemStack[stacks.size()]);
                }

                crfArray[i * cRecipe.recipeWidth + j] = recpStacks;
            }
        }

        return Triplet.with(crfArray, cRecipe.recipeWidth, cRecipe.recipeHeight);
    }

    private static Triplet<ItemStack[][], Integer, Integer> getCrafting(ShapedOreRecipe cRecipe) {
        int recipeWidth = TmrUtils.getOreRecipeWidth(cRecipe);
        int recipeHeight = TmrUtils.getOreRecipeHeight(cRecipe);
        ItemStack[][] crfArray = new ItemStack[recipeWidth * recipeHeight][];

        for( int i = 0; i < recipeHeight; i++ ) {
            for( int j = 0; j < recipeWidth; j++ ) {
                Object recpObj = cRecipe.getInput()[i * recipeWidth + j];
                ItemStack[] recpStacks = null;
                if( recpObj != null ) {
                    List<ItemStack> stacks = new ArrayList<>();

                    if( recpObj instanceof ItemStack ) {
                        ItemStack recpStack = (ItemStack) recpObj;
                        if( recpStack.getItemDamage() == OreDictionary.WILDCARD_VALUE ) {
                            recpStack.getItem().getSubItems(recpStack.getItem(), CreativeTabs.tabAllSearch, stacks);
                        } else {
                            stacks.add(recpStack);
                        }
                    } else if( recpObj instanceof ArrayList ) {
                        //noinspection unchecked
                        for( ItemStack recpStack : (ArrayList<ItemStack>) recpObj ) {
                            if( recpStack != null ) {
                                if( recpStack.getItemDamage() == OreDictionary.WILDCARD_VALUE ) {
                                    recpStack.getItem().getSubItems(recpStack.getItem(), CreativeTabs.tabAllSearch, stacks);
                                } else {
                                    stacks.add(recpStack);
                                }
                            }
                        }
                    }

                    recpStacks = stacks.toArray(new ItemStack[stacks.size()]);
                }

                crfArray[i * recipeWidth + j] = recpStacks;
            }
        }

        return Triplet.with(crfArray, recipeWidth, recipeHeight);
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks) {
        gui.mc.fontRenderer.drawString(EnumChatFormatting.ITALIC + Lang.translate(this.getTitle()), 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        gui.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawTexturedModalRect(2, 16, 192, 18, 34, 34);

        drawItem(gui.mc, 3, 17, this.getIcon(), 2.0F);

        gui.mc.fontRenderer.drawString(this.txtWorkbench, 42, 16, 0xFF6A6A6A, false);

        this.drawHeight = 27 + 9 * this.crafting.getValue2();

        Gui.drawRect(2, this.drawHeight, MAX_ENTRY_WIDTH - 2, this.drawHeight + 1, 0xFF0080BB);

        String text = Lang.translate(this.desc).replace("\\n", "\n");
        gui.mc.fontRenderer.drawSplitString(text, 2, this.drawHeight + 3, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        this.drawHeight = gui.mc.fontRenderer.splitStringWidth(text, MAX_ENTRY_WIDTH - 2) + this.drawHeight + 3 + 2;

        for( int i = 0, maxI = this.crafting.getValue1(); i < maxI; i++ ) {
            for( int j = 0, maxJ = this.crafting.getValue2(); j < maxJ; j++ ) {

                ItemStack crfStack[] = this.crafting.getValue0()[i*3 + j];
                if( crfStack != null && crfStack.length > 0 ) {
                    drawCrfItem(gui, 42 + 9 * j, 25 + 9 * i, mouseX, mouseY, scrollY, crfStack[(int)(this.lastTimestamp / 1000L % crfStack.length)], true);
                }
            }
        }

        long time = System.currentTimeMillis();
        if( this.lastTimestamp + 1000 < time ) {
            this.lastTimestamp = time;
        }
    }

    private static void drawCrfItem(GuiTurretInfo gui, int x, int y, int mouseX, int mouseY, int scrollY, ItemStack stack, boolean drawTooltip) {
        boolean mouseOver = mouseY >= 0 && mouseY < MAX_ENTRY_HEIGHT && mouseX >= x && mouseX < x + 9 && mouseY >= y - scrollY && mouseY < y + 9 - scrollY;
        if( mouseOver ) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, MAX_ENTRY_HEIGHT - 20 + scrollY, 64.0F);
            Gui.drawRect(0, 0, MAX_ENTRY_WIDTH, 20, 0xD0000000);

            List tooltip = TmrClientUtils.getTooltipWithoutShift(stack);
            gui.mc.fontRenderer.drawString(tooltip.get(0).toString(), 22, 2, 0xFFFFFFFF, false);
            if( drawTooltip && tooltip.size() > 1 ) {
                gui.mc.fontRenderer.drawString(tooltip.get(1).toString(), 22, 11, 0xFF808080, false);
            }

            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            ITEM_RENDER.zLevel = -50.0F;
            ITEM_RENDER.renderItemAndEffectIntoGUI(gui.mc.fontRenderer, gui.mc.getTextureManager(), stack, 2, 2);
            ITEM_RENDER.renderItemOverlayIntoGUI(gui.mc.fontRenderer, gui.mc.getTextureManager(), stack, 2, 2);
            ITEM_RENDER.zLevel = 0.0F;
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);

            GL11.glPopMatrix();
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 32.0F);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        gui.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawTexturedModalRect(0, 0, 192, 0, 18, 18);
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
