/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.assembly;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeGroup;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeItem;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TurretAssemblyRegistry
        implements ITurretAssemblyRegistry
{
    public static final TurretAssemblyRegistry INSTANCE = new TurretAssemblyRegistry();

    private final Map<ResourceLocation, RecipeEntry> recipes = new LinkedHashMap<>();
    private final Map<ResourceLocation, RecipeEntry> uRecipes = Collections.unmodifiableMap(this.recipes);
    private final List<IRecipeGroup> groupsList = new ArrayList<>();

    @Override
    public boolean registerRecipe(ResourceLocation id, IRecipeGroup group, @Nonnull ItemStack result, int fluxPerTick, int ticksProcessing, IRecipeItem... resources) {
        if( id == null ) {
            TmrConstants.LOG.log(Level.ERROR, "ID for assembly recipe cannot be null!", new InvalidParameterException());
            return false;
        }
        if( !ItemStackUtils.isValid(result) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Result stack of assembly recipe %s is not valid!", id), new InvalidParameterException());
            return false;
        }
        if( fluxPerTick < 0 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Flux usage cannot be smaller than 0 for assembly recipe %s!", id), new InvalidParameterException());
            return false;
        }
        if( ticksProcessing < 0 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ticks processing cannot be smaller than 0 for assembly recipe %s!", id), new InvalidParameterException());
            return false;
        }
        if( resources == null ) {
            resources = new IRecipeItem[0];
        }

        this.recipes.put(id, new RecipeEntry(resources, fluxPerTick, ticksProcessing, result));

        group.addRecipeId(id);

        return true;
    }

    @Override
    public IRecipeGroup registerGroup(String name, @Nonnull ItemStack stack) {
        name = BlockRegistry.TURRET_ASSEMBLY.getUnlocalizedName() + '.' + name;
        IRecipeGroup group = new RecipeGroup(name, stack);
        this.groupsList.add(group);
        return group;
    }

    @Override
    public IRecipeGroup getGroup(String name) {
        final String fullName = BlockRegistry.TURRET_ASSEMBLY.getUnlocalizedName() + '.' + name;
        return this.groupsList.stream().filter(group -> group.getName().equals(fullName)).findFirst().orElse(null);
    }

    public IRecipeGroup[] getGroups() {
        return this.groupsList.toArray(new IRecipeGroup[0]);
    }

    public RecipeEntry getRecipeEntry(ResourceLocation id) {
        return this.recipes.get(id);
    }

    public RecipeEntry getRecipeEntry(ItemStack result) {
        for( Map.Entry<ResourceLocation, RecipeEntry> entry : this.recipes.entrySet() ) {
            if( ItemStackUtils.areEqual(result, entry.getValue().result) ) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeResult(ResourceLocation id) {
        ItemStack stack = this.recipes.get(id).result;
        return stack.copy();
    }

    @Override
    public Map<ResourceLocation, RecipeEntry> getRecipeList() {
        return this.uRecipes;
    }

    public void finalizeRegistry() {
        LinkedHashMap<ResourceLocation, RecipeEntry> recipeOrdered = new LinkedHashMap<>(this.recipes);
        this.recipes.clear();
        recipeOrdered.entrySet().stream().sorted((o1, o2) -> {
            RecipeEntry r1 = o1.getValue();
            RecipeEntry r2 = o2.getValue();
            int i = Integer.compare(Item.getIdFromItem(r1.result.getItem()), Item.getIdFromItem(r2.result.getItem()));
            if( i == 0 ) {
                NonNullList<ItemStack> subtypes = NonNullList.create();
                r1.result.getItem().getSubItems(CreativeTabs.SEARCH, subtypes);
                return Integer.compare(getStackIndexInList(subtypes, r1.result), getStackIndexInList(subtypes, r2.result));
            }
            return i;
        }).forEach(e -> this.recipes.put(e.getKey(), e.getValue()));

        this.groupsList.forEach(group -> group.finalizeGroup(this));
    }

    static int getStackIndexInList(NonNullList<ItemStack> stacks, ItemStack stack) {
        return stacks.indexOf(stacks.stream().filter(fltStack -> ItemStackUtils.areEqual(stack, fltStack)).findFirst().orElse(null));
    }

    @Nullable
    public List<ItemStack> checkAndConsumeResources(IInventory inv, ResourceLocation id) {
        RecipeEntry entry = this.getRecipeEntry(id);
        if( entry == null ) {
            return null;
        }
        entry = entry.copy();
        List<Tuple> resourceOnSlotList = new ArrayList<>();
        List<IRecipeItem> resourceStacks = new ArrayList<>(Arrays.asList(entry.resources));

        Iterator<IRecipeItem> resourceStacksIt = resourceStacks.iterator();
        int invSize = inv.getSizeInventory();
        while( resourceStacksIt.hasNext() ) {
            IRecipeItem resource = resourceStacksIt.next();
            if( resource == null ) {
                return null;
            }

            for( int i = invSize - 1; i >= 2; i-- ) {
                ItemStack invStack = inv.getStackInSlot(i);
                if( ItemStackUtils.isValid(invStack) ) {
                    @Nonnull
                    ItemStack validStack = ItemStackUtils.getEmpty();
                    if( resource.isItemFitting(invStack) )
                    {
                        validStack = invStack;
                    }

                    if( ItemStackUtils.isValid(validStack) ) {
                        resourceOnSlotList.add(new Tuple(i, Math.min(validStack.getCount(), resource.getItemCount())));
                        resource.decreaseItemCount(validStack.getCount());
                    }

                    if( resource.getItemCount() <= 0 ) {
                        resourceStacksIt.remove();
                        break;
                    }
                }
            }
        }

        if( resourceStacks.size() > 0 ) {
            return null;
        }

        List<ItemStack> removedItems = new ArrayList<>();
        for( Tuple resourceSlot : resourceOnSlotList ) {
            removedItems.add(inv.getStackInSlot(resourceSlot.getValue(0)).splitStack(resourceSlot.getValue(1)));
        }

        return removedItems;
    }
}
