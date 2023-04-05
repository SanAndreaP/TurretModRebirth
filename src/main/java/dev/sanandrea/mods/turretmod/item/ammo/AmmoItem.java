/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.ammo;

import com.google.common.base.Strings;
import dev.sanandrea.mods.turretmod.api.ammo.IAmmunition;
import dev.sanandrea.mods.turretmod.item.TmrItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class AmmoItem
        extends Item
{
    public final ResourceLocation ammoId;
    private      IAmmunition      ammoCache;

    public AmmoItem(ResourceLocation ammoId) {
        super(new Properties().tab(TmrItemGroups.TURRETS));
        this.ammoId = ammoId;
    }

    public IAmmunition getAmmo() {
        if( this.ammoCache == null ) {
            this.ammoCache = AmmunitionRegistry.INSTANCE.get(this.ammoId);
        }

        return this.ammoCache;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        IAmmunition ammo = this.getAmmo();
        ammo.appendHoverText(stack, world, tooltip, flag);

        if( flag.isAdvanced() ) {
            tooltip.add(new StringTextComponent("aid: " + ammo.getId()).withStyle(TextFormatting.DARK_GRAY));
            String subtype = AmmunitionRegistry.INSTANCE.getSubtype(stack);
            if( !Strings.isNullOrEmpty(subtype) ) {
                tooltip.add(new StringTextComponent("subtype: " + subtype).withStyle(TextFormatting.DARK_GRAY));
            }
        }

        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if( this.allowdedIn(group) ) {
            String[] subtypes = this.getAmmo().getSubtypes();

            if( subtypes.length > 0 ) {
                Arrays.stream(subtypes).forEach(s -> items.add(AmmunitionRegistry.INSTANCE.setSubtype(new ItemStack(this, 1), s)));
            } else {
                super.fillItemCategory(group, items);
            }
        }
    }
}