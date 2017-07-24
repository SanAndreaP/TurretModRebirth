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
import de.sanandrew.mods.turretmod.api.assembly.IRecipeEntry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeEntryItem
        implements IRecipeEntry
{
    public int stackSize;
    public ItemStack[] normalAlternatives;
    public String[] oreDictAlternatives;
    public List<IEnchantmentEntry> enchAlternatives;
    private boolean drawTooltip;

    private WeakReference<ItemStack[]> cachedEntryStacks;

    public RecipeEntryItem(int count) {
        this.stackSize = count;
        this.drawTooltip = false;
        this.normalAlternatives = new ItemStack[0];
        this.oreDictAlternatives = new String[0];
        this.enchAlternatives = new ArrayList<>();
    }

    private RecipeEntryItem(int count, ItemStack[] normalAlternatives, String[] oreDictAlternatives, List<IEnchantmentEntry> enchAlternatives) {
        this.stackSize = count;
        this.drawTooltip = false;
        this.normalAlternatives = normalAlternatives;
        this.oreDictAlternatives = oreDictAlternatives;
        this.enchAlternatives = enchAlternatives;
    }

    @Override
    public RecipeEntryItem put(Item... items) {
        List<ItemStack> newStacks = new ArrayList<>();
        newStacks.addAll(Arrays.asList(this.normalAlternatives));
        for( Item item : items ) {
            newStacks.add(new ItemStack(item, 1));
        }

        this.normalAlternatives = newStacks.toArray(new ItemStack[newStacks.size()]);

        return this;
    }

    @Override
    public RecipeEntryItem put(Block... blocks) {
        List<ItemStack> newStacks = new ArrayList<>();
        newStacks.addAll(Arrays.asList(this.normalAlternatives));
        for( Block block : blocks ) {
            newStacks.add(new ItemStack(block, 1));
        }

        this.normalAlternatives = newStacks.toArray(new ItemStack[newStacks.size()]);

        return this;
    }

    @Override
    public RecipeEntryItem put(ItemStack... stacks) {
        List<ItemStack> newStacks = new ArrayList<>();
        newStacks.addAll(Arrays.asList(this.normalAlternatives));
        newStacks.addAll(Arrays.asList(stacks));

        this.normalAlternatives = newStacks.toArray(new ItemStack[newStacks.size()]);

        return this;
    }

    @Override
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

    @Override
    public final RecipeEntryItem put(IEnchantmentEntry... enchItems) {
        this.enchAlternatives.addAll(Arrays.asList(enchItems));

        return this;
    }

    @Override
    public RecipeEntryItem drawTooltip() {
        this.drawTooltip = true;
        return this;
    }

    @Override
    public boolean shouldDrawTooltip() {
        return this.drawTooltip;
    }

    @Override
    public RecipeEntryItem copy() {
        ItemStack[] stacksToCopy = new ItemStack[this.normalAlternatives.length];
        for( int i = 0; i < stacksToCopy.length; i++ ) {
            stacksToCopy[i] = this.normalAlternatives[i].copy();
        }

        return new RecipeEntryItem(this.stackSize, stacksToCopy, this.oreDictAlternatives.clone(), new ArrayList<>(this.enchAlternatives));
    }

    @Override
    public boolean isItemFitting(@Nonnull ItemStack stack) {
        if( !ItemStackUtils.isValid(stack) ) {
            return false;
        }

        for( ItemStack nrmStack : this.normalAlternatives ) {
            if( ItemStackUtils.areEqual(nrmStack, stack) ) {
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

        for( IEnchantmentEntry enchItem : this.enchAlternatives ) {
            if( stack.isItemEnchanted() && ItemStackUtils.areEqual(enchItem.stack(), stack, false) && EnchantmentHelper.getEnchantmentLevel(enchItem.enchantment(), stack) > 0 ) {
                return true;
            }
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack[] getEntryItemStacks() {
        if( this.cachedEntryStacks == null || this.cachedEntryStacks.get() == null ) {
            NonNullList<ItemStack> stacks = NonNullList.create();
            NonNullList<ItemStack> fltStacks = NonNullList.create();
            for( ItemStack stack : this.normalAlternatives ) {
                if( stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ) {
                    stack.getItem().getSubItems(CreativeTabs.SEARCH, stacks);
                } else {
                    stacks.add(stack);
                }
            }

            for( String oreDictName : this.oreDictAlternatives ) {
                OreDictionary.getOres(oreDictName).forEach(stack -> {
                    if( stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ) {
                        stack.getItem().getSubItems(CreativeTabs.SEARCH, stacks);
                    }else {
                        stacks.add(stack);
                    }
                });
            }

            for( IEnchantmentEntry enchItem : this.enchAlternatives ) {
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
                    valid.setCount(this.stackSize);
                    fltStacks.add(valid);
                }
            }

            this.cachedEntryStacks = new WeakReference<>(fltStacks.toArray(new ItemStack[fltStacks.size()]));
        }

        return this.cachedEntryStacks.get();
    }

    @Override
    public int getItemCount() {
        return this.stackSize;
    }

    @Override
    public void decreaseItemCount(int amount) {
        assert amount <= 0 : "Amount must be greater than 0!";
        assert this.stackSize - amount < 0 : "Item count cannot become less than 0!";
        this.stackSize -= amount;
    }

    public static final class EnchantmentEntry
            extends Tuple
            implements IEnchantmentEntry
    {
        private static final long serialVersionUID = 2146846913523392590L;

        public EnchantmentEntry(@Nonnull ItemStack stack, Enchantment enchantment) {
            super(stack, enchantment);
        }

        @Nonnull
        public ItemStack stack() {
            return this.getValue(0);
        }

        public Enchantment enchantment() {
            return this.getValue(1);
        }
    }
}
