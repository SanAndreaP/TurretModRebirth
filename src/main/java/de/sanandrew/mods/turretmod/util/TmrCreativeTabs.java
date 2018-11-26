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
import de.sanandrew.mods.turretmod.client.util.ResourceOrderer;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class TmrCreativeTabs
{
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
            ResourceOrderer.orderItems(itmList);
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
            ResourceOrderer.orderItems(itmList);
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
            ResourceOrderer.orderItems(itmList);
        }
    };
}
