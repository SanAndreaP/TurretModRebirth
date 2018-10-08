/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
class AssemblyRecipeCategory<T extends IRecipeWrapper>
        implements IRecipeCategory<T>
{
    static final String UID = TmrConstants.ID + ".assemblytbl";
    private final IDrawableStatic background;

    public AssemblyRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(Resources.JEI_ASSEMBLY_BKG.resource, 0, 0, 164, 130);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return LangUtils.translate(Lang.JEI_ASSEMBLY_TITLE);
    }

    @Override
    public String getModName() {
        return TmrConstants.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipeWrapper, IIngredients ingredients) {
        if( !(recipeWrapper instanceof AssemblyRecipeWrapper) ) {
            return;
        }

        recipeWrapper.getIngredients(ingredients);
        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);

        int inputSize = inputs.size();

        if( inputSize > 0 ) {
            int index = 0;
            int posX = 1;
            while( index < inputSize ) {
                List<ItemStack> stacks = inputs.get(index);
                recipeLayout.getItemStacks().init(index, true, posX, 1);
                recipeLayout.getItemStacks().set(index, stacks);
                index++;
                posX += 18;
            }
        }

        recipeLayout.getItemStacks().init(inputSize, false, 73, 66);
        recipeLayout.getItemStacks().set(ingredients);
//        recipeLayout.getItemStacks().set(inputSize, ingredients.getOutputs(ItemStack.class));
    }
}
