/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.tileentity.electrolyte;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.electrolytegen.IElectrolyteManager;
import dev.sanandrea.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})

public class ElectrolyteManager
        implements IElectrolyteManager
{
    public static final IRecipeType<IElectrolyteRecipe> TYPE     = IRecipeType.register(TmrConstants.ID + ":electrolyte_generator");
    public static final ElectrolyteManager              INSTANCE = new ElectrolyteManager();

    @Override
    public List<IElectrolyteRecipe> getFuels(World world) {
        return world.getRecipeManager().getAllRecipesFor(TYPE);
    }

    @Override
    public IElectrolyteRecipe getFuel(World world, ResourceLocation id) {
        return world.getRecipeManager().byKey(id).map(IElectrolyteRecipe.class::cast).orElse(EmptyRecipe.INSTANCE);
    }

    @Override
    public IElectrolyteRecipe getFuel(World world, ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) ) {
            return null;
        }

        for( IElectrolyteRecipe recipe : this.getFuels(world) ) {
            for( ItemStack key : recipe.getIngredients().get(0).getItems() ) {
                if( ItemStackUtils.areEqual(key, stack, key.hasTag(), false) ) {
                    return recipe;
                }
            }
        }

        return null;
    }

    private static final class EmptyRecipe
            extends ElectrolyteRecipe
    {
        static final EmptyRecipe INSTANCE = new EmptyRecipe();

        public EmptyRecipe() {
            super(new ResourceLocation(TmrConstants.ID, "null"), Ingredient.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, 0, 0, 0, 0);
        }
    }
}
