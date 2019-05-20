/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Map;

@JEIPlugin
public class JeiPlugin
    implements IModPlugin
{
    public JeiPlugin() { }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new AssemblyRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(IAssemblyRecipe.class, new AssemblyRecipeWrapper.Factory(), AssemblyRecipeCategory.UID);

        registry.addRecipes(AssemblyManager.INSTANCE.getRecipes(), AssemblyRecipeCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.TURRET_ASSEMBLY), AssemblyRecipeCategory.UID);
    }
}
