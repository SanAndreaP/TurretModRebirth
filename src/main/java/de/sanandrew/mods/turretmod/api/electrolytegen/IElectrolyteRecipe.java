/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.electrolytegen;

import de.sanandrew.mods.turretmod.api.ILeveledInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nonnull;

public interface IElectrolyteRecipe
        extends IRecipe<ILeveledInventory>
{
    float getEfficiency();

    int getProcessTime();

    @Nonnull
    @Override
    default ItemStack assemble(@Nonnull ILeveledInventory inv) {
        return getTrashResult(inv);
    }

    ItemStack getTrashResult(ILeveledInventory inv);

    ItemStack getTreasureResult(ILeveledInventory inv);

    float getTrashChance();

    float getTreasureChance();

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }
}
