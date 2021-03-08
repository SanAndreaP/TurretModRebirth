/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.assembly;

import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyManager;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class AssemblyManager
        implements IAssemblyManager
{
    public static final IRecipeType<IAssemblyRecipe> TYPE = IRecipeType.register(TmrConstants.ID + ":turret_assembly");
    public static final AssemblyManager INSTANCE = new AssemblyManager();

    private final Map<ResourceLocation, IAssemblyRecipe> recipes     = new LinkedHashMap<>();
    private final Map<String, List<ResourceLocation>>    groups      = new HashMap<>();
    private final Map<String, ItemStack>                 groupIcons  = new HashMap<>();
    private final Map<String, Integer>                   groupOrders = new HashMap<>();

    private String[]                           cacheGroupNames;
    private List<IAssemblyRecipe>              cacheRecipes;
    private Map<String, List<IAssemblyRecipe>> cacheGroupToRecipes;

    @Override
    public boolean registerRecipe(@Nonnull IAssemblyRecipe recipe) {
        return registerRecipe(recipe, false);
    }

    public boolean registerRecipe(@Nonnull IAssemblyRecipe recipe, boolean throwException) {
        final Consumer<String> exc = throwException ? s -> { throw new RuntimeException(s); } : s -> TmrConstants.LOG.log(Level.ERROR, s, new InvalidParameterException());

        ResourceLocation id = recipe.getId();
        if( recipe.getFluxPerTick() < 0 ) {
            exc.accept(String.format("Flux usage cannot be smaller than 0 for assembly recipe %s!", id));
            return false;
        }
        if( recipe.getProcessTime() < 0 ) {
            exc.accept(String.format("Processing time cannot be smaller than 0 for assembly recipe %s!", id));
            return false;
        }

        if( recipes.containsKey(id) ) {
            this.removeRecipe(id);
        }

        this.recipes.put(id, recipe);
        this.groups.computeIfAbsent(recipe.getGroup(), k -> new ArrayList<>()).add(id);
        this.groupOrders.putIfAbsent(recipe.getGroup(), 0);

        this.invalidateCaches();

        return true;
    }

    @Override
    public void removeRecipe(ResourceLocation id) {
        IAssemblyRecipe recipe = this.recipes.remove(id);
        String recipeGroup = recipe.getGroup();
        this.groups.get(recipeGroup).removeIf(r -> r.equals(id));

        if( this.groups.get(recipeGroup).size() < 1 ) {
            this.groups.remove(recipeGroup);
        }

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
    public void setGroupOrder(String group, int ordinal) {
        this.cacheGroupNames = null;

        this.groupOrders.put(group, ordinal);
    }

    @Override
    public String[] getGroups() {
        if( this.cacheGroupNames == null ) {
            this.cacheGroupNames = this.groups.keySet().stream().sorted(Comparator.comparingInt(g -> this.groupOrders.getOrDefault(g, 0)))
                                              .toArray(String[]::new);
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

    public void clearRecipes() {
        this.recipes.clear();
        this.groups.clear();

        this.invalidateCaches();
    }

    public void clearRecipesByGroup(String group) {
        new ArrayList<>(this.recipes.keySet()).forEach(id -> {
            if( this.recipes.get(id).getGroup().equals(group) ) {
                this.removeRecipe(id);
            }
        });
    }

    @SuppressWarnings("Convert2MethodRef")
    public void finalizeRegistry() {
        LinkedHashMap<ResourceLocation, IAssemblyRecipe> recipeOrdered = new LinkedHashMap<>(this.recipes);
        this.recipes.clear();
        this.recipes.putAll(recipeOrdered.entrySet().stream().sorted(Map.Entry.comparingByKey())
                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, throwingMerger(), () -> new LinkedHashMap<>())));
    }

    /** See {@link java.util.stream.Collectors#throwingMerger()} **/
    @SuppressWarnings("JavadocReference")
    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }

    @Nullable
    public List<ItemStack> checkAndConsumeResources(IInventory inv, World world, IAssemblyRecipe recipe) {
        if( recipe.canFit(9, 2) && recipe.matches(inv, world) ) {
            List<ItemStack> removedStacks = new ArrayList<>();
            Map<AssemblyIngredient, Integer> ingredients = recipe.getIngredients().stream().collect(Collectors.toMap(a -> (AssemblyIngredient) a,
                                                                                                                     a -> ((AssemblyIngredient) a).getCount()));


            for( int slot = inv.getSizeInventory() - 1; slot > 3; slot-- ) {
                ItemStack slotStack = inv.getStackInSlot(slot);
                ingredients.entrySet().forEach(e -> {
                    int v = e.getValue();
                    AssemblyIngredient ingredient = e.getKey();
                    ItemStack slotStackMatch = slotStack.copy();

                    slotStackMatch.setCount(ingredient.getCount());
                    if( v > 0 && e.getKey().apply(slotStackMatch) ) {
                        int       extractAmount = Math.min(slotStack.getCount(), v);
                        ItemStack removed       = slotStack.copy();
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
