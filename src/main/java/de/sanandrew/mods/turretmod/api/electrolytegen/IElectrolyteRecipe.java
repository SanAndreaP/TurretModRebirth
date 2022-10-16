/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
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
