/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity.assembly;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.ILeveledInventory;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyManager;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AssemblyManager
        implements IAssemblyManager
{
    public static final IRecipeType<IAssemblyRecipe> TYPE = IRecipeType.register(TmrConstants.ID + ":turret_assembly");
    public static final AssemblyManager INSTANCE = new AssemblyManager();

//    private final Map<ResourceLocation, IAssemblyRecipe> recipes     = new LinkedHashMap<>();
//    private final Map<String, List<ResourceLocation>>    groups      = new HashMap<>();
    private final Map<String, ItemStack>                 groupIcons  = new HashMap<>();
    private final Map<String, Integer>                   groupOrders = new HashMap<>();
//
//    private String[]                           cacheGroupNames;
//    private List<IAssemblyRecipe>              cacheRecipes;
//    private Map<String, List<IAssemblyRecipe>> cacheGroupToRecipes;

//    @Override
//    public boolean registerRecipe(@Nonnull IAssemblyRecipe recipe) {
//        return registerRecipe(recipe, false);
//    }

//    public boolean registerRecipe(@Nonnull IAssemblyRecipe recipe, boolean throwException) {
//        final Consumer<String> exc = throwException ? s -> { throw new RuntimeException(s); } : s -> TmrConstants.LOG.log(Level.ERROR, s, new InvalidParameterException());
//
//        ResourceLocation id = recipe.getId();
//        if( recipe.getEnergyConsumption() < 0 ) {
//            exc.accept(String.format("Flux usage cannot be smaller than 0 for assembly recipe %s!", id));
//            return false;
//        }
//        if( recipe.getProcessTime() < 0 ) {
//            exc.accept(String.format("Processing time cannot be smaller than 0 for assembly recipe %s!", id));
//            return false;
//        }
//
////        if( recipes.containsKey(id) ) {
////            this.removeRecipe(id);
////        }
//
//        this.recipes.put(id, recipe);
//        this.groups.computeIfAbsent(recipe.getGroup(), k -> new ArrayList<>()).add(id);
//        this.groupOrders.putIfAbsent(recipe.getGroup(), 0);
//
//        this.invalidateCaches();
//
//        return true;
//    }

//    @Override
//    public void removeRecipe(ResourceLocation id) {
//        IAssemblyRecipe recipe = this.recipes.remove(id);
//        String recipeGroup = recipe.getGroup();
//        this.groups.get(recipeGroup).removeIf(r -> r.equals(id));
//
//        if( this.groups.get(recipeGroup).size() < 1 ) {
//            this.groups.remove(recipeGroup);
//        }
//
//        this.invalidateCaches();
//    }

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
        this.groupOrders.put(group, ordinal);
    }

    @Override
    public List<IAssemblyRecipe> getRecipes(World level) {
        return level.getRecipeManager().getAllRecipesFor(TYPE);
    }

    @Override
    public String[] getGroups(World level) {
        return this.getRecipes(level).stream().map(IAssemblyRecipe::getGroup).distinct()
                                     .filter(g -> !Strings.isNullOrEmpty(g))
                                     .sorted(Comparator.comparingInt(g -> this.groupOrders.getOrDefault(g, 0)))
                                     .toArray(String[]::new);
    }

    @Override
    public List<IAssemblyRecipe> getRecipes(World level, String groupName) {
        return this.getRecipes(level).stream().filter(r -> r.getGroup().equals(groupName)).collect(Collectors.toList());
    }

    @Override
    public IAssemblyRecipe getRecipe(World level, ResourceLocation id) {
        return this.getRecipes(level).stream().filter(r -> r.getId().equals(id)).findFirst().orElse(EmptyRecipe.INSTANCE);
    }

//    public IAssemblyRecipe findRecipe(ItemStack output) {
//        for( IAssemblyRecipe recipe : this.getRecipes() ) {
//            if( ItemStackUtils.areEqual(output, recipe.getResultItem(), false, true) ) {
//                return recipe;
//            }
//        }
//
//        return null;
//    }

    private static final class EmptyRecipe
            extends AssemblyRecipe
    {
        static final EmptyRecipe INSTANCE = new EmptyRecipe();

        private EmptyRecipe() {
            super(new ResourceLocation(TmrConstants.ID, "null"), "", NonNullList.create(), 0, Integer.MAX_VALUE, ItemStack.EMPTY);
        }

        @Nonnull @Override public String getGroup() { return ""; }
    }

    @Nullable
    public List<ItemStack> checkAndConsumeResources(ILeveledInventory inv, World world, IAssemblyRecipe recipe, int[] inputSlotIds) {
        if( recipe.canCraftInDimensions(9, 2) && recipe.matches(inv, world) ) {
            List<ItemStack> removedStacks = new ArrayList<>();

            for( Ingredient ing : recipe.getIngredients() ) {
                boolean isSatisfied = false;

                for( ItemStack ingStack : ing.getItems() ) {
                    Map<Integer, Integer> modifiedSlots    = new HashMap<>();
                    List<ItemStack>       removedStacksIng = new ArrayList<>();
                    int                   totalAmt         = ingStack.getCount();

                    for( int i = inputSlotIds.length - 1; i >= 0; i-- ) {
                        ItemStack slotStack = inv.getItem(inputSlotIds[i]);
                        if( ItemStackUtils.areEqualNbtFit(slotStack, ingStack, false, false) ) {
                            ItemStack removedStack = slotStack.copy();
                            int       removedAmt   = Math.min(totalAmt, slotStack.getCount());

                            totalAmt -= removedAmt;
                            removedStack.setCount(removedAmt);

                            modifiedSlots.put(inputSlotIds[i], removedAmt);
                            removedStacksIng.add(removedStack);
                        }

                        if( totalAmt == 0 ) {
                            break;
                        }
                    }
                    if( totalAmt == 0 ) {
                        modifiedSlots.forEach((slotId, reduceAmt) -> inv.getItem(slotId).shrink(reduceAmt));
                        removedStacks.addAll(removedStacksIng);
                        isSatisfied = true;
                        break;
                    }
                }

                if( !isSatisfied ) {
                    return null;
                }
            }

            return removedStacks;
        }

        return null;
    }
}
