/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.assembly;

import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ITurretAssemblyRegistry
{
    boolean registerRecipe(UUID uuid, IRecipeGroup group, @Nonnull ItemStack result, int fluxPerTick, int ticksProcessing, IRecipeEntry... resources);

    IRecipeGroup registerGroup(String name, @Nonnull ItemStack stack);

    IRecipeGroup getGroup(String name);

    List<TurretAssemblyRegistry.RecipeKeyEntry> getRecipeList();
}
