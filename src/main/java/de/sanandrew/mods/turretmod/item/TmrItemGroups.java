/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.ResourceOrderer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class TmrItemGroups
{
    private TmrItemGroups() { }

    public static final ItemGroup TURRETS = new ItemGroup(TmrConstants.ID + ":turrets") {
        private NonNullList<ItemStack> tabIcons = null;

        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack getIconItem() {
            return makeIcon();
        }

        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            if( this.tabIcons == null ) {
                this.tabIcons = NonNullList.create();
                ItemRegistry.TURRET_PLACERS.forEach((rl, item) -> this.tabIcons.add(new ItemStack(item)));
            }

            return this.tabIcons.get((int) (System.currentTimeMillis() / 4250L) % this.tabIcons.size());
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void fillItemList(@Nonnull NonNullList<ItemStack> items) {
            super.fillItemList(items);
            items.sort(ResourceOrderer.getComparator());
        }
    };

    public static final ItemGroup MISC = new ItemGroup(TmrConstants.ID + ":misc") {
        private ItemStack currTabIcon = ItemStack.EMPTY;

        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            if( !ItemStackUtils.isValid(this.currTabIcon) ) {
                this.currTabIcon = new ItemStack(ItemRegistry.TURRET_CONTROL_UNIT, 1);
            }

            return this.currTabIcon;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void fillItemList(@Nonnull NonNullList<ItemStack> items) {
            super.fillItemList(items);
            items.sort(ResourceOrderer.getComparator());
        }
    };

    public static final ItemGroup UPGRADES = new ItemGroup(TmrConstants.ID + ":upgrades") {
        private NonNullList<ItemStack> tabIcons = null;

        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack getIconItem() {
            return makeIcon();
        }

        @Nonnull
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            if( this.tabIcons == null ) {
                this.tabIcons = NonNullList.create();
                ItemRegistry.TURRET_UPGRADES.forEach((rl, item) -> this.tabIcons.add(new ItemStack(item)));
            }

            return this.tabIcons.get((int) (System.currentTimeMillis() / 4250L) % this.tabIcons.size());
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void fillItemList(@Nonnull NonNullList<ItemStack> items) {
            super.fillItemList(items);
            items.sort(ResourceOrderer.getComparator());
        }
    };
}
