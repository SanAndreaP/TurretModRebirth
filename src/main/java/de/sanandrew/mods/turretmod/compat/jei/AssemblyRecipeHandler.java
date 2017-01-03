package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class AssemblyRecipeHandler
        implements IRecipeHandler<TurretAssemblyRecipes.RecipeKeyEntry>
{
    @Override
    public Class<TurretAssemblyRecipes.RecipeKeyEntry> getRecipeClass() {
        return TurretAssemblyRecipes.RecipeKeyEntry.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return AssemblyRecipeCategory.UID;
    }

    @Override
    public String getRecipeCategoryUid(TurretAssemblyRecipes.RecipeKeyEntry recipe) {
        return AssemblyRecipeCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(TurretAssemblyRecipes.RecipeKeyEntry recipe) {
        return new AssemblyRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(TurretAssemblyRecipes.RecipeKeyEntry recipe) {
        return ItemStackUtils.isValid(recipe.stack());
    }
}
