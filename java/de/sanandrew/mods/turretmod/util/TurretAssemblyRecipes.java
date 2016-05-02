/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoArrow;
import de.sanandrew.mods.turretmod.registry.medpack.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.darkhax.bookshelf.lib.javatuples.Pair;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
        INSTANCE.registerRecipe(UUID.fromString("1a011825-2e5b-4f17-925e-f734e6a732b9"), ItemRegistry.ammo.getAmmoItem(4, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.ARROW_UUID)),
                                5, 60,
                                new ItemStack(Items.arrow, 1));
        INSTANCE.registerRecipe(UUID.fromString("c079d29a-e6e2-4be8-8478-326bdfede08b"), ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.QUIVER_UUID)),
                                5, 120,
                                ItemRegistry.ammo.getAmmoItem(16, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.ARROW_UUID)), new ItemStack(Items.leather, 1));
        INSTANCE.registerRecipe(UUID.fromString("47b68be0-30d6-4849-b995-74c147c8cc5d"), new ItemStack(ItemRegistry.tcu, 1), 10, 180,
                                new ItemStack(Items.iron_ingot, 5), new ItemStack(Items.redstone, 2), new ItemStack(Blocks.glass_pane, 1));
        INSTANCE.registerRecipe(UUID.fromString("531f0b05-5bb8-45fc-a899-226a3f52d5b7"),
                                ItemRegistry.repairKit.getRepKitItem(6, RepairKitRegistry.INSTANCE.getRepairKit(RepairKitRegistry.REGEN_MK1)), 25, 600,
                                new ItemStack(Items.leather, 2), new ItemStack(Items.potionitem, 3, 0),
                                new ItemStack(Items.nether_wart, 1), new ItemStack(Items.ghast_tear, 1));
    }

    public boolean checkAndConsumeResources(IInventory inv, UUID uuid) {
        RecipeEntry entry = this.getRecipeEntry(uuid);
        List<Pair<Integer, Integer>> resourceOnSlotList = new ArrayList<>();
        List<ItemStack> resourceStacks = new ArrayList<>(Arrays.asList(entry.resources));

        Iterator<ItemStack> resourceStacksIt = resourceStacks.iterator();
        int invSize = inv.getSizeInventory();
        while( resourceStacksIt.hasNext() ) {
            ItemStack resource = resourceStacksIt.next();
            if( !ItemStackUtils.isValidStack(resource) ) {
                return false;
            }

            for( int i = invSize - 1; i >= 2; i-- ) {
                ItemStack invStack = inv.getStackInSlot(i);
                if( ItemStackUtils.isValidStack(invStack) ) {
                    ItemStack validStack = null;
                    if( (resource.hasTagCompound() && TmrUtils.areStacksEqual(resource, invStack, TmrUtils.NBT_COMPARATOR_FIXD))
                        || (!resource.hasTagCompound() && ItemStackUtils.areStacksEqual(resource, invStack, false)) )
                    {
                        validStack = invStack;
                    }

                    if( validStack != null ) {
                        resourceOnSlotList.add(Pair.with(i, Math.min(validStack.stackSize, resource.stackSize)));
                        resource.stackSize -= validStack.stackSize;
                    }

                    if( resource.stackSize <= 0 ) {
                        resourceStacksIt.remove();
                        break;
                    }
                }
            }
        }

        if( resourceStacks.size() > 0 ) {
            return false;
        }

        for( Pair<Integer, Integer> resourceSlot : resourceOnSlotList ) {
            inv.decrStackSize(resourceSlot.getValue0(), resourceSlot.getValue1());
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
