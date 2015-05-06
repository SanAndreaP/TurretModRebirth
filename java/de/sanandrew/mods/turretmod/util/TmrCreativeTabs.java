/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.core.manpack.util.helpers.ItemUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TmrCreativeTabs
{
    public static final List<Object> REG_ORDER_ENTRIES = new ArrayList<>();

    public static final CreativeTabs TURRETS = new CreativeTabs("turrets") {
        @Override public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.dispenser);
        }

        @Override
        public void displayAllReleventItems(List p_78018_1_) {
            super.displayAllReleventItems(p_78018_1_);
            sortItemsByName(p_78018_1_);
            sortItemsByType(p_78018_1_);
            sortItemsBySubItems(p_78018_1_, this);
        }
    };

    protected static void sortItemsByType(List<ItemStack> items) {
        Comparator<ItemStack> comp = new Comparator<ItemStack>() {
            @Override
            public int compare(ItemStack o1, ItemStack o2) {
                return ItemBlock.class.isAssignableFrom(o1.getItem().getClass()) ? -1 : 1;
            }
        };

        Collections.sort(items, comp);
    }

    protected static void sortItemsByName(List<ItemStack> items) {
        Comparator<ItemStack> comp = new Comparator<ItemStack>() {
            @Override
            public int compare(ItemStack o1, ItemStack o2) {
                String fstName = o1.getUnlocalizedName();
                String sndName = o2.getUnlocalizedName();

                return sndName.compareTo(fstName);
            }
        };

        Collections.sort(items, comp);
    }

    protected static void sortItemsBySubItems(final List<ItemStack> items, final CreativeTabs tab) {
        Comparator<ItemStack> comp = new Comparator<ItemStack>() {
            @Override
            public int compare(ItemStack o1, ItemStack o2) {
                if( o1.getItem() != o2.getItem() ) {
                    return -1;
                }

                List<ItemStack> subItms = new ArrayList<>();
                o1.getItem().getSubItems(o1.getItem(), tab, subItms);

                return getStackIndexInList(o2, o2.hasTagCompound(), subItms) > getStackIndexInList(o1, o1.hasTagCompound(), subItms) ? 1 : -1;
            }
        };

        Collections.sort(items, comp);
    }

    public static int getStackIndexInList(ItemStack stack, boolean checkNbt, List<ItemStack> stackArray) {
        for( ItemStack stackElem : stackArray ) {
            if( ItemUtils.areStacksEqual(stack, stackElem, checkNbt) ) {
                return stackArray.indexOf(stackElem);
            }
        }

        return -1;
    }
}
