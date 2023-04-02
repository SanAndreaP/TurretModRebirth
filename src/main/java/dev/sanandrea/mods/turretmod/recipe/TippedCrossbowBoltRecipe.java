/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.recipe;

import dev.sanandrea.mods.turretmod.item.ammo.AmmunitionRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.Ammunitions;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TippedCrossbowBoltRecipe
        extends SpecialRecipe
{
    public static final IRecipeSerializer<?> SERIALIZER = new SpecialRecipeSerializer<>(TippedCrossbowBoltRecipe::new);

    public TippedCrossbowBoltRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory inventory, @Nonnull World level) {
        if( this.canCraftInDimensions(inventory.getWidth(), inventory.getHeight()) ) {
            for( int col = 0; col < 3; ++col ) {
                for( int row = 0; row < 3; ++row ) {
                    if( !isItemValid(inventory, row, col) ) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean isItemValid(CraftingInventory inventory, int row, int col) {
        ItemStack iStack = inventory.getItem(col + row * 3);
        if( iStack.isEmpty() ) {
            return false;
        }

        if( col == 1 && row == 1 ) {
            return iStack.getItem() == Items.LINGERING_POTION;
        } else {
            return AmmunitionRegistry.INSTANCE.get(iStack).equals(Ammunitions.BOLT);
        }
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingInventory inventory) {
        ItemStack itemstack = inventory.getItem(4);
        if( itemstack.getItem() != Items.LINGERING_POTION ) {
            return ItemStack.EMPTY;
        } else {
            return AmmunitionRegistry.INSTANCE.getItem(Ammunitions.TIPPED_BOLT,
                                                       Objects.requireNonNull(PotionUtils.getPotion(itemstack).getRegistryName()).toString(),
                                                       8);
        }
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 3 && height == 3;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
