/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import codechicken.lib.inventory.InventoryUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.darkhax.bookshelf.lib.javatuples.Pair;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TurretAssemblyRecipes
{
    public static final TurretAssemblyRecipes INSTANCE = new TurretAssemblyRecipes();

    private Map<UUID, ItemStack> recipeResults = new HashMap<>();
    private Map<UUID, RecipeEntry> recipeResources = new HashMap<>();

    public boolean registerRecipe(UUID uuid, ItemStack result, int fluxPerTick, int ticksProcessing, ItemStack... resources) {
        if( uuid == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, "UUID for assembly recipe cannot be null!", new InvalidParameterException());
            return false;
        }
        if( this.recipeResults.containsKey(uuid) ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("UUID %s for assembly recipe cannot be registered twice!", uuid), new InvalidParameterException());
            return false;
        }
        if( !ItemStackUtils.isValidStack(result) ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Result stack of UUID %s is not valid!", uuid), new InvalidParameterException());
            return false;
        }
        if( fluxPerTick < 0 ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Flux usage cannot be smaller than 0 for UUID %s!", uuid), new InvalidParameterException());
            return false;
        }
        if( ticksProcessing < 0 ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Ticks processing cannot be smaller than 0 for UUID %s!", uuid), new InvalidParameterException());
            return false;
        }
        if( resources == null || resources.length < 1 ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Resources cannot be free for UUID %s! It needs at least 1 resource.", uuid), new InvalidParameterException());
            return false;
        }

        this.recipeResults.put(uuid, result);
        this.recipeResources.put(uuid, new RecipeEntry(resources, fluxPerTick, ticksProcessing));

        return true;
    }

    public RecipeEntry getRecipeEntry(UUID uuid) {
        RecipeEntry entry = this.recipeResources.get(uuid);
        return entry == null ? null : entry.copy();
    }

    public ItemStack getRecipeResult(UUID uuid) {
        ItemStack stack = this.recipeResults.get(uuid);
        return stack == null ? null : stack.copy();
    }

    public List<Pair<UUID, ItemStack>> getRecipeList() {
        List<Pair<UUID, ItemStack>> ret = new ArrayList<>(this.recipeResults.size());
        for( UUID key : this.recipeResults.keySet() ) {
            ret.add(Pair.with(key, this.getRecipeResult(key)));
        }

        return ret;
    }

    public static void initialize() {
        INSTANCE.registerRecipe(UUID.fromString("21f88959-c157-44e3-815b-dd956b065052"), ItemRegistry.turret.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(EntityTurretCrossbow.class)),
                                10, 100,
                                new ItemStack(Blocks.cobblestone, 12), new ItemStack(Items.bow, 1), new ItemStack(Items.redstone, 4), new ItemStack(Blocks.planks, 4, OreDictionary.WILDCARD_VALUE));
    }

    public boolean checkAndConsumeResources(IInventory inv, UUID uuid) {
        RecipeEntry entry = this.getRecipeEntry(uuid);

        List<Pair<Integer, ItemStack>> resourceOnSlotList = new ArrayList<>();

        for( ItemStack resource : entry.resources ) {
            if( !ItemStackUtils.isValidStack(resource) ) {
                return false;
            }

            Pair<Integer, ItemStack> similarStackInv = TmrUtils.getSimilarStackFromInventory(resource, inv, resource.hasTagCompound() ? TmrUtils.NBT_COMPARATOR_FIXD : null);
            if( similarStackInv == null || similarStackInv.getValue1().stackSize < resource.stackSize ) {
                return false;
            }

            resourceOnSlotList.add(Pair.with(similarStackInv.getValue0(), resource));
        }

        for( Pair<Integer, ItemStack> resourceSlot : resourceOnSlotList ) {
            inv.decrStackSize(resourceSlot.getValue0(), resourceSlot.getValue1().stackSize);
        }

        return true;
    }

    public static class RecipeEntry {
        public final ItemStack[] resources;
        public final int fluxPerTick;
        public final int ticksProcessing;

        RecipeEntry(ItemStack[] resources, int fluxPerTick, int ticksProcessing) {
            this.resources = resources;
            this.fluxPerTick = fluxPerTick;
            this.ticksProcessing = ticksProcessing;
        }

        public RecipeEntry copy() {
            List<ItemStack> stacks = new ArrayList<>();
            for( ItemStack stack : this.resources ) {
                stacks.add(stack.copy());
            }
            return new RecipeEntry(stacks.toArray(new ItemStack[stacks.size()]), this.fluxPerTick, this.ticksProcessing);
        }
    }
}
