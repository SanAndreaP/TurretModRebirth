/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.assembly;

import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface ITurretAssemblyRegistry
{
    boolean registerRecipe(UUID uuid, IRecipeGroup group, ItemStack result, int fluxPerTick, int ticksProcessing, IRecipeEntry... resources);

    IRecipeGroup registerGroup(String name, ItemStack stack);
}
