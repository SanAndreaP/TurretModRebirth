/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.electrolytegen;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public interface IElectrolyteManager
{
    List<IElectrolyteRecipe> getFuels(World world);

    IElectrolyteRecipe getFuel(World world, ResourceLocation id);

    IElectrolyteRecipe getFuel(World world, ItemStack stack);

//    boolean registerFuel(IElectrolyteRecipe recipe);
//
//    void removeFuel(ResourceLocation id);
}
