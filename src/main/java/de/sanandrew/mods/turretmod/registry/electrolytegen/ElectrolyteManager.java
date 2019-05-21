/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.electrolytegen;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteManager;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ElectrolyteManager
        implements IElectrolyteManager
{
    public static final ElectrolyteManager INSTANCE = new ElectrolyteManager();

    private final Map<ResourceLocation, IElectrolyteRecipe> fuels = new HashMap<>();

    private List<IElectrolyteRecipe> cacheFuels;

    @Override
    public List<IElectrolyteRecipe> getFuels() {
        if( this.cacheFuels == null ) {
            this.cacheFuels = Collections.unmodifiableList(new ArrayList<>(fuels.values()));
        }

        return this.cacheFuels;
    }

    @Override
    public IElectrolyteRecipe getFuel(ResourceLocation id) {
        return this.fuels.get(id);
    }

    @Override
    public IElectrolyteRecipe getFuel(ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) ) {
            return null;
        }

        for( IElectrolyteRecipe recipe : this.fuels.values() ) {
            for( ItemStack key : recipe.getIngredients().get(0).getMatchingStacks() ) {
                if( ItemStackUtils.areEqual(key, stack, key.hasTagCompound(), false, key.getItemDamage() == OreDictionary.WILDCARD_VALUE) ) {
                    return recipe;
                }
            }
        }

        return null;
    }

    @Override
    public boolean registerFuel(IElectrolyteRecipe recipe) {
        ResourceLocation id = recipe.getId();
        if( id == null ) {
            TmrConstants.LOG.log(Level.ERROR, "ID for electrolyte recipe cannot be null!", new InvalidParameterException());
            return false;
        }

        if( recipe.getEfficiency() < 1.0F ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Efficiency cannot be less than 1.0 for electrolyte recipe %s!", id), new InvalidParameterException());
            return false;
        }

        if( recipe.getProcessTime() < 0 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Processing time cannot be less than 0 for electrolyte recipe %s!", id), new InvalidParameterException());
            return false;
        }

        this.fuels.put(id, recipe);

        this.cacheFuels = null;

        return true;
    }

    @Override
    public void removeFuel(ResourceLocation id) {
        this.fuels.remove(id);

        this.cacheFuels = null;
    }
}
