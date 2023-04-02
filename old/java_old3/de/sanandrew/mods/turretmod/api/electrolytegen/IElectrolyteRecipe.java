/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.electrolytegen;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IElectrolyteRecipe
//        extends IRecipe //TODO: implement in 1.14
{
    ResourceLocation getId();

    float getEfficiency();

    int getProcessTime();

    boolean matches(IInventory inv, World worldIn);

    ItemStack getCraftingResult(IInventory inv);

    ItemStack getTreasureResult(IInventory inv);

    default boolean canFit(int width, int height) {
        return true;
    }

    ItemStack getRecipeOutput();

    NonNullList<Ingredient> getIngredients();

    String getGroup();

    float getTrashChance();

    float getTreasureChance();
}
