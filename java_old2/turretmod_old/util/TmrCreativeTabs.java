/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.ItemUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TmrCreativeTabs
{
    private static final Comparator<ItemStack> ITM_TYPE_COMP = new ItemTypeComparator();
    private static final Comparator<ItemStack> ITM_NAME_COMP = new ItemNameComparator();

    public static final CreativeTabs TURRETS = new CreativeTabs(TurretMod.MOD_ID + ":turrets") {
        private ItemStack[] tabIcons;

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return TmrItems.turretItem;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack() {
            if( this.tabIcons == null ) {
                List<ItemStack> subItms = new ArrayList<>();
                TmrItems.turretItem.getSubItems(TmrItems.turretItem, this, subItms);
                this.tabIcons = subItms.toArray(new ItemStack[subItms.size()]);
            }

            return this.tabIcons[(int) (System.currentTimeMillis() / 4250) % this.tabIcons.length];
        }

        @Override
        @SideOnly(Side.CLIENT)
        @SuppressWarnings("unchecked")
        public void displayAllReleventItems(List itmList) {
            super.displayAllReleventItems(itmList);

            sortItemsByName(itmList);
            sortItemsBySubItems(itmList, this);
        }
    };

    public static final CreativeTabs MISC = new CreativeTabs(TurretMod.MOD_ID + ":misc") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return TmrItems.turretCtrlUnit;
        }

        @Override
        @SideOnly(Side.CLIENT)
        @SuppressWarnings("unchecked")
        public void displayAllReleventItems(List itmList) {
            super.displayAllReleventItems(itmList);

            sortItemsByName(itmList);
            sortItemsBySubItems(itmList, this);
            sortItemsByType(itmList);
        }
    };

    public static final CreativeTabs UPGRADES = new CreativeTabs(TurretMod.MOD_ID + ":upgrades") {
        private ItemStack[] tabIcons;

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return TmrItems.turretUpgrade;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack() {
            if( this.tabIcons == null ) {
                List<ItemStack> subItms = new ArrayList<>();
                TmrItems.turretUpgrade.getSubItems(TmrItems.turretUpgrade, this, subItms);
                this.tabIcons = subItms.toArray(new ItemStack[subItms.size()]);
            }

            return this.tabIcons[(int) (System.currentTimeMillis() / 4250) % this.tabIcons.length];
        }

        @Override
        @SideOnly(Side.CLIENT)
        @SuppressWarnings("unchecked")
        public void displayAllReleventItems(List itmList) {
            super.displayAllReleventItems(itmList);

            sortItemsByName(itmList);
            sortItemsBySubItems(itmList, this);
        }
    };

    protected static void sortItemsByType(List<ItemStack> items) {
        Collections.sort(items, ITM_TYPE_COMP);
    }

    protected static void sortItemsByName(List<ItemStack> items) {
        Collections.sort(items, ITM_NAME_COMP);
    }

    protected static void sortItemsBySubItems(final List<ItemStack> items, final CreativeTabs tab) {
        Collections.sort(items, new ItemSubComparator(tab));
    }

    private static class ItemTypeComparator implements Comparator<ItemStack>
    {
        @Override
        public int compare(ItemStack stack1, ItemStack stack2) {
            return ItemBlock.class.isAssignableFrom(stack1.getItem().getClass()) && !ItemBlock.class.isAssignableFrom(stack2.getItem().getClass()) ? -1 : 1;
        }
    }

    private static class ItemNameComparator implements Comparator<ItemStack>
    {
        @Override
        public int compare(ItemStack stack1, ItemStack stack2) {
            return stack2.getUnlocalizedName().compareTo(stack1.getUnlocalizedName());
        }
    }

    private static class ItemSubComparator implements Comparator<ItemStack>
    {
        private final CreativeTabs tab;

        private ItemSubComparator(CreativeTabs thisTab) {
            this.tab = thisTab;
        }

        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            if( o1.getItem() != o2.getItem() ) {
                return -1;
            }

            List<ItemStack> subItms = new ArrayList<>();
            o1.getItem().getSubItems(o1.getItem(), this.tab, subItms);

            return getStackIndexInList(o2, subItms) > getStackIndexInList(o1, subItms) ? -1 : 1;
        }

        private static int getStackIndexInList(ItemStack stack, List<ItemStack> stackArray) {
            for( ItemStack stackElem : stackArray ) {
                if( ItemUtils.areStacksEqual(stack, stackElem, true) ) {
                    return stackArray.indexOf(stackElem);
                }
            }

            return -1;
        }
    }
}
