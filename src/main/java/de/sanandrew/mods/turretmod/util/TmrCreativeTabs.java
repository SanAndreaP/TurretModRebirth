/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemRepairKit;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;

public class TmrCreativeTabs
{
    private static final Comparator<ItemStack> ITM_NAME_COMP = new ItemNameComparator();

    public static final CreativeTabs TURRETS = new CreativeTabs(TmrConstants.ID + ":turrets") {
        private NonNullList<ItemStack> tabIcons = null;

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return getIconItemStack();
        }

        @Override
        @SideOnly(Side.CLIENT)
        @Nonnull
        public ItemStack getIconItemStack() {
            if( this.tabIcons == null ) {
                this.tabIcons = NonNullList.create();
                ItemRegistry.TURRET_PLACERS.forEach((rl, item) -> item.getSubItems(this, this.tabIcons));
            }

            return this.tabIcons.get((int) (System.currentTimeMillis() / 4250L) % this.tabIcons.size());
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(NonNullList<ItemStack> itmList) {
            super.displayAllRelevantItems(itmList);

            itmList.sort((itm1, itm2) -> {
                if( itm1 != null && itm1.getItem() instanceof ItemTurret ) {
                    return itm2 != null && itm2.getItem() instanceof ItemTurret ? 0 : -2;
                } else if( itm2 != null && itm2.getItem() instanceof ItemTurret ) {
                    return 2;
                } else if( itm1 != null && itm1.getItem() instanceof ItemAmmo ) {
                    return itm2 != null && itm2.getItem() instanceof ItemAmmo ? 0 : -1;
                } else if( itm2 != null && itm2.getItem() instanceof ItemAmmo ) {
                    return 1;
                }

                return 0;
            });
        }
    };

    public static final CreativeTabs MISC = new CreativeTabs(TmrConstants.ID + ":misc") {
        @Nonnull
        private ItemStack currTabIcon = ItemStackUtils.getEmpty();

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            if( !ItemStackUtils.isValid(this.currTabIcon) ) {
                this.currTabIcon = new ItemStack(ItemRegistry.TURRET_CONTROL_UNIT, 1);
            }

            return this.currTabIcon;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(NonNullList<ItemStack> itmList) {
            super.displayAllRelevantItems(itmList);

            itmList.sort((itm1, itm2) -> {
                if( itm1 != null && itm1.getItem() instanceof ItemBlock ) {
                    return itm2 != null && itm2.getItem() instanceof ItemBlock ? 0 : -2;
                } else if( itm2 != null && itm2.getItem() instanceof ItemBlock ) {
                    return 2;
                } else if( itm1 != null && itm1.getItem() instanceof ItemRepairKit ) {
                    return itm2 != null && itm2.getItem() instanceof ItemRepairKit ? 0 : 1;
                } else if( itm2 != null && itm2.getItem() instanceof ItemRepairKit ) {
                    return -1;
                }

                return 0;
            });
        }
    };

    public static final CreativeTabs UPGRADES = new CreativeTabs(TmrConstants.ID + ":upgrades") {
        private NonNullList<ItemStack> tabIcons = null;

        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return getIconItemStack();
        }

        @Override
        @SideOnly(Side.CLIENT)
        @Nonnull
        public ItemStack getIconItemStack() {
            if( this.tabIcons == null ) {
                this.tabIcons = NonNullList.create();
                ItemRegistry.TURRET_UPGRADES.forEach((rl, item) -> item.getSubItems(this, this.tabIcons));
            }

            return this.tabIcons.get((int) (System.currentTimeMillis() / 4250L) % this.tabIcons.size());
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(NonNullList<ItemStack> itmList) {
            super.displayAllRelevantItems(itmList);

            sortItemsByName(itmList);
            sortItemsBySubItems(itmList, this);
        }
    };

    private static void sortItemsByName(NonNullList<ItemStack> items) {
        items.sort(ITM_NAME_COMP);
    }

    private static void sortItemsBySubItems(final NonNullList<ItemStack> items, final CreativeTabs tab) {
        items.sort(new ItemSubComparator(tab));
    }

    private static class ItemNameComparator implements Comparator<ItemStack>
    {
        @Override
        public int compare(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
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
        public int compare(@Nonnull ItemStack o1, @Nonnull ItemStack o2) {
            if( o1.getItem() != o2.getItem() ) {
                return -1;
            }

            NonNullList<ItemStack> subItms = NonNullList.create();
            o1.getItem().getSubItems(this.tab, subItms);

            return Integer.compare(getStackIndexInList(o1, subItms), getStackIndexInList(o2, subItms));
        }

        private static int getStackIndexInList(@Nonnull ItemStack stack, List<ItemStack> stackArray) {
            for( ItemStack stackElem : stackArray ) {
                if( ItemStackUtils.areEqual(stack, stackElem, true) ) {
                    return stackArray.indexOf(stackElem);
                }
            }

            return -1;
        }
    }
}
