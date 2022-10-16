/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.electrolyte;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteManager;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

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
