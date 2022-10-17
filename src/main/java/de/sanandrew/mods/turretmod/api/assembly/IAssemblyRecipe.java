/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.assembly;

import de.sanandrew.mods.turretmod.api.ILeveledInventory;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public interface IAssemblyRecipe
        extends IRecipe<ILeveledInventory>
{
    int getEnergyConsumption();

    int getProcessTime();

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Nonnull
    NonNullList<AssemblyRecipe.CountedIngredient> getCountedIngredients();
}
