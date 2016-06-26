/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.item.ItemTurret;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TmrCreativeTabs
{
    private static final Comparator<ItemStack> ITM_NAME_COMP = new ItemNameComparator();

    public static final CreativeTabs TURRETS = new CreativeTabs(TurretModRebirth.ID + ":turrets") {
        private ItemStack[] tabIcons;

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Items.BLAZE_POWDER;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack() {
            if( this.tabIcons == null ) {
                List<ItemStack> subItms = new ArrayList<>();
                ItemRegistry.turret.getSubItems(ItemRegistry.turret, this, subItms);
                this.tabIcons = subItms.toArray(new ItemStack[subItms.size()]);
            }

            return this.tabIcons[(int) (System.currentTimeMillis() / 4250) % this.tabIcons.length];
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(List<ItemStack> itmList) {
            super.displayAllRelevantItems(itmList);

            Collections.sort(itmList, (itm1, itm2) -> {
                if( itm1 != null && itm1.getItem() == ItemRegistry.turret ) {
                    return itm2 != null && itm2.getItem() == ItemRegistry.turret ? 0 : -2;
                } else if( itm2 != null && itm2.getItem() == ItemRegistry.turret ) {
                    return 2;
                } else if( itm1 != null && itm1.getItem() == ItemRegistry.ammo ) {
                    return itm2 != null && itm2.getItem() == ItemRegistry.ammo ? 0 : -1;
                } else if( itm2 != null && itm2.getItem() == ItemRegistry.ammo ) {
                    return 1;
                }

                return 0;
            });
        }
    };

    public static final CreativeTabs MISC = new CreativeTabs(TurretModRebirth.ID + ":misc") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ItemRegistry.tcu;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(List<ItemStack> itmList) {
            super.displayAllRelevantItems(itmList);

            Collections.sort(itmList, (itm1, itm2) -> {
                if( itm1 != null && itm1.getItem() instanceof ItemBlock ) {
                    return itm2 != null && itm2.getItem() instanceof ItemBlock ? 0 : -2;
                } else if( itm2 != null && itm2.getItem() instanceof ItemBlock ) {
                    return 2;
                } else if( itm1 != null && itm1.getItem() == ItemRegistry.repairKit ) {
                    return itm2 != null && itm2.getItem() == ItemRegistry.repairKit ? 0 : 1;
                } else if( itm2 != null && itm2.getItem() == ItemRegistry.repairKit ) {
                    return -1;
                }

                return 0;
            });
        }
    };

    public static final CreativeTabs UPGRADES = new CreativeTabs(TurretModRebirth.ID + ":upgrades") {
        private ItemStack[] tabIcons;

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ItemRegistry.turretUpgrade;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack() {
            if( this.tabIcons == null ) {
                List<ItemStack> subItms = new ArrayList<>();
                ItemRegistry.turretUpgrade.getSubItems(ItemRegistry.turretUpgrade, this, subItms);
                this.tabIcons = subItms.toArray(new ItemStack[subItms.size()]);
            }

            return this.tabIcons[(int) (System.currentTimeMillis() / 4250) % this.tabIcons.length];
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(List<ItemStack> itmList) {
            super.displayAllRelevantItems(itmList);

            sortItemsByName(itmList);
            sortItemsBySubItems(itmList, this);
        }
    };

    protected static void sortItemsByName(List<ItemStack> items) {
        Collections.sort(items, ITM_NAME_COMP);
    }

    protected static void sortItemsBySubItems(final List<ItemStack> items, final CreativeTabs tab) {
        Collections.sort(items, new ItemSubComparator(tab));
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
                if( ItemStackUtils.areStacksEqual(stack, stackElem, true) ) {
                    return stackArray.indexOf(stackElem);
                }
            }

            return -1;
        }
    }
}
