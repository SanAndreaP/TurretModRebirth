/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.assembly;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyManager;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public final class AssemblyManager
        implements IAssemblyManager
{
    public static final AssemblyManager INSTANCE = new AssemblyManager();

    private final Map<ResourceLocation, IAssemblyRecipe> recipes = new LinkedHashMap<>();
    private final Map<String, List<ResourceLocation>> groups = new HashMap<>();
    private Map<String, ItemStack> groupIcons = new HashMap<>();

    private String[] cacheGroupNames;
    private List<IAssemblyRecipe> cacheRecipes;
    private Map<String, List<IAssemblyRecipe>> cacheGroupToRecipes;

    @Override
    public boolean registerRecipe(@Nonnull IAssemblyRecipe recipe) {
        ResourceLocation id = recipe.getId();
        if( id == null ) {
            TmrConstants.LOG.log(Level.ERROR, "ID for assembly recipe cannot be null!", new InvalidParameterException());
            return false;
        }
        if( recipe.getFluxPerTick() < 0 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Flux usage cannot be smaller than 0 for assembly recipe %s!", id), new InvalidParameterException());
            return false;
        }
        if( recipe.getProcessTime() < 0 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Processing time cannot be smaller than 0 for assembly recipe %s!", id), new InvalidParameterException());
            return false;
        }

        if( recipes.containsKey(id) ){
            this.removeRecipe(id);
        }

        this.recipes.put(id, recipe);

        this.groups.computeIfAbsent(recipe.getGroup(), k -> new ArrayList<>()).add(id);

        this.invalidateCaches();

        return true;
    }

    @Override
    public void removeRecipe(ResourceLocation id) {
        this.recipes.remove(id);
        this.groups.forEach((k, v) -> v.removeIf(r -> r.equals(id)));

        this.invalidateCaches();
    }

    @Override
    public void setGroupIcon(String group, ItemStack icon) {
        this.groupIcons.put(group, icon);
    }

    @Override
    public ItemStack getGroupIcon(String group) {
        return this.groupIcons.getOrDefault(group, ItemStack.EMPTY);
    }

    @Override
    public String[] getGroups() {
        if( this.cacheGroupNames == null ) {
            this.cacheGroupNames = this.groups.keySet().toArray(new String[0]);
        }

        return this.cacheGroupNames;
    }

    @Override
    public List<IAssemblyRecipe> getRecipes(String groupName) {
        if( this.cacheGroupToRecipes == null ) {
            this.cacheGroupToRecipes = this.groups.entrySet().stream()
                                                  .collect(Collectors.toMap(Map.Entry::getKey,
                                                           e -> e.getValue().stream().map(this::getRecipe).collect(Collectors.toList())));
        }

        return this.cacheGroupToRecipes.get(groupName);
    }

    @Override
    public IAssemblyRecipe getRecipe(ResourceLocation id) {
        return this.recipes.get(id);
    }

    public IAssemblyRecipe findRecipe(ItemStack output) {
        for( IAssemblyRecipe recipe : recipes.values() ) {
            if( ItemStackUtils.areEqual(output, recipe.getRecipeOutput(), false, true, true) ) {
                return recipe;
            }
        }

        return null;
    }

    @Override
    public List<IAssemblyRecipe> getRecipes() {
        if( this.cacheRecipes == null ) {
            this.cacheRecipes = Collections.unmodifiableList(new ArrayList<>(this.recipes.values()));
        }

        return this.cacheRecipes;
    }

    @SuppressWarnings("Convert2MethodRef")
    public void finalizeRegistry() {
        LinkedHashMap<ResourceLocation, IAssemblyRecipe> recipeOrdered = new LinkedHashMap<>(this.recipes);
        this.recipes.clear();
        this.recipes.putAll(recipeOrdered.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, throwingMerger(), () -> new LinkedHashMap<>())));
    }

    /** See {@link java.util.stream.Collectors#throwingMerger()} **/
    @SuppressWarnings("JavadocReference")
    private static <T> BinaryOperator<T> throwingMerger() {
        return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }

    @Nullable
    public List<ItemStack> checkAndConsumeResources(IInventory inv, World world, de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe recipe) {
        if( recipe.matches(inv, world) ) {
            List<ItemStack> removedStacks = new ArrayList<>();
            Map<AssemblyIngredient, Integer> ingredients = recipe.getIngredients().stream().collect(Collectors.toMap(a -> (AssemblyIngredient) a,
                                                                                                                     a -> ((AssemblyIngredient) a).getCount()));
            for( int slot = inv.getSizeInventory() - 1; slot > 3; slot-- ) {
                ItemStack slotStack = inv.getStackInSlot(slot);
                ingredients.entrySet().forEach(e -> {
                    int v = e.getValue();
                    if( v > 0 && e.getKey().apply(slotStack) ) {
                        int extractAmount = Math.min(slotStack.getCount(), v);
                        ItemStack removed = slotStack.copy();
                        removed.setCount(extractAmount);
                        slotStack.shrink(extractAmount);

                        removedStacks.add(removed);
                        e.setValue(v - extractAmount);
                    }
                });
                ingredients.entrySet().removeIf(e -> e.getValue() < 1);
            }

            return removedStacks;
        }

        return null;
    }

    private void invalidateCaches() {
        this.cacheGroupNames = null;
        this.cacheRecipes = null;
        this.cacheGroupToRecipes = null;
    }
}
