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
import de.sanandrew.mods.turretmod.api.assembly.IRecipeEntry;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeGroup;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TurretAssemblyRegistry
        implements ITurretAssemblyRegistry
{
    public static final TurretAssemblyRegistry INSTANCE = new TurretAssemblyRegistry();

    private Map<UUID, ItemStack> recipeResults = new HashMap<>();
    private Map<UUID, RecipeEntry> recipeResources = new HashMap<>();
    private List<RecipeKeyEntry> recipeEntries = new ArrayList<>();

    private List<IRecipeGroup> groupsList = new ArrayList<>();

    @Override
    public boolean registerRecipe(UUID uuid, IRecipeGroup group, @Nonnull ItemStack result, int fluxPerTick, int ticksProcessing, IRecipeEntry... resources) {
        if( uuid == null ) {
            TmrConstants.LOG.log(Level.ERROR, "UUID for assembly recipe cannot be null!", new InvalidParameterException());
            return false;
        }
        if( this.recipeResults.containsKey(uuid) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("UUID %s for assembly recipe cannot be registered twice!", uuid), new InvalidParameterException());
            return false;
        }
        if( !ItemStackUtils.isValid(result) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Result stack of UUID %s is not valid!", uuid), new InvalidParameterException());
            return false;
        }
        if( fluxPerTick < 0 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Flux usage cannot be smaller than 0 for UUID %s!", uuid), new InvalidParameterException());
            return false;
        }
        if( ticksProcessing < 0 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ticks processing cannot be smaller than 0 for UUID %s!", uuid), new InvalidParameterException());
            return false;
        }
        if( resources == null ) {
            resources = new IRecipeEntry[0];
        }

        this.recipeResults.put(uuid, result);
        this.recipeResources.put(uuid, new RecipeEntry(resources, fluxPerTick, ticksProcessing));
        this.recipeEntries.add(new RecipeKeyEntry(uuid, result));

        group.addRecipeId(uuid);

        return true;
    }

    @Override
    public IRecipeGroup registerGroup(String name, @Nonnull ItemStack stack) {
        name = BlockRegistry.turret_assembly.getUnlocalizedName() + '.' + name;
        IRecipeGroup group = new RecipeGroup(name, stack);
        this.groupsList.add(group);
        return group;
    }

    @Override
    public IRecipeGroup getGroup(String name) {
        final String fullName = BlockRegistry.turret_assembly.getUnlocalizedName() + '.' + name;
        return this.groupsList.stream().filter(group -> group.getName().equals(fullName)).findFirst().orElse(null);
    }

    public IRecipeGroup[] getGroups() {
        return this.groupsList.toArray(new IRecipeGroup[this.groupsList.size()]);
    }

    public RecipeEntry getRecipeEntry(UUID uuid) {
        return this.recipeResources.get(uuid);
    }

    @Override
    @Nonnull
    public ItemStack getRecipeResult(UUID uuid) {
        ItemStack stack = this.recipeResults.get(uuid);
        return stack.copy();
    }

    @Override
    public List<RecipeKeyEntry> getRecipeList() {
        return new ArrayList<>(this.recipeEntries);
    }

    public void finalizeRegistry() {
        this.recipeEntries.sort((o1, o2) -> {
            int i = Integer.compare(Item.getIdFromItem(o2.stack.getItem()), Item.getIdFromItem(o1.stack.getItem()));
            if( i == 0 ) {
                NonNullList<ItemStack> subtypes = NonNullList.create();
                o1.stack.getItem().getSubItems(CreativeTabs.SEARCH, subtypes);
                return Integer.compare(getStackIndexInList(subtypes, o1.stack), getStackIndexInList(subtypes, o2.stack));
            }
            return i;
        });

        this.groupsList.forEach(group -> group.finalizeGroup(this));
    }

    private static int getStackIndexInList(NonNullList<ItemStack> stacks, ItemStack stack) {
        return stacks.indexOf(stacks.stream().filter(fltStack -> ItemStackUtils.areEqual(stack, fltStack)).findFirst().orElse(null));
    }

    public boolean checkAndConsumeResources(IInventory inv, UUID uuid) {
        RecipeEntry entry = this.getRecipeEntry(uuid);
        if( entry == null ) {
            return false;
        }
        entry = entry.copy();
        List<Tuple> resourceOnSlotList = new ArrayList<>();
        List<IRecipeEntry> resourceStacks = new ArrayList<>(Arrays.asList(entry.resources));

        Iterator<IRecipeEntry> resourceStacksIt = resourceStacks.iterator();
        int invSize = inv.getSizeInventory();
        while( resourceStacksIt.hasNext() ) {
            IRecipeEntry resource = resourceStacksIt.next();
            if( resource == null ) {
                return false;
            }

            for( int i = invSize - 1; i >= 2; i-- ) {
                ItemStack invStack = inv.getStackInSlot(i);
                if( ItemStackUtils.isValid(invStack) ) {
                    ItemStack validStack = null;
                    if( resource.isItemFitting(invStack) )
                    {
                        validStack = invStack;
                    }

                    if( validStack != null ) {
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
            return false;
        }

        for( Tuple resourceSlot : resourceOnSlotList ) {
            inv.decrStackSize(resourceSlot.getValue(0), resourceSlot.getValue(1));
        }

        return true;
    }

    public static class RecipeEntry
    {
        public final IRecipeEntry[] resources;
        public final int fluxPerTick;
        public final int ticksProcessing;

        RecipeEntry(IRecipeEntry[] resources, int fluxPerTick, int ticksProcessing) {
            this.resources = resources;
            this.fluxPerTick = fluxPerTick;
            this.ticksProcessing = ticksProcessing;
        }

        public RecipeEntry copy() {
            List<IRecipeEntry> stacks = new ArrayList<>();
            for( IRecipeEntry stack : this.resources ) {
                stacks.add(stack.copy());
            }
            return new RecipeEntry(stacks.toArray(new IRecipeEntry[stacks.size()]), this.fluxPerTick, this.ticksProcessing);
        }
    }

    public static class RecipeKeyEntry
    {
        public final UUID id;
        @Nonnull
        public final ItemStack stack;

        public RecipeKeyEntry(UUID id, @Nonnull ItemStack stack) {
            this.id = id;
            this.stack = stack;
        }
    }

    public static class RecipeGroup
            implements IRecipeGroup
    {
        private final String name;
        @Nonnull
        private final ItemStack icon;
        private final List<UUID> recipes = new ArrayList<>();

        public RecipeGroup(String name, @Nonnull ItemStack icon) {
            this.name = name;
            this.icon = icon;
        }

        public void addRecipeId(UUID id) {
            this.recipes.add(id);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        @Nonnull
        public ItemStack getIcon() {
            return this.icon;
        }

        @Override
        public List<UUID> getRecipeIdList() {
            return new ArrayList<>(this.recipes);
        }

        @Override
        public void finalizeGroup(ITurretAssemblyRegistry registry) {
            this.recipes.sort((o1, o2) -> {
                ItemStack is1 = registry.getRecipeResult(o1);
                ItemStack is2 = registry.getRecipeResult(o2);
                int i = Integer.compare(Item.getIdFromItem(is1.getItem()), Item.getIdFromItem(is2.getItem()));
                if( i == 0 ) {
                    NonNullList<ItemStack> subtypes = NonNullList.create();
                    is1.getItem().getSubItems(CreativeTabs.SEARCH, subtypes);
                    return Integer.compare(getStackIndexInList(subtypes, is1), getStackIndexInList(subtypes, is2));
                }
                return i;
            });
        }
    }
}
