/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.assembly;

import de.sanandrew.mods.turretmod.api.ILeveledInventory;
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
    NonNullList<ICountedIngredient> getCountedIngredients();
}
