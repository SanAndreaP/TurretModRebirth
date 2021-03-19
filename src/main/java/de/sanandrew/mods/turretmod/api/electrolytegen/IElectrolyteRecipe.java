/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.electrolytegen;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nonnull;

public interface IElectrolyteRecipe
        extends IRecipe<IElectrolyteInventory>
{
    float getEfficiency();

    int getProcessTime();

    @Nonnull
    default ItemStack getCraftingResult(@Nonnull IElectrolyteInventory inv) {
        return getTrashResult(inv);
    }

    ItemStack getTrashResult(IElectrolyteInventory inv);

    ItemStack getTreasureResult(IElectrolyteInventory inv);

    float getTrashChance();

    float getTreasureChance();

    @Override
    default boolean canFit(int width, int height) {
        return true;
    }
}
