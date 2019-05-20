/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.compat.jei;

import com.google.common.collect.ImmutableList;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.util.Lang;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
class AssemblyRecipeWrapper
        implements IRecipeWrapper
{
    private final List<List<ItemStack>> input;
    private final List<ItemStack> output;
    private final int fluxPerTick;
    private final int timeInTicks;

    private AssemblyRecipeWrapper(IAssemblyRecipe recipe) {
        ImmutableList.Builder<List<ItemStack>> inputBuilder = ImmutableList.builder();
        for( Ingredient item : recipe.getIngredients()) {
            inputBuilder.add(Arrays.asList(item.getMatchingStacks()));
        }
        this.input = inputBuilder.build();
        this.fluxPerTick = recipe.getFluxPerTick();
        this.timeInTicks = recipe.getProcessTime();
        this.output = ImmutableList.of(recipe.getRecipeOutput());
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(ItemStack.class, this.input);
        ingredients.setOutputs(ItemStack.class, this.output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String s = LangUtils.translate(Lang.JEI_ASSEMBLY_ENERGY) + ' ' + this.fluxPerTick * this.timeInTicks + " RF";
        minecraft.fontRenderer.drawString(s, 0, 90, 0xFF808080);
        s = LangUtils.translate(Lang.JEI_ASSEMBLY_TIME) + ' ' + MiscUtils.getTimeFromTicks(this.timeInTicks);
        minecraft.fontRenderer.drawString(s, 0, 100, 0xFF808080);
    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return ImmutableList.of();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public static class Factory
            implements IRecipeWrapperFactory<IAssemblyRecipe>
    {
        @Override
        public IRecipeWrapper getRecipeWrapper(IAssemblyRecipe recipe) {
            return new AssemblyRecipeWrapper(recipe);
        }
    }
}
