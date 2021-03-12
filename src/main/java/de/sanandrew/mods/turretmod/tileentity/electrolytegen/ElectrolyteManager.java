/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.electrolytegen;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteManager;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})

public class ElectrolyteManager
        implements IElectrolyteManager
{
    public static final IRecipeType<IElectrolyteRecipe> TYPE     = IRecipeType.register(TmrConstants.ID + ":electrolyte_generator");
    public static final ElectrolyteManager              INSTANCE = new ElectrolyteManager();

//    private final Map<ResourceLocation, IElectrolyteRecipe> fuels;

//    private List<IElectrolyteRecipe> cacheFuels;

    @Override
    public List<IElectrolyteRecipe> getFuels(World world) {
        return world.getRecipeManager().getRecipes().stream()
                    .filter(r -> r instanceof IElectrolyteRecipe && r.getType() == TYPE)
                    .map(r -> (IElectrolyteRecipe) r)
                    .collect(Collectors.toList());
//        if( this.cacheFuels == null ) {
//            this.cacheFuels = Collections.unmodifiableList(new ArrayList<>(fuels.values()));
//        }
//
//        return this.cacheFuels;
    }

    @Override
    public IElectrolyteRecipe getFuel(World world, ResourceLocation id) {
        return world.getRecipeManager().getRecipe(id).map(r -> (IElectrolyteRecipe) r).orElse(EmptyRecipe.INSTANCE);
    }

    @Override
    public IElectrolyteRecipe getFuel(World world, ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) ) {
            return null;
        }

        for( IElectrolyteRecipe recipe : this.getFuels(world) ) {
            for( ItemStack key : recipe.getIngredients().get(0).getMatchingStacks() ) {
                if( ItemStackUtils.areEqual(key, stack, key.hasTag(), false) ) {
                    return recipe;
                }
            }
        }

        return null;
    }

//    @Override
//    public boolean registerFuel(IElectrolyteRecipe recipe) {
//        ResourceLocation id = recipe.getId();
//
//        if( recipe.getEfficiency() < 1.0F ) {
//            TmrConstants.LOG.log(Level.ERROR, String.format("Efficiency cannot be less than 1.0 for electrolyte recipe %s!", id), new InvalidParameterException());
//            return false;
//        }
//
//        if( recipe.getProcessTime() < 0 ) {
//            TmrConstants.LOG.log(Level.ERROR, String.format("Processing time cannot be less than 0 for electrolyte recipe %s!", id), new InvalidParameterException());
//            return false;
//        }
//
//        this.fuels.put(id, recipe);
//
//        this.cacheFuels = null;
//
//        return true;
//    }

//    @Override
//    public void removeFuel(ResourceLocation id) {
//        this.fuels.remove(id);
//
//        this.cacheFuels = null;
//    }
//
//    public void clearFuels() {
//        this.fuels.clear();
//
//        this.cacheFuels = null;
//    }

    private static final class EmptyRecipe
            extends ElectrolyteRecipe
    {
        static final EmptyRecipe INSTANCE = new EmptyRecipe();

        public EmptyRecipe() {
            super(new ResourceLocation(TmrConstants.ID, "null"), Ingredient.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, 0, 0, 0, 0);
        }
    }
}
