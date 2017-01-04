/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AssemblyRecipeHandler
        implements IRecipeHandler<TurretAssemblyRegistry.RecipeKeyEntry>
{
    @Override
    public Class<TurretAssemblyRegistry.RecipeKeyEntry> getRecipeClass() {
        return TurretAssemblyRegistry.RecipeKeyEntry.class;
    }

    @Override
    @Deprecated
    public String getRecipeCategoryUid() {
        return AssemblyRecipeCategory.UID;
    }

    @Override
    public String getRecipeCategoryUid(TurretAssemblyRegistry.RecipeKeyEntry recipe) {
        return AssemblyRecipeCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(TurretAssemblyRegistry.RecipeKeyEntry recipe) {
        return new AssemblyRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(TurretAssemblyRegistry.RecipeKeyEntry recipe) {
        return ItemStackUtils.isValid(recipe.stack());
    }
}
