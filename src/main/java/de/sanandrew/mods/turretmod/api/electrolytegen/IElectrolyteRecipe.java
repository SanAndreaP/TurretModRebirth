/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.electrolytegen;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IElectrolyteRecipe
        extends IRecipe<IElectrolyteInventory> //TODO: implement in 1.14
{
    float getEfficiency();

    int getProcessTime();

    default ItemStack getCraftingResult(IElectrolyteInventory inv) {
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
