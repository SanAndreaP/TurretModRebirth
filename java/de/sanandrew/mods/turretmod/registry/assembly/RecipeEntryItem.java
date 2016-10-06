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
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeEntryItem
{
    public int stackSize;
    public ItemStack[] normalAlternatives;
    public String[] oreDictAlternatives;
    public List<ItemEnchEntry> enchAlternatives;
    private boolean drawTooltip;

    private WeakReference<ItemStack[]> cachedEntryStacks;

    public RecipeEntryItem(int count) {
        this.stackSize = count;
        this.drawTooltip = false;
        this.normalAlternatives = new ItemStack[0];
        this.oreDictAlternatives = new String[0];
        this.enchAlternatives = new ArrayList<>();
    }

    private RecipeEntryItem(int count, ItemStack[] normalAlternatives, String[] oreDictAlternatives, List<ItemEnchEntry> enchAlternatives) {
        this.stackSize = count;
        this.drawTooltip = false;
        this.normalAlternatives = normalAlternatives;
        this.oreDictAlternatives = oreDictAlternatives;
        this.enchAlternatives = enchAlternatives;
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
        for( String name : oreDictNames ) {
            if( name.endsWith("*") ) {
                name = name.substring(0, name.lastIndexOf('*'));
                for( String oreName : OreDictionary.getOreNames() ) {
                    if( oreName.startsWith(name) ) {
                        newNames.add(oreName);
                    }
                }
            } else {
                newNames.add(name);
            }
        }

        this.oreDictAlternatives = newNames.toArray(new String[newNames.size()]);

        return this;
    }

    public final RecipeEntryItem put(ItemEnchEntry... enchItems) {
        this.enchAlternatives.addAll(Arrays.asList(enchItems));

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

        return new RecipeEntryItem(this.stackSize, stacksToCopy, this.oreDictAlternatives.clone(), new ArrayList<>(this.enchAlternatives));
    }

    public boolean isItemFitting(ItemStack stack) {
        if( stack == null ) {
            return false;
        }

        for( ItemStack nrmStack : this.normalAlternatives ) {
            if( (nrmStack.hasTagCompound() && TmrUtils.areStacksEqual(nrmStack, stack, TmrUtils.NBT_COMPARATOR_FIXD))
                    || (!nrmStack.hasTagCompound() && ItemStackUtils.areEqual(nrmStack, stack, false)) )
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

        for( ItemEnchEntry enchItem : this.enchAlternatives ) {
            if( stack.isItemEnchanted() && ItemStackUtils.areEqual(enchItem.stack(), stack, false) && EnchantmentHelper.getEnchantmentLevel(enchItem.enchantment(), stack) > 0 ) {
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

            for( ItemEnchEntry enchItem : this.enchAlternatives ) {
                ItemStack newStack = enchItem.stack().copy();
                EnchantmentHelper.setEnchantments(Collections.singletonMap(enchItem.enchantment(), 1), newStack);
                stacks.add(newStack);
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

    public static final class ItemEnchEntry
            extends Tuple
    {
        private static final long serialVersionUID = 2146846913523392590L;

        public ItemEnchEntry(ItemStack stack, Enchantment enchantment) {
            super(stack, enchantment);
        }

        public ItemStack stack() {
            return this.getValue(0);
        }

        public Enchantment enchantment() {
            return this.getValue(1);
        }
    }
}
