/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.assembly;

import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeEntryItem
{
    public int stackSize;
    public ItemStack[] normalAlternatives;
    public String[] oreDictAlternatives;
    private boolean drawTooltip;

    private WeakReference<ItemStack[]> cachedEntryStacks;

    public RecipeEntryItem(int count) {
        this.stackSize = count;
        this.drawTooltip = false;
        this.normalAlternatives = new ItemStack[0];
        this.oreDictAlternatives = new String[0];
    }

    private RecipeEntryItem(int count, ItemStack[] normalAlternatives, String[] oreDictAlternatives) {
        this.stackSize = count;
        this.drawTooltip = false;
        this.normalAlternatives = normalAlternatives;
        this.oreDictAlternatives = oreDictAlternatives;
    }

    public RecipeEntryItem put(Item... items) {
        return this.put(false, items);
    }

    public RecipeEntryItem put(boolean allDmg, Item... items) {
        List<ItemStack> newStacks = new ArrayList<>();
        newStacks.addAll(Arrays.asList(this.normalAlternatives));
        for( Item item : items ) {
            newStacks.add(new ItemStack(item, 1, allDmg ? OreDictionary.WILDCARD_VALUE : 0));
        }

        this.normalAlternatives = newStacks.toArray(new ItemStack[newStacks.size()]);

        return this;
    }

    public RecipeEntryItem put(Block... blocks) {
        return this.put(false, blocks);
    }

    public RecipeEntryItem put(boolean allDmg, Block... blocks) {
        List<ItemStack> newStacks = new ArrayList<>();
        newStacks.addAll(Arrays.asList(this.normalAlternatives));
        for( Block block : blocks ) {
            newStacks.add(new ItemStack(block, 1, allDmg ? OreDictionary.WILDCARD_VALUE : 0));
        }

        this.normalAlternatives = newStacks.toArray(new ItemStack[newStacks.size()]);

        return this;
    }

    public RecipeEntryItem put(ItemStack... stacks) {
        List<ItemStack> newStacks = new ArrayList<>();
        newStacks.addAll(Arrays.asList(this.normalAlternatives));
        newStacks.addAll(Arrays.asList(stacks));

        this.normalAlternatives = newStacks.toArray(new ItemStack[newStacks.size()]);

        return this;
    }

    public RecipeEntryItem put(String... oreDictNames) {
        List<String> newNames = new ArrayList<>();
        newNames.addAll(Arrays.asList(this.oreDictAlternatives));
        newNames.addAll(Arrays.asList(oreDictNames));

        this.oreDictAlternatives = newNames.toArray(new String[newNames.size()]);

        return this;
    }

    public RecipeEntryItem drawTooltip() {
        this.drawTooltip = true;
        return this;
    }

    public boolean shouldDrawTooltip() {
        return this.drawTooltip;
    }

    public RecipeEntryItem copy() {
        ItemStack[] stacksToCopy = new ItemStack[this.normalAlternatives.length];
        for( int i = 0; i < stacksToCopy.length; i++ ) {
            stacksToCopy[i] = this.normalAlternatives[i].copy();
        }

        return new RecipeEntryItem(this.stackSize, stacksToCopy, this.oreDictAlternatives.clone());
    }

    public boolean isItemFitting(ItemStack stack) {
        for( ItemStack nrmStack : this.normalAlternatives ) {
            if( (nrmStack.hasTagCompound() && TmrUtils.areStacksEqual(nrmStack, stack, TmrUtils.NBT_COMPARATOR_FIXD))
                    || (!nrmStack.hasTagCompound() && ItemStackUtils.areStacksEqual(nrmStack, stack, false)) )
            {
                return true;
            }
        }

        int[] stackOreIds = OreDictionary.getOreIDs(stack);
        for( int oreId : stackOreIds ) {
            String oreIdName = OreDictionary.getOreName(oreId);
            if( ArrayUtils.contains(this.oreDictAlternatives, oreIdName) ) {
                return true;
            }
        }

        return false;
    }

    public ItemStack[] getEntryItemStacks() {
        if( this.cachedEntryStacks == null || this.cachedEntryStacks.get() == null ) {
            List<ItemStack> stacks = new ArrayList<>();
            List<ItemStack> fltStacks = new ArrayList<>();
            for( ItemStack stack : this.normalAlternatives ) {
                if( stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ) {
                    stack.getItem().getSubItems(stack.getItem(), CreativeTabs.SEARCH, stacks);
                } else {
                    stacks.add(stack);
                }
            }

            for( String oreDictName : this.oreDictAlternatives ) {
                for( ItemStack stack : OreDictionary.getOres(oreDictName) ) {
                    if( stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ) {
                        stack.getItem().getSubItems(stack.getItem(), CreativeTabs.SEARCH, stacks);
                    } else {
                        stacks.add(stack);
                    }
                }
            }

            for( ItemStack stack : stacks ) {
                ItemStack valid = stack.copy();
                for( ItemStack fltStack : fltStacks ) {
                    if( fltStack.isItemEqual(valid) ) {
                        valid = null;
                        break;
                    }
                }

                if( valid != null ) {
                    valid.stackSize = this.stackSize;
                    fltStacks.add(valid);
                }
            }

            this.cachedEntryStacks = new WeakReference<>(fltStacks.toArray(new ItemStack[fltStacks.size()]));
        }

        return this.cachedEntryStacks.get();
    }
}