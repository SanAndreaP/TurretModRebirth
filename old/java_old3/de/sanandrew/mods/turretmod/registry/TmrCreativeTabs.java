/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.util.ResourceOrderer;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class TmrCreativeTabs
{
    public static final ItemGroup TURRETS = new ItemGroup(TmrConstants.ID + ":turrets") {
        private NonNullList<ItemStack> tabIcons = null;

        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack getIcon() {
            return createIcon();
        }

        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            if( this.tabIcons == null ) {
                this.tabIcons = NonNullList.create();
                ItemRegistry.TURRET_PLACERS.forEach((rl, item) -> this.tabIcons.add(new ItemStack(item)));
            }

            return this.tabIcons.get((int) (System.currentTimeMillis() / 4250L) % this.tabIcons.size());
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void fill(@Nonnull NonNullList<ItemStack> items) {
            super.fill(items);
            items.sort(ResourceOrderer.getComparator());
        }
    };

    public static final ItemGroup MISC = new ItemGroup(TmrConstants.ID + ":misc") {
        private ItemStack currTabIcon = ItemStack.EMPTY;

        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            if( !ItemStackUtils.isValid(this.currTabIcon) ) {
                this.currTabIcon = new ItemStack(ItemRegistry.TURRET_CONTROL_UNIT, 1);
            }

            return this.currTabIcon;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void fill(@Nonnull NonNullList<ItemStack> items) {
            super.fill(items);
            items.sort(ResourceOrderer.getComparator());
        }
    };

    public static final ItemGroup UPGRADES = new ItemGroup(TmrConstants.ID + ":upgrades") {
        private NonNullList<ItemStack> tabIcons = null;

        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack getIcon() {
            return createIcon();
        }

        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            if( this.tabIcons == null ) {
                this.tabIcons = NonNullList.create();
                ItemRegistry.TURRET_UPGRADES.forEach((rl, item) -> this.tabIcons.add(new ItemStack(item)));
            }

            return this.tabIcons.get((int) (System.currentTimeMillis() / 4250L) % this.tabIcons.size());
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void fill(@Nonnull NonNullList<ItemStack> items) {
            super.fill(items);
            items.sort(ResourceOrderer.getComparator());
        }
    };
}
