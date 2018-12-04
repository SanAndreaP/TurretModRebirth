/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.assembly;

import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ITurretAssemblyRegistry
{
    boolean registerRecipe(ResourceLocation id, IRecipeGroup group, @Nonnull ItemStack result, int fluxPerTick, int ticksProcessing, IRecipeItem... resources);

    IRecipeGroup registerGroup(String name, @Nonnull ItemStack stack);

    IRecipeGroup getGroup(String name);

    @Nonnull
    ItemStack getRecipeResult(ResourceLocation id);

    Map<ResourceLocation, RecipeEntry> getRecipeList();
}
